package com.nle.shared.service.nlekemenkeu;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.exception.BadRequestException;
import com.nle.io.entity.BookingCustomer;
import com.nle.io.repository.BookingCustomerRepository;
import com.nle.security.AuthoritiesConstants;
import com.nle.security.jwt.TokenProvider;
import com.nle.shared.dto.UserInfoNleKemenkeuDTO;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.response.booking.BookingCustomerResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.beans.BeanUtils;
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
    public BookingCustomerResponse getConvertUserToken(String token) {
        if (token.isEmpty())
            throw new BadRequestException("Token not found!");

        UserInfoNleKemenkeuDTO userInfoNleKemenkeuDTO = this.getUserInfo(token);

        if (userInfoNleKemenkeuDTO.getEmailVerified().equalsIgnoreCase("false"))
            return new BookingCustomerResponse(null);

        Optional<BookingCustomer> bookingCustomer = bookingCustomerRepository.findByEmail(userInfoNleKemenkeuDTO.getEmail());
        if (bookingCustomer.isEmpty())
            throw new BadRequestException("Please register!");

        BookingCustomer getBookingCustomer = bookingCustomer.get();
        if (getBookingCustomer.getPhone_number().isEmpty())
            throw new BadRequestException("Not found phone number!");

        String convertToken = tokenProvider.generateManualToken(getBookingCustomer.getPhone_number(), AuthoritiesConstants.BOOKING_CUSTOMER);

        return convertToResponse(getBookingCustomer, convertToken);

    }

    private BookingCustomerResponse convertToResponse (BookingCustomer entity, String token) {
        BookingCustomerResponse response = new BookingCustomerResponse(token);
        BeanUtils.copyProperties(entity, response);
        return response;
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
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return userInfoNleKemenkeuDTO;
    }
}
