package com.nle.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JWTToken {
    private String idToken;

    public JWTToken(String idToken) {
        this.idToken = idToken;
    }

    @JsonProperty("access_token")
    String getIdToken() {
        return idToken;
    }

    void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
