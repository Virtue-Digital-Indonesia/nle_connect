package com.nle.ui.model.response.booking;

import com.nle.ui.model.JWTToken;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookingCustomerResponse extends JWTToken {

    private Long id;
    private String phone_number;
    private String email;

    public BookingCustomerResponse(String idToken) {
        super(idToken);
    }
}
