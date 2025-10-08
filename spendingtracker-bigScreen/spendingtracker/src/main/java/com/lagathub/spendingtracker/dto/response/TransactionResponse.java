package com.lagathub.spendingtracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.lagathub.spendingtracker.domain.model.Transaction;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String categoryName;
    private Long categoryId; // NEW: For easier frontend handling
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // NEW: Show when last modified
    private String note;
    private boolean recentlyUpdated; // NEW: UI indicator

    // Factory method
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getCategory() != null ? transaction.getCategory().getName() : null,
            transaction.getCategory() != null ? transaction.getCategory().getId() : null,
            transaction.getCreatedAt(),
            transaction.getUpdatedAt(),
            transaction.getNote(),
            transaction.wasRecentlyUpdated()
        );
    }
}