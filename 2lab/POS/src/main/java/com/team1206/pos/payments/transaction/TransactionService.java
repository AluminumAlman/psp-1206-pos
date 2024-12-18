package com.team1206.pos.payments.transaction;

import com.team1206.pos.common.enums.PaymentMethodType;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.common.enums.TransactionStatus;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.order.order.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    // TODO filter my Merchant
    // Get paged transactions
    public Page<TransactionResponseDTO> getTransactions(int limit, int offset, String orderId) {
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Transaction> transactionPage = transactionRepository.findAllWithFilters(
                orderId,
                pageable
        );

        return transactionPage.map(this::mapToResponseDTO);
    }

    // Create transaction
    public TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO) {
        Transaction transaction = new Transaction();

        setTransactionFieldsFromRequestDTO(transaction, requestDTO);
        transaction.setOrder(orderService.getOrderById(requestDTO.getOrderId()));

        transaction.setStatus(TransactionStatus.PENDING);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponseDTO(savedTransaction);
    }

    // Update transaction state by ID
    public TransactionResponseDTO updateTransaction(UUID transactionId, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                                                       .orElseThrow(() -> new ResourceNotFoundException(
                                                               ResourceType.TRANSACTION,
                                                               transactionId.toString()
                                                       ));

        transaction.setStatus(status);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return mapToResponseDTO(updatedTransaction);
    }


    // *** Helper methods ***

    private void setTransactionFieldsFromRequestDTO(
            Transaction transaction,
            TransactionRequestDTO requestDTO
    ) {
        transaction.setAmount(requestDTO.getAmount());
        transaction.setPaymentMethod(PaymentMethodType.valueOf(requestDTO.getPaymentMethod()
                                                                         .toUpperCase()));
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();

        responseDTO.setId(transaction.getId());
        responseDTO.setStatus(transaction.getStatus().name());
        responseDTO.setPaymentMethod(transaction.getPaymentMethod().name());
        responseDTO.setAmount(transaction.getAmount());
        responseDTO.setOrderId(transaction.getOrder().getId());
        responseDTO.setCreatedAt(transaction.getCreatedAt());
        responseDTO.setUpdatedAt(transaction.getUpdatedAt());
        responseDTO.setOrderId(transaction.getOrder().getId());

        return responseDTO;
    }
}
