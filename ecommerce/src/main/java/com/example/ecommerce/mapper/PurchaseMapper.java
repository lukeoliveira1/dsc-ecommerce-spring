package com.example.ecommerce.mapper;

import com.example.ecommerce.domain.Purchase;
import com.example.ecommerce.domain.dto.purchase.PurchaseRequestDTO;
import com.example.ecommerce.domain.dto.purchase.PurchaseResponseDTO;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {
            ClientMapper.class,
            OrderItemMapper.class
        }
)
public interface PurchaseMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "client")
    @Mapping(target = "items", source = "items")
    PurchaseResponseDTO toResponseDTO(Purchase purchase);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalValue", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "items", ignore = true)
    Purchase toEntity(PurchaseRequestDTO dto);

    List<PurchaseResponseDTO> toDTOList(List<Purchase> purchases);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "totalValue", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "items")
    void updateEntityToDTO(PurchaseRequestDTO dto,
                           @MappingTarget Purchase purchase);
}
