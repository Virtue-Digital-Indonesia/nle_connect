package com.nle.shared.service;

import com.nle.config.prop.AppProperties;
import com.nle.exception.CommonException;
import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.shared.dto.verihubs.VerihubsSendDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OTPService { 

    private final AppProperties appProperties;
    private final String HEADER_JSON ="application/json";

    public VerihubsResponseDTO sendOTP(String phoneNumber) {

        String phone = "";

        if (phoneNumber.startsWith("+62"))
            phone = phoneNumber;
        else {
            phone = "+62" + phoneNumber.substring(1);
        }

        VerihubsSendDTO sendDTO = new VerihubsSendDTO();
        sendDTO.setTime_limit("300");
        sendDTO.setMsisdn(phone);

        try {
            RestTemplate restTemplate = new RestTemplate();
            URI baseUrl = new URI("https://api.verihubs.com/v1/otp/send");
            HttpHeaders headers = new HttpHeaders();
            headers.add("accept", HEADER_JSON);
            headers.add("content-type", HEADER_JSON);
            headers.add("App-ID", appProperties.getVerihubs().getAppId());
            headers.add("API-Key", appProperties.getVerihubs().getApiKey());

            HttpEntity<VerihubsSendDTO> payload = new HttpEntity<>(sendDTO, headers);
            VerihubsResponseDTO response = restTemplate.postForObject(baseUrl, payload, VerihubsResponseDTO.class);
            log.info("OTP send to " + phoneNumber);
            return response;
        } catch (URISyntaxException e) {
            log.info("otp failed send to " + phoneNumber);
        }

        VerihubsResponseDTO emptyResponse = new VerihubsResponseDTO();
        emptyResponse.setMessage("something wrong with code");
        return emptyResponse;
    }

    public ResponseEntity<String> verifOTP(String otp, String phone_number){
        String phone = "";

        if (phone_number.startsWith("+62"))
            phone = phone_number;
        else {
            phone = "+62" + phone_number.substring(1);
        }

        VerihubsSendDTO sendDTO = new VerihubsSendDTO();
        sendDTO.setMsisdn(phone);
        sendDTO.setOtp(otp);

        HttpHeaders headers = factoryHeader();

        try {
            RestTemplate restTemplate = new RestTemplate();
            URI baseUrl = new URI("https://api.verihubs.com/v1/otp/verify");
            HttpEntity<VerihubsSendDTO> payload = new HttpEntity<>(sendDTO, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, payload, String.class);
            return response;
        } catch (URISyntaxException e) {
            log.info("otp failed verify to " + phone_number);
        } catch (final HttpClientErrorException  e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new CommonException(e.getMessage());
        }
        return null;
    }

    private HttpHeaders factoryHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", HEADER_JSON);
        headers.add("content-type", HEADER_JSON);
        headers.add("App-ID", appProperties.getVerihubs().getAppId());
        headers.add("API-Key", appProperties.getVerihubs().getApiKey());
        return headers;
    }
}
