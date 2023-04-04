package com.nle.ui.controller.nlekemenkeu;

import com.nle.shared.service.nlekemenkeu.NleKemenkeuService;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.response.booking.BookingCustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nlekemenkeu")
@RequiredArgsConstructor
public class NleKemenkeuController {
    private final NleKemenkeuService nleKemenkeuService;

    @Operation(description = "Convert token nlekemenkeu", operationId = "token", summary = "convert token nlekemenkeu to nleconnect")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/convert-token")
    public ResponseEntity<BookingCustomerResponse> getConvertUserToken(@RequestParam("token") String token) {
        return ResponseEntity.ok(nleKemenkeuService.getConvertUserToken(token));
    }
}
