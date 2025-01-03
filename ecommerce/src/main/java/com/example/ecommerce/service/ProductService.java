package com.example.ecommerce.service;

import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.dto.product.ProductRequestDTO;
import com.example.ecommerce.domain.dto.product.ProductRequestPatchDTO;
import com.example.ecommerce.domain.dto.product.ProductRequestPutDTO;
import com.example.ecommerce.domain.dto.product.ProductResponseDTO;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductMapper productMapper;

    public ProductResponseDTO save(ProductRequestDTO body) {
        var product = productMapper.toEntity(body);

        if (body.value().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("O valor não pode ser menor do que 0");
        }

        productRepository.save(product);

        return productMapper.toResponseDTO(product);
    }

    public Page<ProductResponseDTO> list(Pageable pageable) {
        Page<Product> productsPage = productRepository.findAll(pageable);

        return productsPage.map(productMapper::toResponseDTO);
    }

    public ProductResponseDTO getById(Long id) {
        var product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado")
        );

        return productMapper.toResponseDTO(product);
    }

    public ProductResponseDTO update(Long id, ProductRequestPutDTO body) {
        var product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado!")
        );

        productMapper.updateEntityToPutDTO(body, product);

        var updatedProduct = productRepository.save(product);

        return productMapper.toResponseDTO(updatedProduct);
    }

    public ProductResponseDTO updateInventory(Long id,
                                              ProductRequestPatchDTO body) {
        var product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado!")
        );

        productMapper.updateEntityToPatchDTO(body, product);

        var updatedProduct = productRepository.save(product);

        return productMapper.toResponseDTO(updatedProduct);
    }

    public void delete(Long id) {
        if(!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado!");
        }
        productRepository.deleteById(id);
    }

    public List<ProductResponseDTO> listByCategory(Long idCategory) {
        Category category = categoryRepository.findById(idCategory).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrado!")
        );

        List<Product> productsByCategory =
                productRepository.findByCategories(category);

        return productMapper.toDTOList(productsByCategory);
    }
}
