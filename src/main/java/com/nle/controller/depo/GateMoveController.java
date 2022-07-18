package com.nle.controller.depo;

import com.nle.controller.dto.GateMoveCreateDTO;
import com.nle.service.dto.GateMoveDTO;
import com.nle.service.gatemove.GateMoveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GateMoveController {


    private final Logger log = LoggerFactory.getLogger(GateMoveController.class);

    private final GateMoveService gateMoveService;

    @Operation(description = "Create new Gate In / Gate Out container", operationId = "createGateMove", summary = "Create new Gate In / Gate Out container")
    @PostMapping("/gate-moves")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<GateMoveDTO> createGateMove(@Valid @RequestBody GateMoveCreateDTO gateMoveCreateDTO)
        throws URISyntaxException {
        log.debug("REST request to save GateMove : {}", gateMoveCreateDTO);
        GateMoveDTO createdGateMove = gateMoveService.createGateMove(gateMoveCreateDTO);
        return ResponseEntity
            .created(new URI("/api/gate-moves/" + createdGateMove.getId()))
            .body(createdGateMove);
    }
}