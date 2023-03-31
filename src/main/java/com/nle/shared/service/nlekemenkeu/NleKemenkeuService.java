package com.nle.shared.service.nlekemenkeu;


import com.nle.ui.model.JWTToken;
import com.nle.ui.model.response.booking.BookingCustomerResponse;

public interface NleKemenkeuService {
    BookingCustomerResponse getConvertUserToken(String token);
}
