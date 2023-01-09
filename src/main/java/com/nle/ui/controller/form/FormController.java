package com.nle.ui.controller.form;

import java.io.ByteArrayOutputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nle.shared.service.form.FormService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    @Operation(description = "Export invoice by booking id", operationId = "exportInvoiceByBookingId", summary = "Export invoice by booking id")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/invoice/{id}")
    public ResponseEntity<ByteArrayResource> exportInvoiceByBookingId(@PathVariable Long id) {
        ByteArrayOutputStream reportByte = formService.exportInvoice(id);
        String filename = "Invoice_" + String.valueOf(id) + ".pdf";
        ByteArrayResource resource = new ByteArrayResource(reportByte.toByteArray());
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
                filename);
        header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return ResponseEntity.status(HttpStatus.OK)
                .headers(header)
                .contentLength(reportByte.size())
                .body(resource);
    }

    @Operation(description = "Export bon by booking id", operationId = "exportBonByBookingId", summary = "Export bon by booking id")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/bon/{id}")
    public ResponseEntity<ByteArrayResource> exportBonByBookingId(@PathVariable Long id) {
        ByteArrayOutputStream reportByte = formService.exportBon(id);
        String filename = "Bon_" + String.valueOf(id) + ".pdf";
        ByteArrayResource resource = new ByteArrayResource(reportByte.toByteArray());
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
                filename);
        header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return ResponseEntity.status(HttpStatus.OK)
                .headers(header)
                .contentLength(reportByte.size())
                .body(resource);
    }
}
