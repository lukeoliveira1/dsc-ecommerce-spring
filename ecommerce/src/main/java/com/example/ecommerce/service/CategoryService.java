package com.example.ecommerce.service;

import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.dto.category.CategoryProductsResponseDTO;
import com.example.ecommerce.domain.dto.category.CategoryRequestDTO;
import com.example.ecommerce.domain.dto.category.CategoryResponseDTO;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashSet;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductRepository productRepository;


    public CategoryResponseDTO create(CategoryRequestDTO body) {
        var category = categoryMapper.toEntity(body);

        if (categoryRepository.existsByName(body.name())) {
            throw new BusinessException("Já existe uma categoria com esse nome");
        }

        categoryRepository.save(category);

        return categoryMapper.toResponseDTO(category);
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryResponseDTO getById(Long id) {
        var category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada!")
        );

        return categoryMapper.toResponseDTO(category);
    }

    public List<CategoryResponseDTO> list() {
        List<Category> categories = categoryRepository.findAll();

        return categoryMapper.toDTOList(categories);
    }

    public void delete(Long id) {

        var category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não existe!")
        );

        if (category.getProducts() != null) {
            throw new BusinessException("Não é possível deletar a categoria " +
                    "pois ela possui produtos associados!");
        }
        categoryRepository.deleteById(id);
    }

    public CategoryResponseDTO update(Long id, CategoryRequestDTO body) {
        var category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada!")
        );

        categoryMapper.updateEntityFromDTO(body, category);

        var updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponseDTO(updatedCategory);
    }

    public void addProductToCategory(Product product, Category category) {
        if (category.getProducts() == null) {
            category.setProducts(new HashSet<>());
        }

        if (product.getCategories() == null) {
            product.setCategories(new HashSet<>());
        }

        category.getProducts().add(product);
        product.getCategories().add(category);
    }

    public CategoryProductsResponseDTO associateProductInCategory(
            @PathVariable Long idCategory,
            @PathVariable Long idProduct
    ) {
        var product = productRepository.findById(idProduct).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado!")
        );

        var category = categoryRepository.findById(idCategory).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada!")
        );

        this.addProductToCategory(product, category);

        var updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponseProductsDTO(updatedCategory);
    }

    public void deleteProductToCategory(Product product, Category category) {
        if (!category.getProducts().contains(product)) {
            throw new ResourceNotFoundException("Não existe esse produto nessa categoria");
        }

        category.getProducts().remove(product);


        if (product.getCategories() != null) {
            product.getCategories().remove(category);
        }

        categoryRepository.save(category);
        productRepository.save(product);
    }

    public CategoryResponseDTO removeProductOfCategory(
            @PathVariable Long idCategory,
            @PathVariable Long idProduct
    ) {
        var product = productRepository.findById(idProduct).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado!")
        );

        var category = categoryRepository.findById(idCategory).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada!")
        );

        this.deleteProductToCategory(product, category);

        var updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponseDTO(updatedCategory);
    }
}
