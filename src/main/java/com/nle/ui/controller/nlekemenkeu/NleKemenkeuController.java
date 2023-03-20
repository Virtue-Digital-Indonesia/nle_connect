package com.nle.ui.controller.nlekemenkeu;

import com.nle.shared.service.nlekemenkeu.NleKemenkeuService;
import com.nle.ui.model.JWTToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nlekemenkeu")
@RequiredArgsConstructor
public class NleKemenkeuController {
    private final NleKemenkeuService nleKemenkeuService;

    @Operation(description = "Convert token nlekemenkeu", operationId = "token", summary = "convert token nlekemenkeu to nleconnect")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/token/{token}")
    public ResponseEntity<JWTToken> getConvertUserToken(@PathVariable String token) {

        return ResponseEntity.ok(nleKemenkeuService.getConvertUserToken(token));
    }
}
