package com.example.ecommerce.mapper;

import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.domain.dto.orderItem.OrderItemRequestDTO;
import com.example.ecommerce.domain.dto.orderItem.OrderItemResponseDTO;

import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ProductMapper.class}
)
public interface OrderItemMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "product", source = "product")
    OrderItemResponseDTO toResponseDTO(OrderItem orderItem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quantity")
    @Mapping(target = "unityValue")
    @Mapping(target = "product", ignore = true)
    OrderItem toEntity(OrderItemRequestDTO dto);

    List<OrderItemResponseDTO> toDTOList(List<OrderItem> orderItems);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quantity")
    @Mapping(target = "unityValue")
    @Mapping(target = "product")
    void updateEntityToDTO(OrderItemResponseDTO dto,
                           @MappingTarget OrderItem orderItem);

}
