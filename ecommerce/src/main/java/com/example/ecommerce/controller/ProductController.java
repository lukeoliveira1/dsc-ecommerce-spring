package com.example.ecommerce.controller;

import com.example.ecommerce.domain.dto.product.ProductRequestDTO;
import com.example.ecommerce.domain.dto.product.ProductRequestPatchDTO;
import com.example.ecommerce.domain.dto.product.ProductRequestPutDTO;
import com.example.ecommerce.domain.dto.product.ProductResponseDTO;
import com.example.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Listar todos os produtos")
    @GetMapping("/")
    public ResponseEntity<Page<ProductResponseDTO>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) BigDecimal minValue,
            @RequestParam(required = false) BigDecimal maxValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDTO> productPage =
                productService.list(name, categoryName, minValue, maxValue,
                        pageable);

        return ResponseEntity.ok(productPage);
    }

    @Operation(summary = "Listar um produto")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @Operation(summary = "Criar um produto")
    @PostMapping("/")
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO body) {
        return ResponseEntity.ok(productService.save(body));
    }

    @Operation(summary = "Atualizar um produto")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestPutDTO body
    ) {
        return ResponseEntity.ok(productService.update(id, body));
    }

    @Operation(summary = "Deletar um produto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete((id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar estoque do produto")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProductInventory(
            @PathVariable Long id,
            @RequestBody ProductRequestPatchDTO body
    ) {
        return ResponseEntity.ok(productService.updateInventory(id, body));
    }

    @Operation(summary = "Listar produtos por categoria")
    @GetMapping("category/{idCategory}")
    public ResponseEntity<List<ProductResponseDTO>> removeProductOfCategory(
            @PathVariable Long idCategory
    ) {
        return ResponseEntity.ok(productService.listByCategory(idCategory));
    }
}
