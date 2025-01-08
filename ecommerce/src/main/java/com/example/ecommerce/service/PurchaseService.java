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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public Page<PurchaseResponseDTO> list(LocalDateTime startDate,
                                          LocalDateTime endDate,
                                          Pageable pageable) {
        Page<Purchase> purchasePage =
                purchaseRepository.findFilteredByOrderDate(startDate,
                        endDate, pageable);

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
        Logger logger = LoggerFactory.getLogger(this.getClass());

        logger.info("Iniciando o processamento do pedido.");

        if (body.items().isEmpty()) {
            logger.warn("Tentativa de salvar um pedido sem itens.");
            throw new BusinessException("Pedido deve ter pelo menos um item!");
        }

        logger.debug("Validando o cliente com ID: {}", body.idClient());
        Client client =
                clientRepository.findById(body.idClient()).orElseThrow(() -> {
                    logger.error("Cliente com ID {} não encontrado.",
                            body.idClient());
                    return new ResourceNotFoundException("Cliente não " +
                            "encontrado!");
                }
        );

        logger.debug("Mapeando o pedido para a entidade Purchase.");
        var purchase = purchaseMapper.toEntity(body);
        purchase.setClient(client);
        purchase.setOrderStatus(OrderStatus.WAITING);

        // criando os OrderItems em Purchase
        logger.debug("Iniciando o mapeamento dos itens do pedido.");
        var orderItemsEntity = body.items().stream()
                .map(orderItemDTO -> {
                    logger.debug("Buscando produto com ID: {}", orderItemDTO.idProduct());
                    Product product =
                            productRepository.findById(orderItemDTO.idProduct())
                            .orElseThrow(() -> {
                                logger.error("Produto com ID {} não " +
                                        "encontrado", orderItemDTO.idProduct());
                                return new ResourceNotFoundException("Produto" +
                                        " " +
                                        "não encontrado!");
                            });

                    OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);

                    BigDecimal unityValue = orderItemDTO.unityValue() != null
                            ? orderItemDTO.unityValue()
                            : product.getValue();

                    orderItem.setUnityValue(unityValue);
                    orderItem.setProduct(product);
                    orderItem.setPurchase(purchase);

                    logger.debug("Validando o estoque para o produto ID: {}", product.getId());
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

        logger.info("Pedido processado com sucesso. ID do pedido: {}", purchase.getId());
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

        OrderStatus currentStatus = purchase.getOrderStatus();
        OrderStatus newStatus = body.orderStatus();

        if(!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BusinessException("Transição de status inválida!");
        }

        purchase.setOrderStatus(body.orderStatus());

        // atualizando o estoque se o pedido for pago
        if (body.orderStatus() == OrderStatus.PAID) {
            updateProductInventory(purchase.getItems());
        }

        purchaseRepository.save(purchase);

        return purchaseMapper.toResponseDTO(purchase);
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus,
                                            OrderStatus newStatus) {
        switch (currentStatus) {
            case WAITING:
                return newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
            case PAID:
                return newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED:
                return false;
            case CANCELLED:
                return false;
            default:
                return false;
        }
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
