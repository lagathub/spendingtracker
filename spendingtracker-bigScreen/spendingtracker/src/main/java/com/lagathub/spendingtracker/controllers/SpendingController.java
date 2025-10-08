package com.lagathub.spendingtracker.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.lagathub.spendingtracker.domain.model.Category;
import com.lagathub.spendingtracker.domain.model.Transaction;
import com.lagathub.spendingtracker.service.TransactionService;
import com.lagathub.spendingtracker.service.CategoryService;
import com.lagathub.spendingtracker.dto.request.TransactionRequest;
import com.lagathub.spendingtracker.dto.response.TransactionResponse;
import com.lagathub.spendingtracker.exception.TransactionNotFoundException;
import com.lagathub.spendingtracker.exception.InvalidTransactionException;

@RestController //Tells Spring: "This handles HTTP requests and returns JSON"
@RequestMapping("/api/spending") //All endpoints start with /api/spending
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend
public class SpendingController {
    
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    
    public SpendingController(TransactionService transactionService, CategoryService categoryService) {
        this.transactionService = transactionService;
        this.categoryService = categoryService;
    }
    
    // Get all transactions - now returns DTOs
    @GetMapping("/transactions/all")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    //Get endpoint with pagination and filtering - now uses service that returns DTOs
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
    		@RequestParam(defaultValue = "0") int page,
    		@RequestParam(defaultValue = "50") int size,
    		@RequestParam(required = false) String category,
    		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    	
    	List<TransactionResponse> transactions = transactionService.getTransactions(page, size, category, startDate, endDate);
    	return ResponseEntity.ok(transactions);
    }
    
    // Get transaction by ID - now returns DTO
    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        try {
            TransactionResponse transaction = transactionService.getTransactionResponseById(id);
            return ResponseEntity.ok(transaction);
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Search with proper parameter handling - now returns DTOs
    @GetMapping("/transactions/search")
    public ResponseEntity<List<TransactionResponse>> searchTransactions(
        @RequestParam String category,
        @RequestParam(required = false) String note
    ) {
        List<TransactionResponse> results = transactionService.searchTransactions(category, note);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<String> categoryNames = categories.stream()
            .map(Category::getName)
            .collect(Collectors.toList());
        return ResponseEntity.ok(categoryNames);
    } 
    
    // Add transaction with validation and proper response - returns DTO
    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> addTransaction(@Valid @RequestBody TransactionRequest request) {
        try {
            Transaction saved = transactionService.recordTransaction(
                request.getAmount(),
                request.getCategoryName(), 
                request.getNote()
            );
            
            // Convert to DTO before returning
            TransactionResponse response = mapToTransactionResponse(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (InvalidTransactionException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    //PUT endpoint for updating transactions
    @PutMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
    		@PathVariable Long id,
    		@Valid @RequestBody TransactionRequest request) {
    	
    	Transaction updated = transactionService.updateTransaction(
    			id,
    			request.getAmount(),
    			request.getCategoryName(),
    			request.getNote()
    	);
    	TransactionResponse response = mapToTransactionResponse(updated);
    	return ResponseEntity.ok(response);
    }
    
    //DELETE endpoint for removing transactions
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
    	transactionService.deleteTransaction(id);
    	return ResponseEntity.noContent().build();
    }
    
    // Current week transactions - now returns DTOs (this was causing your 500 error!)
    @GetMapping("/current-week")
    public ResponseEntity<List<TransactionResponse>> getCurrentWeekTransactions() {
        List<TransactionResponse> weekTransactions = transactionService.getCurrentWeekTransactions();
        return ResponseEntity.ok(weekTransactions);
    }
    
    // Get today's total (for immediate feedback)
    @GetMapping("/today-total")
    public ResponseEntity<BigDecimal> getTodayTotal() {
        BigDecimal total = transactionService.getTodayTotal();
        return ResponseEntity.ok(total);
    }
    
    // Helper method to safely convert Transaction to TransactionResponse
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getCategory() != null ? transaction.getCategory().getName() : null,
            transaction.getCategory() != null ? transaction.getCategory().getId() : null,
            transaction.getCreatedAt(),
            transaction.getUpdatedAt(),
            transaction.getNote(),
            transaction.wasRecentlyUpdated() // Make sure this method exists in your Transaction entity
        );
    }
}