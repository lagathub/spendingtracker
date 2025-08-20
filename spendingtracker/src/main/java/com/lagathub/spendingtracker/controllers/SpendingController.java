package com.lagathub.spendingtracker.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;
import java.math.BigDecimal;
import com.lagathub.spendingtracker.domain.model.Transaction;
import com.lagathub.spendingtracker.service.TransactionService;
import com.lagathub.spendingtracker.dto.request.TransactionRequest;
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
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
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
    
    // Useful endpoint for your weekly tracking
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
