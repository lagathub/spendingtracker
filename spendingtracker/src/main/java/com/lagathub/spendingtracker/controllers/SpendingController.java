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

import com.lagathub.spendingtracker.domain.model.Transaction;
import com.lagathub.spendingtracker.service.TransactionService;
import com.lagathub.spendingtracker.dto.request.TransactionRequest;
import com.lagathub.spendingtracker.dto.response.TransactionResponse;
import com.lagathub.spendingtracker.exception.TransactionNotFoundException;
import com.lagathub.spendingtracker.exception.InvalidTransactionException;

@RestController //Tells Spring: "This handles HTTP requests and returns JSON"
@RequestMapping("/api/spending") //All endpoints start with /api/spending
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend
public class SpendingController {
    
    private final TransactionService transactionService;
    
    public SpendingController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    // Get all transactions
    @GetMapping("/transactions/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    //Get endpoint with pagination and filtering
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
    		@RequestParam(defaultValue = "0") int page,
    		@RequestParam(defaultValue = "50") int size,
    		@RequestParam(required = false) String category,
    		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    	
    	List<Transaction> transactions = transactionService.getTransactions(page, size, category, startDate, endDate);
    	List<TransactionResponse> responses = transactions.stream()
    			.map(TransactionResponse::from)
    			.collect(Collectors.toList());
    	
    	return ResponseEntity.ok(responses);
    }
    
    
    // Get transaction by ID with proper error handling
    @GetMapping("/transactions/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        try {
            Transaction transaction = transactionService.findById(id);
            return ResponseEntity.ok(transaction);
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    // Search with proper parameter handling
    @GetMapping("/transactions/search")
    public ResponseEntity<List<Transaction>> searchTransactions(
        @RequestParam String category,
        @RequestParam(required = false) String note
    ) {
        List<Transaction> results = transactionService.searchTransactions(category, note);
        return ResponseEntity.ok(results);
    }
    
    // Add transaction with validation and proper response
    @PostMapping("/transactions")
    public ResponseEntity<Transaction> addTransaction(@Valid @RequestBody TransactionRequest request) {
        try {
            Transaction saved = transactionService.recordTransaction(
                request.getAmount(),
                request.getCategoryName(), 
                request.getNote()
            );
            
            // Return 201 Created with the new transaction
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            
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
    	TransactionResponse response = TransactionResponse.from(updated);
    	return ResponseEntity.ok(response);
    	}
    
    //DELETE endpoint for removing transactions
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
    	transactionService.deleteTransaction(id);
    	return ResponseEntity.noContent().build();
    }
    
    // Useful endpoint for my weekly tracking
    @GetMapping("/current-week")
    public ResponseEntity<List<Transaction>> getCurrentWeekTransactions() {
        List<Transaction> weekTransactions = transactionService.getCurrentWeekTransactions();
        return ResponseEntity.ok(weekTransactions);
    }
    
    // Get today's total (for immediate feedback)
    @GetMapping("/today-total")
    public ResponseEntity<BigDecimal> getTodayTotal() {
        BigDecimal total = transactionService.getTodayTotal();
        return ResponseEntity.ok(total);
    }
}
