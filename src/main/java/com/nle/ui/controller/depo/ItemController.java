package com.nle.ui.controller.depo;

import com.nle.shared.service.item.ItemService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.CreateItemRequest;
import com.nle.ui.model.response.ItemResponse;
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

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(description = "get list sales items of depo owner with paging", operationId = "getListItems", summary = "service items")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping
    public ResponseEntity<PagingResponseModel<ItemResponse>> getListItems(
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable
    ) {
        return ResponseEntity.ok(itemService.getListItem(pageable));
    }

    @Operation(description = "create new item in depo owner", operationId = "createItem", summary = "create new item in depo owner")
    @SecurityRequirement(name = "nleapi")
    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestBody CreateItemRequest request) {
        return ResponseEntity.ok(itemService.createItem(request));
    }

    @Operation(description = "delete list item in depo owner", operationId = "multipleDeleteItem", summary = "create new item in depo owner")
    @SecurityRequirement(name = "nleapi")
    @DeleteMapping
    public ResponseEntity<List<ItemResponse>> multipleDeleteItem(@RequestBody List<Long> listId) {
        return ResponseEntity.ok(itemService.multipleDeleteItem(listId));
    }

}
