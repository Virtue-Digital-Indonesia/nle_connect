package com.nle.shared.service.nlekemenkeu;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nle.exception.BadRequestException;
import com.nle.io.entity.BookingCustomer;
import com.nle.io.repository.BookingCustomerRepository;
import com.nle.security.AuthoritiesConstants;
import com.nle.security.jwt.TokenProvider;
import com.nle.shared.dto.UserInfoNleKemenkeuDTO;
import com.nle.ui.model.JWTToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class NleKemenkeuServiceImpl implements NleKemenkeuService{
    private final TokenProvider tokenProvider;
    private final BookingCustomerRepository bookingCustomerRepository;
    @Override
    public JWTToken getConvertUserToken(String token) {
        if (token.isEmpty())
            throw new BadRequestException("Token not found!");

        UserInfoNleKemenkeuDTO userInfoNleKemenkeuDTO = this.getUserInfo(token);

        if (userInfoNleKemenkeuDTO.getEmailVerified().equalsIgnoreCase("true")){
            Optional<BookingCustomer> bookingCustomer = bookingCustomerRepository.findByEmail(userInfoNleKemenkeuDTO.getEmail());
            if (bookingCustomer.isEmpty())
                throw new BadRequestException("Please register!");

            BookingCustomer getBookingCustomer = bookingCustomer.get();
            if (getBookingCustomer.getPhone_number().isEmpty())
                throw new BadRequestException("Not found phone number!");

            String convertToken = tokenProvider.generateManualToken(getBookingCustomer.getPhone_number(), AuthoritiesConstants.BOOKING_CUSTOMER);
            return new JWTToken(convertToken);
        }

        return new JWTToken(null);
    }

    public UserInfoNleKemenkeuDTO getUserInfo(String token){
        String urlEndPoint = "https://nlehub.kemenkeu.go.id/nle-oauth/v1/user/userinfo-token";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Authorization", "Bearer " + token);
        httpHeaders.add("Content-Type", "application/json");
        UserInfoNleKemenkeuDTO userInfoNleKemenkeuDTO = new UserInfoNleKemenkeuDTO();

        final ObjectMapper objectMapper = new ObjectMapper();

        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = null;
        try {
        response = restTemplate.exchange(urlEndPoint, HttpMethod.GET, requestEntity, String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        userInfoNleKemenkeuDTO.setEmail(root.path("email").asText());
        userInfoNleKemenkeuDTO.setEmailVerified(root.path("email_verified").asText());
        } catch (Exception e){
            e.printStackTrace();
        }
        return userInfoNleKemenkeuDTO;
    }
}
