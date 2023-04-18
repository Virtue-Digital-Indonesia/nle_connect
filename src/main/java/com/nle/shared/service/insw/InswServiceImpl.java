package com.nle.shared.service.insw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nle.config.prop.AppProperties;
import com.nle.constant.AppConstant;
import com.nle.exception.BadRequestException;
import com.nle.io.entity.*;
import com.nle.io.repository.*;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.InswToken;
import com.nle.io.entity.Item;
import com.nle.io.entity.booking.BookingDetailUnloading;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.InswTokenRepository;
import com.nle.io.repository.ItemRepository;

import com.nle.io.repository.booking.BookingDetailUnloadingRepository;
import com.nle.shared.dto.insw.InswSyncDataDTO;
import com.nle.shared.service.fleet.InswShippingService;
import com.nle.shared.service.item.ItemTypeService;
import com.nle.ui.model.response.InswShippingResponse;
import com.nle.ui.model.response.ItemResponse;
import com.nle.ui.model.response.ItemTypeResponse;
import com.nle.ui.model.response.insw.*;
import com.nle.util.DateUtil;
import com.nle.util.NleUtil;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class InswServiceImpl implements InswService{

    private final AppProperties appProperties;
    private final InswTokenRepository inswTokenRepository;

    private final ItemTypeService itemTypeService;
    private final ItemRepository itemRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final DepoFleetRepository depoFleetRepository;
    private final InswShippingService inswShippingService;
    private final BookingDetailUnloadingRepository bookingDetailUnloadingRepository;
    private final GateMoveRepository gateMoveRepository;

    @Override
    public InswResponse getBolData(String bolNumber, Long depoId) {
        //Validasi depo id
        if (depoId == null)
            throw new BadRequestException("Can't Find Depo ID!");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findById(depoId);
        if (depoOwnerAccount.isEmpty())
            throw new BadRequestException("Can't Find Depo!");
        DepoOwnerAccount doa = depoOwnerAccount.get();

        //Get data from insw and convert to nle response
        InswResponse dataResponse = this.getBolDataInsw(bolNumber).getData();
        InswResponse inswResponse = new InswResponse();
        BeanUtils.copyProperties(dataResponse, inswResponse);

        //Get data container as list
        List<ContainerResponse> containerResponseList = new ArrayList<>();
        List<ContainerResponse> containerResponse = dataResponse.getContainer();
        for (ContainerResponse container: containerResponse) {
            containerResponseList.add(this.convertContainerToResponse(container, doa, inswResponse.getNoBL(), inswResponse.getShippingLine()));
        }

        inswResponse.setContainer(containerResponseList);

        InswShippingResponse inswShippingResponse = inswShippingService.searchShippingCode(inswResponse.getShippingLine());
        inswResponse.setShippingFleet(inswShippingResponse);

        return inswResponse;
    }

    @Override
    @Scheduled(cron = "${app.scheduler.insw-sync-cron}")
    public List<InswSyncDataDTO> syncInsw() {

        List<GateMove> gateMoveList = gateMoveRepository.findAllByStatusInsw(AppConstant.Status.SUBMITTED);
        List<InswSyncDataDTO> listResponse = new ArrayList<>();
        for (GateMove gateMove : gateMoveList) {

            //Get data from method convertToInswSyncDataDto
            InswSyncDataDTO inswDTO = this.convertToInswSyncDataDto(gateMove);

            //method for Send data to insw
            inswDTO.setStatusFeedback(this.sendToInsw(inswDTO));
            listResponse.add(inswDTO);

            //gate move yang berhasil dikirim ke insw akan dicatat tanggal kirimnya
            if (inswDTO.getStatusFeedback().equalsIgnoreCase("Success!")){
                gateMoveRepository.updateGateMoveStatusByInsw(gateMove.getId(), LocalDateTime.now());
            }
        }
        return listResponse;
    }

    private InswSyncDataDTO convertToInswSyncDataDto(GateMove gateMove) {
        InswSyncDataDTO inswSyncDataDTO = new InswSyncDataDTO();
        BeanUtils.copyProperties(gateMove, inswSyncDataDTO);
        inswSyncDataDTO.setContainerNumber(gateMove.getContainer_number());
        inswSyncDataDTO.setDateManufacturing(gateMove.getDate_manufacturer());
        inswSyncDataDTO.setDeliveryPort(gateMove.getDelivery_port());
        inswSyncDataDTO.setDischargePort(gateMove.getDischarge_port());
        inswSyncDataDTO.setDriverName(gateMove.getDriver_name());
        inswSyncDataDTO.setFleetManager(gateMove.getFleet_manager());
        inswSyncDataDTO.setIsoCode(gateMove.getIso_code());
        inswSyncDataDTO.setMaxGross(gateMove.getMax_gross());
        inswSyncDataDTO.setProcessType(gateMove.getProcess_type());
        inswSyncDataDTO.setTransportNumber(gateMove.getTransport_number());
        inswSyncDataDTO.setTxDate(gateMove.getTx_date());

        if (gateMove.getGateMoveType().equalsIgnoreCase(NleUtil.GATE_IN)) {
            inswSyncDataDTO.setActivity("GI");
            inswSyncDataDTO.setBlDate(gateMove.getTx_date());
            inswSyncDataDTO.setBlNumber(gateMove.getOrder_number());
        }
        else if (gateMove.getGateMoveType().equalsIgnoreCase(NleUtil.GATE_OUT)) {
            inswSyncDataDTO.setActivity("GO");
            inswSyncDataDTO.setDoDate(gateMove.getTx_date());
            inswSyncDataDTO.setDoNumber(gateMove.getOrder_number());
        }

        inswSyncDataDTO.setDepoId(gateMove.getDepoOwnerAccount().getId());
        inswSyncDataDTO.setGateMoveData(NleUtil.convertFromGateMove(gateMove));

        return inswSyncDataDTO;
    }

    private ContainerResponse convertContainerToResponse(ContainerResponse containerResponse, DepoOwnerAccount doa, String noBl, String code) {
        ContainerResponse response = new ContainerResponse();
        BeanUtils.copyProperties(containerResponse, response);
        Long depoId = doa.getId();

        //Get fleet from shippingline code
        Optional<DepoFleet> fleet = depoFleetRepository.getFleetInDepo(doa.getCompanyEmail(), code);

        if (!fleet.isPresent() || fleet.isEmpty()) {
            fleet = depoFleetRepository.getFleetInDepo(doa.getCompanyEmail(),"SSI");
            if (!fleet.isPresent() || fleet.isEmpty()){
                response.setItemResponse(null);
                return response;
            }
        }
        DepoFleet getFleet = fleet.get();

        //Get item type base on size and type
        List<ItemTypeResponse> itemTypeResponseList = itemTypeService.getFromIsoCode(containerResponse.getSize(), containerResponse.getType());
        if (itemTypeResponseList.isEmpty())
            response.setItemResponse(null);

        try {
            for (ItemTypeResponse getItemType: itemTypeResponseList) {
                List<Item> getItemOfId = itemRepository.getItemOfShipping(depoId,getItemType.getId(),getFleet.getId());
                if (getItemOfId.isEmpty()){
                    response.setItemResponse(null);
                } else {
                    Item item = getItemOfId.get(getItemOfId.size()-1);
                    ItemResponse itemResponse = new ItemResponse();
                    itemResponse.setId(item.getId());

                    ItemTypeResponse itemTypeResponse = new ItemTypeResponse();
                    BeanUtils.copyProperties(item.getItem_name(), itemTypeResponse);

                    itemResponse.setItem_name(itemTypeResponse);
                    itemResponse.setPrice(item.getPrice());
                    itemResponse.setSku(item.getSku());
                    itemResponse.setDescription(item.getDescription());
                    itemResponse.setType(item.getType());
                    itemResponse.setStatus(item.getStatus());
                    itemResponse.setDeleted(item.getDeleted());
                    response.setItemResponse(itemResponse);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        //get status container
        if (response.getItemResponse() != null){
            List<BookingDetailUnloading> bookingDetailUnloadings = bookingDetailUnloadingRepository.getValidateContainer(
                                                                    noBl,
                                                                    containerResponse.getNoContainer(),
                                                                    response.getItemResponse().getId(),
                                                                    depoId);
            if (bookingDetailUnloadings.isEmpty()){
                response.setActiveStatus(true);
            } else {
                response.setActiveStatus(false);
            }
        }

        return response;
    }

    public String sendToInsw(InswSyncDataDTO inswSyncDataDTO){
        String inswUrl = "https://api-test.insw.go.id/api/v1/services/transaksi/dosp2/send-container-status";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        String bearerToken        = null;
        Optional<InswToken> inswToken = Optional.ofNullable(inswTokenRepository.findByActiveStatus(AppConstant.VerificationStatus.ACTIVE));

        //Validate if token expired
        if (inswToken.isEmpty()){
            bearerToken = this.createToken();
        } else {
            InswToken getInswToken = inswToken.get();
            if (checkToken(getInswToken.getExpiryDate())){
                bearerToken = getInswToken.getAccessToken();
            } else {
                inswTokenRepository.updateStatus(AppConstant.VerificationStatus.INACTIVE, getInswToken.getId());
                bearerToken = this.createToken();
            }
        }

        httpHeaders.add("Authorization", "Bearer " + bearerToken);
        httpHeaders.add("Content-Type", "application/json");

        //Convert element insw from gatemove
        JSONObject sendParam = new JSONObject();
        try {
            sendParam.put("activity", checkNullString(inswSyncDataDTO.getActivity()));
            sendParam.put("idPlatform", checkNullString(inswSyncDataDTO.getIdPlatform()));
            sendParam.put("carrier", checkNullString(inswSyncDataDTO.getCarrier()));
            sendParam.put("clean", (inswSyncDataDTO.getClean() == "0")?"Yes":"No");
            sendParam.put("condition", checkNullString(inswSyncDataDTO.getCondition()));
            sendParam.put("containerNumber", checkNullString(inswSyncDataDTO.getContainerNumber()));
            sendParam.put("customer", checkNullString(inswSyncDataDTO.getCustomer()));
            sendParam.put("dateManufacturing", this.convertDateManufacturing(inswSyncDataDTO.getDateManufacturing()));
            sendParam.put("deliveryPort", checkNullString(inswSyncDataDTO.getDeliveryPort()));
            sendParam.put("depot", inswSyncDataDTO.getDepot());
            sendParam.put("dischargePort", checkNullString(inswSyncDataDTO.getDischargePort()));
            sendParam.put("driverName", checkNullString(inswSyncDataDTO.getDriverName()));
            sendParam.put("fleetManager", checkNullString(inswSyncDataDTO.getFleetManager()));
            sendParam.put("grade", checkNullString(inswSyncDataDTO.getGrade()));
            sendParam.put("isoCode", checkNullString(inswSyncDataDTO.getIsoCode()));
            sendParam.put("maxGross", inswSyncDataDTO.getMaxGross());
            sendParam.put("blNumber", checkNullString(inswSyncDataDTO.getBlNumber()));
            sendParam.put("doNumber", checkNullString(inswSyncDataDTO.getDoNumber()));
            sendParam.put("payload", inswSyncDataDTO.getPayload());
            sendParam.put("processType", NleUtil.convertProcessType(inswSyncDataDTO.getProcessType()));
            sendParam.put("remark", checkNullString(inswSyncDataDTO.getRemarks()));
            sendParam.put("tare", inswSyncDataDTO.getTare());
            sendParam.put("transportNumber", checkNullString(inswSyncDataDTO.getTransportNumber()));
            sendParam.put("txDate", DateUtil.getDateOfPattern(inswSyncDataDTO.getTxDate()));
            sendParam.put("vessel", checkNullString(inswSyncDataDTO.getVessel()));
            sendParam.put("voyage", checkNullString(inswSyncDataDTO.getVoyage()));
            sendParam.put("amount", (inswSyncDataDTO.getAmount() != null)?inswSyncDataDTO.getAmount():0);
        } catch (JSONException e){
            e.printStackTrace();
        }

        JSONObject sendData = new JSONObject();
        try {
            sendData.put("data", sendParam);
        } catch (JSONException e){
            e.printStackTrace();
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        HttpEntity<String> request = new HttpEntity<String>(sendData.toString(), httpHeaders);
        String result = restTemplate.postForObject(inswUrl, request, String.class);

        //get response from insw status
        String feedBackMessage = null;
        try {
            JsonNode root = objectMapper.readTree(result);
            feedBackMessage = root.path("message").asText();
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        System.out.println(result);
        return feedBackMessage;
    }

    public String convertDateManufacturing(String date){
        if (date == null){
            return "";
        }

        String patternString = "MMM yyyy";
        String patternDate = "yyyy-MM";
        String dateStr = null;
        DateTimeFormatter formatterString = DateTimeFormatter.ofPattern(patternString);
        try {
            formatterString.parse(date);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternString);
            Date sDate = null;
            try {
                sDate = simpleDateFormat.parse(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            simpleDateFormat = new SimpleDateFormat(patternDate);
            dateStr = simpleDateFormat.format(sDate);
        } catch (DateTimeParseException e) {
            dateStr = date;
        }
        return dateStr;
    }

    public String checkNullString(String stringData){
        if (stringData == null){
            return "";
        }
        return stringData;
    }


    public DataResponse getBolDataInsw(String bolNumber) {
        String curlLocUrl = "https://api-test.insw.go.id/api/v2/services/transaksi/do-sp2/container-asdeki?nomor_bl="+bolNumber;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders   = new HttpHeaders();
        String bearerToken        = null;
        Optional<InswToken> inswToken = Optional.ofNullable(inswTokenRepository.findByActiveStatus(AppConstant.VerificationStatus.ACTIVE));

        //Validate if token expired
        if (inswToken.isEmpty()){
            bearerToken = this.createToken();
        } else {
            InswToken getInswToken = inswToken.get();
            if (checkToken(getInswToken.getExpiryDate())){
                bearerToken = getInswToken.getAccessToken();
            } else {
                inswTokenRepository.updateStatus(AppConstant.VerificationStatus.INACTIVE, getInswToken.getId());
                bearerToken = this.createToken();
            }
        }

        httpHeaders.add("Authorization", "Bearer " + bearerToken);
        httpHeaders.add("Content-Type", "application/json");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<DataResponse> response = null;
        try {
            response = restTemplate.exchange(curlLocUrl,
                    HttpMethod.GET,
                    entity, DataResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.getBody();
    }

    private Boolean checkToken(LocalDateTime expiredDate) {
            // check expired token
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), expiredDate);
            if (seconds < 0) {
                return false;
            }
            return true;
    }

    public String createToken(){
        String curlLocUrl = "https://api-test.insw.go.id/login-svc/oauth2/token";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        String clientId     = appProperties.getInsw().getClientId();
        String clientSecret = appProperties.getInsw().getClientSecret();
        String getCode      = getNewCode();

        httpHeaders.add("Content-Type", "application/json");

        JSONObject paramBody = new JSONObject();
        paramBody.put("grant_type","authorization_code");
        paramBody.put("code", getCode);
        paramBody.put("client_id", clientId);
        paramBody.put("client_secret", clientSecret);

        final ObjectMapper objectMapper = new ObjectMapper();

        HttpEntity<String> request = new HttpEntity<>(paramBody.toString(), httpHeaders);
        String result = restTemplate.postForObject(curlLocUrl, request, String.class);
        try {
            JsonNode root = objectMapper.readTree(result);
            String accessToken = root.path("access_token").asText();
            Long expiresIn    = root.path("expires_in").asLong();

            // plus expires in(second) before expired
            LocalDateTime expiredDate = LocalDateTime.now().plusSeconds(expiresIn);
            // Create new token data
            InswToken inswToken = new InswToken();
            inswToken.setRefreshToken(root.path("refresh_token").asText());
            inswToken.setTokenType(root.path("token_type").asText());
            inswToken.setAccessToken(accessToken);
            inswToken.setExpiryDate(expiredDate);
            inswToken.setActiveStatus(AppConstant.VerificationStatus.ACTIVE);
            inswTokenRepository.save(inswToken);

            return accessToken;
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return "failed";
    }

    public String getNewCode(){
        String curlLocUrl = "https://api-test.insw.go.id/login-svc/oauth2/authorize";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        String clientId     = appProperties.getInsw().getClientId();
        String provisionKey = appProperties.getInsw().getProvisionKey();
        String redirectUri  = appProperties.getInsw().getRedirectUri();

        httpHeaders.add("Content-Type", "application/json");

        JSONObject paramBody = new JSONObject();
        paramBody.put("client_id", clientId);
        paramBody.put("response_type", "code");
        paramBody.put("scope", "session");
        paramBody.put("provision_key", provisionKey);
        paramBody.put("authenticated_userid", "virtuedigital");
        paramBody.put("redirect_uri", redirectUri);

        final ObjectMapper objectMapper = new ObjectMapper();

        HttpEntity<String> request = new HttpEntity<>(paramBody.toString(), httpHeaders);
        String result = restTemplate.postForObject(curlLocUrl, request, String.class);
        try {
            JsonNode root = objectMapper.readTree(result);
            String getPath = root.path("redirect_uri").asText();
            return getPath.substring(29);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return "failed";
    }
}
