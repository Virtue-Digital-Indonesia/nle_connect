package com.nle.shared.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nle.config.prop.AppProperties;
import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.shared.dto.verihubs.VerihubsSendDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final ObjectMapper mapper;

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
}
