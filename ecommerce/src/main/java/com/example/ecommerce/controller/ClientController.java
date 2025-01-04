package com.example.ecommerce.controller;

import com.example.ecommerce.domain.dto.address.AddressRequestDTO;
import com.example.ecommerce.domain.dto.client.ClientRequestDTO;
import com.example.ecommerce.domain.dto.client.ClientResponseDTO;
import com.example.ecommerce.service.ClientService;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Operation(summary = "Listar todos os clientes")
    @GetMapping("/")
    public ResponseEntity<Page<ClientResponseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection))));

        Page<ClientResponseDTO> clientPage = clientService.list(sortBy,
                sortDirection,
                pageable);

        return ResponseEntity.ok(clientPage);
    }

    @Operation(summary = "Listar um cliente")
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    @Operation(summary = "Criar um cliente")
    @PostMapping("/")
    public ResponseEntity<ClientResponseDTO> create(@Valid @RequestBody ClientRequestDTO body) {
        return ResponseEntity.ok(clientService.save(body));
    }

    @Operation(summary = "Atualizar um cliente")
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id, @RequestBody ClientRequestDTO body) {
        return ResponseEntity.ok(clientService.update(id, body));
    }

    @Operation(summary = "Deletar um cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar endereço do cliente")
    @GetMapping("/{clientId}/addresses/")
    public ResponseEntity<ClientResponseDTO> getAddressOfClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.getAddressOfClient(clientId));
    }

    @Operation(summary = "Criar um novo endereço para um cliente")
    @PostMapping("/{clientId}/addresses/")
    public ResponseEntity<ClientResponseDTO> saveAddressInClient(@PathVariable Long clientId, @RequestBody AddressRequestDTO body) {
        return ResponseEntity.ok(clientService.addAddressInClient(clientId, body));
    }

    @Operation(summary = "Atualizar um endereço de um cliente")
    @PutMapping("/{clientId}/addresses/")
    public ResponseEntity<ClientResponseDTO> updateAddressOfClient(@PathVariable Long clientId, @RequestBody AddressRequestDTO body) {
        return ResponseEntity.ok(clientService.updateAddressOfClient(clientId, body));
    }

    @Operation(summary = "Deletar um endereço de um cliente")
    @DeleteMapping("/{clientId}/addresses/")
    public ResponseEntity<Void> deleteAddressOfClient(@PathVariable Long clientId) {
        clientService.deleteAddressOfClient(clientId);
        return ResponseEntity.noContent().build();
    }

}
