package com.lagathub.spendingtracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * DTO for transaction creation requests
 * Contains validation annotations to ensure data quality
 */
public class TransactionRequest {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    private String categoryName;
    
    @Size(max = 255, message = "Note must not exceed 255 characters")
    private String note;
    
    // Default constructor (required for JSON deserialization)
    public TransactionRequest() {
    }
    
    // Constructor with all fields
    public TransactionRequest(BigDecimal amount, String categoryName, String note) {
        this.amount = amount;
        this.categoryName = categoryName;
        this.note = note;
    }
    
    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    @Override
    public String toString() {
        return "TransactionRequest{" +
                "amount=" + amount +
                ", categoryName='" + categoryName + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}