package com.example.ecommerce.domain.dto.client;

import com.example.ecommerce.domain.dto.address.AddressResponseDTO;

public record ClientResponseDTO(
        Long id,
        String name,
        String email,
        String cpf,
        String phoneNumber,
        AddressResponseDTO address
) {}
