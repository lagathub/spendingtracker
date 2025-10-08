package com.lagathub.spendingtracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.time.DayOfWeek;

import com.lagathub.spendingtracker.domain.model.Category;
import com.lagathub.spendingtracker.domain.model.Transaction;
import com.lagathub.spendingtracker.dto.response.TransactionResponse;
import com.lagathub.spendingtracker.exception.InvalidTransactionException;
import com.lagathub.spendingtracker.exception.ResourceNotFoundException;
import com.lagathub.spendingtracker.repository.TransactionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
@Transactional //Important as it ensures database consistency
public class TransactionService {
	
	private final TransactionRepository transactionRepository;
	private final CategoryService categoryService; //Depends on other service
	
	public TransactionService(TransactionRepository transactionRepository,
							 CategoryService categoryService) {
		this.transactionRepository = transactionRepository;
		this.categoryService = categoryService;
	}
	
	//Main method operation: Record a new transaction
	public Transaction recordTransaction(BigDecimal amount, String categoryName, String note) {
		
		//Step 1: Validate input
		validateTransactionData(amount, categoryName);
		
		//Step 2: Get or create category (delegate to CategoryService)
		Category category = categoryService.findOrCreateCategory(categoryName);
		
		//Step 3: Create transaction (using constructor - slightly cleaner)
		Transaction transaction = new Transaction(amount, category, note);
		
		//Step 4: Save and return
		return transactionRepository.save(transaction);
	}
	
	//Update existing transaction
	public Transaction updateTransaction(Long id, BigDecimal amount, String categoryName, String note) {
		//Validate amount
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Amount must be positive");
		}
		
		//Find existing transaction
		Transaction existingTransaction = transactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
		
		//Find or create category
		Category category = categoryService.findOrCreateCategory(categoryName);
		
		//Update fields
		existingTransaction.setAmount(amount);
		existingTransaction.setCategory(category);
		existingTransaction.setNote(note);
		existingTransaction.setUpdatedAt(LocalDateTime.now()); //Track when updated
		
		return transactionRepository.save(existingTransaction);
	}
	
	//Delete transaction
	public void deleteTransaction(Long id) {
		Transaction transaction = transactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
		
		transactionRepository.delete(transaction);
	}
	
	//Get single transaction by ID - return DTO for API endpoints
	public TransactionResponse getTransactionResponseById(Long id) {
		Transaction transaction = transactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
		return mapToTransactionResponse(transaction);
	}
	
	// Keep this for internal use
	public Transaction getTransactionById(Long id) {
		return transactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
	}
	
	//Get transactions with filters and pagination - return DTOs
	public List<TransactionResponse> getTransactions(int page, int size, String category, LocalDate startDate, LocalDate endDate) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		
		List<Transaction> transactions;
		
		//If no filters, return recent transactions
		if (category == null && startDate == null && endDate == null) {
			Page<Transaction> transactionPage = transactionRepository.findAll(pageable);
			transactions = transactionPage.getContent();
		} else {
			//Apply filters
			LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
			LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
			
			transactions = transactionRepository.findWithFilters(category, startDateTime, endDateTime, pageable);
		}
		
		return transactions.stream()
				.map(this::mapToTransactionResponse)
				.collect(Collectors.toList());
	}
	
    // Validation helper - your validation logic is excellent!
    private void validateTransactionData(BigDecimal amount, String categoryName) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("amount", amount, "must be positive");
        }
        if (amount.compareTo(new BigDecimal("1000000")) > 0) {
            throw new InvalidTransactionException("amount", amount, "exceeds maximum limit of 1,000,000");
        }
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new InvalidTransactionException("category", categoryName, "is required");
        }
        if (categoryName.length() > 50) {
            throw new InvalidTransactionException("category", categoryName, "name too long (max 50 characters)");
        }
    }
	
	//Get today's transactions - return DTOs
	public List<TransactionResponse> getTodayTransactions() {
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		
		List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(startOfDay, endOfDay);
		return transactions.stream()
				.map(this::mapToTransactionResponse)
				.collect(Collectors.toList());
	}

	//Get total spent today (for immediate feedback)
	public BigDecimal getTodayTotal() {
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		
		BigDecimal total = transactionRepository.sumAmountByDateRange(startOfDay, endOfDay);
		return total != null ? total : BigDecimal.ZERO;
	}
	
	//Get current week transactions - return DTOs
	public List<TransactionResponse> getCurrentWeekTransactions() {
		LocalDateTime weekStart = LocalDate.now()
				.with(DayOfWeek.MONDAY)
				.atStartOfDay();
		LocalDateTime weekEnd = weekStart.plusDays(6).withHour(23).withMinute(59);
		
		List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(weekStart, weekEnd);
		return transactions.stream()
				.map(this::mapToTransactionResponse)
				.collect(Collectors.toList());
	}
	
	//Get transactions by category - return DTOs
	public List<TransactionResponse> getTransactionsByCategory(String categoryName) {
		List<Transaction> transactions = transactionRepository.findByCategoryName(categoryName);
		return transactions.stream()
				.map(this::mapToTransactionResponse)
				.collect(Collectors.toList());
	}
	
    // Get transactions for a specific date range - return DTOs
    public List<TransactionResponse> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidTransactionException("Date range cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidTransactionException("Start date must be before or equal to end date");
        }
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(start, end);
        return transactions.stream()
				.map(this::mapToTransactionResponse)
				.collect(Collectors.toList());
    }
    
    public List<TransactionResponse> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
				.map(this::mapToTransactionResponse)
				.collect(Collectors.toList());
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new InvalidTransactionException("Transaction not found with id: " + id));
    }

    public List<TransactionResponse> searchTransactions(String categoryName, String note) {
        List<Transaction> transactions;
        if (note == null || note.trim().isEmpty()) {
            transactions = transactionRepository.findByCategoryName(categoryName);
        } else {
            // You'll need to add this method to your repository
            transactions = transactionRepository.findByCategoryNameAndNoteContaining(categoryName, note);
        }
        
        return transactions.stream()
				.map(this::mapToTransactionResponse)
				.collect(Collectors.toList());
    }
    
    // Helper method to map Transaction to TransactionResponse
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getCategory() != null ? transaction.getCategory().getName() : null,
            transaction.getCategory() != null ? transaction.getCategory().getId() : null,
            transaction.getCreatedAt(),
            transaction.getUpdatedAt(),
            transaction.getNote(),
            transaction.wasRecentlyUpdated() // You'll need to implement this method in Transaction entity
        );
    }
}