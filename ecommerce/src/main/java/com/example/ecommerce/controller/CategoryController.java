package com.example.ecommerce.controller;

import com.example.ecommerce.domain.dto.category.CategoryProductsResponseDTO;
import com.example.ecommerce.domain.dto.category.CategoryRequestDTO;
import com.example.ecommerce.domain.dto.category.CategoryResponseDTO;
import com.example.ecommerce.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Listar todas as categorias")
    @GetMapping("/")
    public ResponseEntity<List<CategoryResponseDTO>> get() {
        List<CategoryResponseDTO> categories = categoryService.list();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Listar uma categoria")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        CategoryResponseDTO category = categoryService.getById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Criar uma categoria")
    @PostMapping("/")
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody CategoryRequestDTO body) {
        CategoryResponseDTO categoryResponseDTO = categoryService.create(body);
        return ResponseEntity.ok(categoryResponseDTO);
    }

    @Operation(summary = "Atualizar uma categoria")
    @PutMapping("/")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @RequestBody CategoryRequestDTO body) {
        CategoryResponseDTO categoryResponseDTO = categoryService.update(id, body);
        return ResponseEntity.ok(categoryResponseDTO);
    }

    @Operation(summary = "Deletar uma categoria")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adicionar produto em categoria")
    @PostMapping("{idCategory}/products/{idProduct}")
    public ResponseEntity<CategoryProductsResponseDTO> addProductInCategory(
            @PathVariable Long idCategory,
            @PathVariable Long idProduct
    ) {
        return ResponseEntity.ok(
                categoryService.associateProductInCategory(idCategory, idProduct)
        );
    }

    @Operation(summary = "Remover produto em categoria")
    @DeleteMapping("{idCategory}/products/{idProduct}")
    public ResponseEntity<CategoryResponseDTO> removeProductOfCategory(
            @PathVariable Long idCategory,
            @PathVariable Long idProduct
    ) {
        return ResponseEntity.ok(
                categoryService.removeProductOfCategory(idCategory, idProduct)
        );
    }
}
