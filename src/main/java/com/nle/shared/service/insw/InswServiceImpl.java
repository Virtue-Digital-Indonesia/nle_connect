package com.nle.shared.service.insw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nle.config.prop.AppProperties;
import com.nle.constant.AppConstant;
import com.nle.exception.BadRequestException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.InswToken;
import com.nle.io.repository.InswTokenRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class InswServiceImpl implements InswService{

    private final AppProperties appProperties;
    private final InswTokenRepository inswTokenRepository;
    @Override
    public ResponseEntity<String> getBolData(String bolNumber) {
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
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(curlLocUrl,
                    HttpMethod.GET,
                    entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
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
