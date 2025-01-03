package com.example.ecommerce.controller;

import com.example.ecommerce.domain.dto.purchase.PurchaseRequestDTO;
import com.example.ecommerce.domain.dto.purchase.PurchaseRequestStatusDTO;
import com.example.ecommerce.domain.dto.purchase.PurchaseResponseDTO;
import com.example.ecommerce.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Operation(summary = "Criar um pedido")
    @PostMapping("/")
    public ResponseEntity<PurchaseResponseDTO> create(@RequestBody PurchaseRequestDTO body) {
        return ResponseEntity.ok(purchaseService.save(body));
    }

    @Operation(summary = "Listar pedidos")
    @GetMapping("/")
    public ResponseEntity<Page<PurchaseResponseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchaseResponseDTO> purchasesPage =
                purchaseService.list(pageable);

        return ResponseEntity.ok(purchasesPage);
    }

    @Operation(summary = "Listar pedido por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getById(id));
    }


    @Operation(summary = "Atualizar status do pedido")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PurchaseResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody PurchaseRequestStatusDTO body
    ) {
        return ResponseEntity.ok(purchaseService.updateStatusPurchase(id,
                body));
    }

    @Operation(summary = "Listar pedido por cliente")
    @GetMapping("/client/{id}")
    public ResponseEntity<List<PurchaseResponseDTO>> getByClientId(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.listByClient(id));
    }

}
