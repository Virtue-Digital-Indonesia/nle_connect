package com.nle.ui.model.response.booking;

import lombok.Data;

@Data
public class BookingCustomerProfileResponse {
    private Long id;
    private String full_name;
    private String email;
    private String phone_number;
}
