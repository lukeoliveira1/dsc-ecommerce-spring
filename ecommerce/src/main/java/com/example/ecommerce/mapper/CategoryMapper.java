package com.example.ecommerce.mapper;

import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.dto.category.CategoryProductsResponseDTO;
import com.example.ecommerce.domain.dto.category.CategoryRequestDTO;
import com.example.ecommerce.domain.dto.category.CategoryResponseDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO toResponseDTO(Category category);

    CategoryProductsResponseDTO toResponseProductsDTO(Category category);

    // Converter DTO para Categoria
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name")
    @Mapping(target = "description")
    Category toEntity(CategoryRequestDTO dto);

    List<CategoryResponseDTO> toDTOList(List<Category> categories);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name")
    @Mapping(target = "description")
    void updateEntityFromDTO(CategoryRequestDTO dto, @MappingTarget Category category);

}
