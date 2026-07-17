package com.ecommerce.inventory_service.service.impl;

import com.ecommerce.inventory_service.dto.InventoryRequestDTO;
import com.ecommerce.inventory_service.dto.InventoryResponseDTO;
import com.ecommerce.inventory_service.exception.ResourceNotFoundException;
import com.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory_service.model.Inventory;
import com.ecommerce.inventory_service.repository.InventoryRepository;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(String sku, Integer quantity) {
        return inventoryRepository.findBySku(sku)
                .map(inventory -> inventory.getQuantity() >= quantity)
                .orElse(false);
    }

    @Override
    @Transactional
    public InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequest) {
        String sku = inventoryRequest.sku();
        boolean inventoryExist = inventoryRepository.existsBySku(sku);

        if (inventoryExist) {
            throw new RuntimeException("El inventario para el SKU " + sku + " ya existe");
        }

        Inventory inventory = mapper.toModel(inventoryRequest);
        Inventory savedInventory = inventoryRepository.save(inventory);

        return mapper.toResponse(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getAllInventory() {
        return inventoryRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public InventoryResponseDTO updateInventory(Long id, InventoryRequestDTO inventoryRequest) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", id));

        inventory.setSku(inventoryRequest.sku());
        inventory.setQuantity(inventoryRequest.quantity());

        Inventory updateInventory = inventoryRepository.save(inventory);

        log.info("Inventario actualizado para el id: {}", id);

        return mapper.toResponse(updateInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        inventoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", id));

        inventoryRepository.deleteById(id);
    }
}
