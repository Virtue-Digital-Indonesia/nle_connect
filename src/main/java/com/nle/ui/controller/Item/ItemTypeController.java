package com.nle.ui.controller.Item;

import com.nle.shared.service.item.ItemTypeService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.ItemTypeRequest;
import com.nle.ui.model.response.ItemTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/item-type")
@RequiredArgsConstructor
public class ItemTypeController {

    private final ItemTypeService itemTypeService;

    @Operation(description = "get list item type with paging", operationId = "getListItemType", summary = "get list item type with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping
    public ResponseEntity<PagingResponseModel<ItemTypeResponse>> getAllItemType (
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable
    ) {
        return ResponseEntity.ok(itemTypeService.getAllItemType(pageable));
    }

    @Operation(description = "create item type", operationId = "createItemType", summary = "create item type")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/addItemType")
    public ResponseEntity<ItemTypeResponse> createItemType(@RequestBody ItemTypeRequest request) {
        return null;
    }

}
