package com.example.ecommerce.mapper;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.dto.product.ProductRequestDTO;
import com.example.ecommerce.domain.dto.product.ProductRequestPatchDTO;
import com.example.ecommerce.domain.dto.product.ProductRequestPutDTO;
import com.example.ecommerce.domain.dto.product.ProductResponseDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {
    ProductResponseDTO toResponseDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name")
    @Mapping(target = "description")
    @Mapping(target = "value")
    @Mapping(target = "inventory")
    @Mapping(target = "categories", ignore = true)
    Product toEntity(ProductRequestDTO dto);

    List<ProductResponseDTO> toDTOList(List<Product> products);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "value", ignore = true)
    @Mapping(target = "inventory")
    @Mapping(target = "categories", ignore = true)
    void updateEntityToPatchDTO(ProductRequestPatchDTO dto,
                                @MappingTarget Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name")
    @Mapping(target = "description")
    @Mapping(target = "value")
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "categories", ignore = true)
    void updateEntityToPutDTO(ProductRequestPutDTO dto,
                           @MappingTarget Product product);
}
