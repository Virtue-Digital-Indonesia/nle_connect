package com.nle.shared.service.insw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nle.io.entity.DepoOwnerAccount;
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

import java.util.Base64;

@RequiredArgsConstructor
@Service
@Transactional
public class InswServiceImpl implements InswService{
    @Override
    public ResponseEntity<String> getBolData(String bolNumber) {
        String curlLocUrl = "https://api-test.insw.go.id/api/v2/services/transaksi/do-sp2/container-asdeki?nomor_bl="+bolNumber;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders   = new HttpHeaders();
        String bearerToken        = "4BAoRI0CMsdChsiogp56w8MjO6j0QH9T";

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
}
