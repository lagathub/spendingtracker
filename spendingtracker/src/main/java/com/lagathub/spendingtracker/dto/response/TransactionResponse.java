package com.lagathub.spendingtracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.lagathub.spendingtracker.domain.model.Transaction;

public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String categoryName;
    private Long categoryId; // NEW: For easier frontend handling
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // NEW: Show when last modified
    private String note;
    private boolean recentlyUpdated; // NEW: UI indicator
    
    // Constructors
    public TransactionResponse() {}
    
    public TransactionResponse(Long id, BigDecimal amount, String categoryName, Long categoryId,
                             LocalDateTime createdAt, LocalDateTime updatedAt, String note, boolean recentlyUpdated) {
        this.id = id;
        this.amount = amount;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.note = note;
        this.recentlyUpdated = recentlyUpdated;
    }
    
    // Factory method
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getCategory().getName(),
            transaction.getCategory().getId(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt(),
            transaction.getNote(),
            transaction.wasRecentlyUpdated()
        );
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public boolean isRecentlyUpdated() { return recentlyUpdated; }
    public void setRecentlyUpdated(boolean recentlyUpdated) { this.recentlyUpdated = recentlyUpdated; }

}
