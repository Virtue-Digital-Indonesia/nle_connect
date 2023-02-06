package com.nle.shared.service.insw;

import org.springframework.http.ResponseEntity;

public interface InswService {
    ResponseEntity<String> getBolData(String bolNumber);
}
