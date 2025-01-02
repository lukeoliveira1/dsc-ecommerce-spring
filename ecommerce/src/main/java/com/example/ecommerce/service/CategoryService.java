package com.example.ecommerce.service;

import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.dto.category.CategoryRequestDTO;
import com.example.ecommerce.domain.dto.category.CategoryResponseDTO;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper mapper;

    public CategoryResponseDTO create(CategoryRequestDTO body) {
        var category = mapper.toEntity(body);

        if (categoryRepository.existsByName(body.name())) {
            throw new BusinessException("Já existe uma categoria com esse nome");
        }

        categoryRepository.save(category);

        return mapper.toResponseDTO(category);
    }

    public CategoryResponseDTO getById(Long id) {
        var category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada!")
        );

        return mapper.toResponseDTO(category);
    }

    public List<CategoryResponseDTO> list() {
        List<Category> categories = categoryRepository.findAll();

        return mapper.toDTOList(categories);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    public CategoryResponseDTO update(Long id, CategoryRequestDTO body) {
        var category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada!")
        );

        mapper.updateEntityFromDTO(body, category);

        var updatedCategory = categoryRepository.save(category);

        return mapper.toResponseDTO(updatedCategory);
    }

//    public associateProductInCategory
//    public removeProductOfCategory
}
