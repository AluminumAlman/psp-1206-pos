package com.team1206.pos.payments.transaction;

import com.team1206.pos.common.enums.PaymentMethodType;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.common.enums.TransactionStatus;
import com.team1206.pos.exceptions.InvalidPaymentMethod;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.order.order.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final OrderService orderService;

    public TransactionService(
            TransactionRepository transactionRepository,
            OrderService orderService
    ) {
        this.transactionRepository = transactionRepository;
        this.orderService = orderService;
    }

    // TODO: Check that the logged in user has access to the order
    // TODO filter my Merchant
    // Get paged transactions
    public Page<TransactionResponseDTO> getTransactions(int limit, int offset, UUID orderId) {
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Transaction> transactionPage = transactionRepository.findAllWithFilters(
                orderId,
                pageable
        );

        return transactionPage.map(this::mapToResponseDTO);
    }

    // TODO: Check that the logged in user has access to the order
    // Get paged transactions with filters
    public Page<TransactionResponseDTO> getTransactions(
            int limit,
            int offset,
            UUID orderId,
            String paymentMethodType,
            String status,
            BigDecimal amount
    ) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be greater than or equal to 0");
        }

        checkIfOrderExists(orderId);

        PaymentMethodType paymentMethod =
                (paymentMethodType != null && !paymentMethodType.isEmpty()) ? PaymentMethodType.valueOf(
                        paymentMethodType.toUpperCase()) : null;

        TransactionStatus transactionStatus =
                (status != null && !status.isEmpty()) ? TransactionStatus.valueOf(status.toUpperCase()) : null;

        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Transaction> transactionPage = transactionRepository.findAllWithFilters(
                orderId,
                paymentMethod,
                transactionStatus,
                amount,
                pageable
        );

        return transactionPage.map(this::mapToResponseDTO);
    }

    // TODO: Check that the logged in user has access to the order
    // Create transaction
    public TransactionResponseDTO createTransaction(
            UUID orderId,
            TransactionRequestDTO requestDTO
    ) {
        checkIfOrderExists(orderId);

        Transaction transaction = new Transaction();

        setTransactionFieldsFromRequestDTO(transaction, requestDTO);
        transaction.setOrder(orderService.getOrderEntityById(orderId));

        transaction.setStatus(TransactionStatus.PENDING);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponseDTO(savedTransaction);
    }

    // TODO: Check that the logged in user has access to the order
    // Get transaction by ID
    public TransactionResponseDTO getTransaction(UUID orderId, UUID transactionId) {
        checkIfOrderExists(orderId);

        Transaction transaction = transactionRepository.findById(transactionId)
                                                       .orElseThrow(() -> new ResourceNotFoundException(
                                                               ResourceType.TRANSACTION,
                                                               transactionId.toString()
                                                       ));

        if (!transaction.getOrder().getId().equals(orderId)) {
            throw new ResourceNotFoundException(ResourceType.TRANSACTION, transactionId.toString());
        }

        return mapToResponseDTO(transaction);
    }

    // TODO: Check that the logged in user has access to the order
    // Mark cash transaction as completed
    public TransactionResponseDTO completeCashTransaction(UUID orderId, UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                                                       .orElseThrow(() -> new ResourceNotFoundException(
                                                               ResourceType.TRANSACTION,
                                                               transactionId.toString()
                                                       ));

        if (!transaction.getPaymentMethod().equals(PaymentMethodType.CASH)) {
            throw new InvalidPaymentMethod("Only cash transactions can be completed");
        }

        checkIfOrderExists(orderId);

        if (!transaction.getOrder().getId().equals(orderId)) {
            throw new ResourceNotFoundException(ResourceType.TRANSACTION, transactionId.toString());
        }

        transaction.setStatus(TransactionStatus.COMPLETED);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return mapToResponseDTO(updatedTransaction);
    }

    // TODO: Check that the logged in user has access to the order
    // Refund transaction
    public TransactionResponseDTO refundTransaction(UUID orderId, UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                                                       .orElseThrow(() -> new ResourceNotFoundException(
                                                               ResourceType.TRANSACTION,
                                                               transactionId.toString()
                                                       ));
        checkIfOrderExists(orderId);

        Transaction savedTransaction;
        if (transaction.getPaymentMethod().equals(PaymentMethodType.CASH)) {
            transaction.setStatus(TransactionStatus.REFUNDED);

            savedTransaction = transactionRepository.save(transaction);
        }
        else {
            throw new InvalidPaymentMethod("Currently, only cash transactions can be refunded");
        }

        return mapToResponseDTO(savedTransaction);
    }


    // *** Helper methods ***

    private void checkIfOrderExists(UUID orderId) {
        orderService.getOrderEntityById(orderId);
    }

    private void setTransactionFieldsFromRequestDTO(
            Transaction transaction,
            TransactionRequestDTO requestDTO
    ) {
        transaction.setAmount(requestDTO.getAmount());
        transaction.setPaymentMethod(PaymentMethodType.valueOf(requestDTO.getPaymentMethodType()
                                                                         .toUpperCase()));
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();

        responseDTO.setId(transaction.getId());
        responseDTO.setStatus(transaction.getStatus().name());
        responseDTO.setPaymentMethodType(transaction.getPaymentMethod().name());
        responseDTO.setAmount(transaction.getAmount());
        responseDTO.setOrderId(transaction.getOrder().getId());
        responseDTO.setCreatedAt(transaction.getCreatedAt());
        responseDTO.setUpdatedAt(transaction.getUpdatedAt());
        responseDTO.setOrderId(transaction.getOrder().getId());

        return responseDTO;
    }
}
