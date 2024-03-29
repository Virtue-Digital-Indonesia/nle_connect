package com.nle.ui.controller.gavemove;

import com.nle.constant.enums.GateMoveSource;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.CreateGateMoveReqDTO;
import com.nle.ui.model.request.UpdateGateMoveReqDTO;
import com.nle.ui.model.request.search.GateMoveSearchRequest;
import com.nle.ui.model.response.CreatedGateMoveResponseDTO;
import com.nle.ui.model.response.GateMoveResponseDTO;
import com.nle.ui.model.response.UpdatedGateMoveResponseDTO;
import com.nle.io.repository.dto.MoveStatistic;
import com.nle.io.repository.dto.ShippingLineStatistic;
import com.nle.shared.service.gatemove.GateMoveService;
import com.nle.ui.model.response.count.CountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/gate-moves")
@RequiredArgsConstructor
public class GateMoveController {
    private final Logger log = LoggerFactory.getLogger(GateMoveController.class);

    private final GateMoveService gateMoveService;

    @Operation(description = "Create new Gate In / Gate Out container", operationId = "createGateMove", summary = "Create new Gate In / Gate Out container")
    @PostMapping
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<CreatedGateMoveResponseDTO> createGateMove(@Valid @RequestBody CreateGateMoveReqDTO gateMoveCreateDTO)
            throws URISyntaxException {
        log.debug("REST request to save GateMove : {}", gateMoveCreateDTO);
        CreatedGateMoveResponseDTO createdGateMove = gateMoveService.createGateMove(gateMoveCreateDTO, GateMoveSource.API);
        return ResponseEntity
                .created(new URI("/api/gate-moves/" + createdGateMove.getId()))
                .body(createdGateMove);
    }

    @Operation(description = "Upload Gate In / Gate Out photo", operationId = "uploadGateMoveFile", summary = "Upload Gate In / Gate Out photo")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<Void> uploadGateMoveFile(@RequestParam("gateMoveId") Long gateMoveId, @RequestPart("files") MultipartFile files[]) {
        log.debug("REST request to upload {} files", files.length);
        gateMoveService.uploadFile(files, gateMoveId);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Get list of gate move with paging", operationId = "getAllGateMoves", summary = "Get list of gate move with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter(in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter(in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping
    public ResponseEntity<PagingResponseModel<GateMoveResponseDTO>> findAllGateMoves(
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
            @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(gateMoveService.getAllGateMove(pageable));
    }

    @Operation(description = "Update date Gate Move", operationId = "updateGateMove", summary = "Update date Gate Move")
    @SecurityRequirement(name = "nleapi")
    @PutMapping
    public ResponseEntity<UpdatedGateMoveResponseDTO> updateGateMove(@Valid @RequestBody UpdateGateMoveReqDTO gateMoveDTO) {
        return ResponseEntity.ok(gateMoveService.updateGateMove(gateMoveDTO, GateMoveSource.API));
    }

    @Operation(description = "Count total GateMove by type", operationId = "countTotalGateMoveByType", summary = "Count total GateMove by type")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "count-gate-move-type")
    public ResponseEntity<List<MoveStatistic>> countTotalGateMoveByType() {
        return ResponseEntity.ok(gateMoveService.countTotalGateMoveByType());
    }

    @Operation(description = "Count total GateMove by shipping line", operationId = "countTotalGateMoveByShippingLine", summary = "Count total GateMove by shipping line")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "count-shipping-line")
    public ResponseEntity<List<ShippingLineStatistic>> countTotalGateMoveByShippingLine() {
        return ResponseEntity.ok(gateMoveService.countTotalGateMoveByShippingLine());
    }

    @Operation(description = "Count total GateMove last days", operationId = "countTotalGateMoveByDuration", summary = "Count total GateMove by Duration")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "count-duration")
    public ResponseEntity<CountResponse> countTotalGateMoveByDuration(@RequestParam Long duration) {
        return ResponseEntity.ok(gateMoveService.countTotalGateMoveByDuration(duration));
    }

    @Operation(description = "Count total GateMove last days with fleet manager", operationId = "countTotalGateMoveByDurationWithFleetManager", summary = "Count total GateMove last days with fleet manager")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/count-duration/fleet-manager")
    public ResponseEntity<CountResponse> countTotalGateMoveByDurationWithFleetManager(@RequestParam Long duration) {
        return ResponseEntity.ok(gateMoveService.countTotalGateMoveByDurationWithFleetManager(duration));
    }

    @Operation(description = "global search Gate Move by Query",
            operationId = "searchGateMove",
            summary = "global search gate move by query")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter (in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @PostMapping(value = "/search")
    public ResponseEntity<PagingResponseModel<GateMoveResponseDTO>> searchGateMove(
            @RequestBody GateMoveSearchRequest request,
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(gateMoveService.searchByCondition(pageable, request));
    }

    @Operation(description = "multiple create gate move", operationId = "multipleGateMove", summary = "multiple create gate move from list")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/multiple")
    public ResponseEntity<List<CreatedGateMoveResponseDTO>> multipleGateMove(@RequestBody List<CreateGateMoveReqDTO> listCreate) {
        return ResponseEntity.ok(gateMoveService.multipleCreateGateMove(listCreate));
    }

}