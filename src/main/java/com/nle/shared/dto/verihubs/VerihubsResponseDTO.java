package com.nle.shared.dto.verihubs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(value = { "otp" })
@NoArgsConstructor
public class VerihubsResponseDTO{

    @JsonProperty("message")
    private String message;

    @JsonProperty("otp")
    private String otp;

    @JsonProperty("msisdn")
    private String msisdn;

    @JsonProperty("session_id")
    private String session_id;

    @JsonProperty("try_count")
    private int try_count;

    @JsonProperty("segment_count")
    private int segment_count;
}
