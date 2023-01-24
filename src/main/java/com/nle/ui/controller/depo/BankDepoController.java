package com.nle.ui.controller.depo;

import com.nle.shared.service.bankDepo.BankDepoService;
import com.nle.ui.model.request.BankDepoRequest;
import com.nle.ui.model.response.BankDepoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/depo/bank")
public class BankDepoController {

    private final BankDepoService bankDepoService;

    @Operation(description = "get all bank for depo", operationId = "getAllBankDepo", summary = "get all bank for depo")
    @SecurityRequirement(name = "nleapi")
    @GetMapping()
    public ResponseEntity<List<BankDepoResponse>> getAllBankDepo() {
        return ResponseEntity.ok(bankDepoService.getAllBankDepo());
    }

    @Operation(description = "change bank for depo", operationId = "changeBankCode", summary = "change bank for depo")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/change")
    public ResponseEntity<BankDepoResponse> changetBankCode(@RequestBody BankDepoRequest request) {
        return ResponseEntity.ok(bankDepoService.changeBankCode(request));
    }
}
