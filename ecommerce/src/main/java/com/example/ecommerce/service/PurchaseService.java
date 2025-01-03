package com.example.ecommerce.service;

import com.example.ecommerce.domain.Client;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.Purchase;
import com.example.ecommerce.domain.dto.purchase.PurchaseRequestDTO;
import com.example.ecommerce.domain.dto.purchase.PurchaseRequestStatusDTO;
import com.example.ecommerce.domain.dto.purchase.PurchaseResponseDTO;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.OrderItemMapper;
import com.example.ecommerce.mapper.PurchaseMapper;
import com.example.ecommerce.repository.ClientRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    public Page<PurchaseResponseDTO> list(Pageable pageable) {
        Page<Purchase> purchasePage = purchaseRepository.findAll(pageable);

        return purchasePage.map(purchaseMapper::toResponseDTO);
    }

    public PurchaseResponseDTO getById(Long id) {
        var purchase = purchaseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado!")
        );

        return purchaseMapper.toResponseDTO(purchase);
    }

    @Transactional
    public PurchaseResponseDTO save(PurchaseRequestDTO body) {
        if (body.items().isEmpty()) {
            throw new BusinessException("Pedido deve ter pelo menos um item!");
        }

       Client client = clientRepository.findById(body.idClient()).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado!")
        );

        var purchase = purchaseMapper.toEntity(body);
        purchase.setClient(client);
        purchase.setOrderStatus(OrderStatus.WAITING);

        // criando os OrderItems em Purchase
        var orderItemsEntity = body.items().stream()
                .map(orderItemDTO -> {
                    Product product =
                            productRepository.findById(orderItemDTO.idProduct())
                            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado!"));

                    OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);

                    BigDecimal unityValue = orderItemDTO.unityValue() != null
                            ? orderItemDTO.unityValue()
                            : product.getValue();

                    orderItem.setUnityValue(unityValue);
                    orderItem.setProduct(product);
                    orderItem.setPurchase(purchase);

                    validateProductInventory(orderItem);

                    return orderItem;
                })
                .toList();

        // adicionando os OrderItems em Purchase.items
        orderItemsEntity.forEach(orderItem -> {
            purchase.getItems().add(orderItem);
        });

        BigDecimal totalValue = calculateTotalValue(orderItemsEntity);
        purchase.setTotalValue(totalValue);

        orderItemRepository.saveAll(orderItemsEntity);
        purchaseRepository.save(purchase);

        return purchaseMapper.toResponseDTO(purchase);
    }

    /**
     * Calcula o valor total do pedido
     */
    private BigDecimal calculateTotalValue(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> {
                    return orderItem.getUnityValue().multiply(
                            BigDecimal.valueOf(
                                    orderItem.getQuantity()
                            ));

                }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Verifica o estoque
     */
    private void validateProductInventory(OrderItem orderItem) {
        if (orderItem.getProduct().getInventory() < orderItem.getQuantity()) {
            throw new BusinessException("Estoque insuficiente " +
                    "para o produto " + orderItem.getProduct().getName());
        }
    }

    public PurchaseResponseDTO updateStatusPurchase(Long id,
                                                    PurchaseRequestStatusDTO body) {
        var purchase = purchaseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado!")
        );

        purchase.setOrderStatus(body.orderStatus());

        // atualizando o estoque se o pedido for pago
        if (body.orderStatus() == OrderStatus.PAID) {
            updateProductInventory(purchase.getItems());
        }

        purchaseRepository.save(purchase);

        return purchaseMapper.toResponseDTO(purchase);
    }

    /**
     * Atualiza o estoque
     */
    private void updateProductInventory(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            var product = orderItem.getProduct();
            product.setInventory(product.getInventory() - orderItem.getQuantity());
            productRepository.save(product);
        });
    }

    public List<PurchaseResponseDTO> listByClient(Long idClient) {
        List<Purchase> purchases = purchaseRepository.findByClientId(idClient);

        return purchaseMapper.toDTOList(purchases);
    }
}
