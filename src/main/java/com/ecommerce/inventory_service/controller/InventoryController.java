package com.ecommerce.inventory_service.controller;

import com.ecommerce.inventory_service.dto.InventoryRequestDTO;
import com.ecommerce.inventory_service.dto.InventoryResponseDTO;
import com.ecommerce.inventory_service.dto.StockRequest;
import com.ecommerce.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/exist/{sku}/quantity/{quantity}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("sku") String sku, @PathVariable("quantity") Integer quantity) {
        return inventoryService.isInStock(sku, quantity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponseDTO createInventory(@RequestBody @Valid InventoryRequestDTO inventoryRequest) {
        return inventoryService.createInventory(inventoryRequest);
    }

    @GetMapping
    public List<InventoryResponseDTO> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InventoryResponseDTO updateInventory(@PathVariable Long id, @RequestBody InventoryRequestDTO inventoryRequest) {
        return inventoryService.updateInventory(id, inventoryRequest);
    }

    @PatchMapping("/stock")
    @ResponseStatus(HttpStatus.OK)
    public String reduceStock(@RequestBody StockRequest stockRequest) {
        log.info(stockRequest.toString());
        inventoryService.reduceStock(stockRequest.sku(), stockRequest.quantity());

        return "Stock actualizado";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }

}
