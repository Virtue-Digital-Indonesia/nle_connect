package com.nle.ui.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class GenerealResponse<T> {
    private String status;
    private String message;
    private T payload;

    public GenerealResponse(String status, String message, T payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }
}
