package com.lagathub.spendingtracker.service;

import java.math.BigDecimal;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.time.DayOfWeek;

import com.lagathub.spendingtracker.domain.model.Category;
import com.lagathub.spendingtracker.domain.model.Transaction;
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
	
	//Get single transaction by ID
	public Transaction getTransactionById(Long id) {
		return transactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
	}
	
	//Get transactions with filters and pagination
	public List<Transaction> getTransactions(int page, int size, String category, LocalDate startDate, LocalDate endDate) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		
		//If no filters, return recent transactions
		if (category == null && startDate == null && endDate == null) {
			Page<Transaction> transactionPage = transactionRepository.findAll(pageable);
			return transactionPage.getContent();
		}
		
		//Apply filters
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
		
		return transactionRepository.findWithFilters(category, startDateTime, endDateTime, pageable);
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
	
	//Get today's transactions
	public List<Transaction> getTodayTransactions() {
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		
		return transactionRepository.findByCreatedAtBetween(startOfDay, endOfDay);
	}

	//Get total spent today (for immediate feedback)
	public BigDecimal getTodayTotal() {
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		
		BigDecimal total = transactionRepository.sumAmountByDateRange(startOfDay, endOfDay);
		return total != null ? total : BigDecimal.ZERO;
	}
	
	//Get current week transactions
	public List<Transaction> getCurrentWeekTransactions() {
		LocalDateTime weekStart = LocalDate.now()
				.with(DayOfWeek.MONDAY)
				.atStartOfDay();
		LocalDateTime weekEnd = weekStart.plusDays(6).withHour(23).withMinute(59);
		
		return transactionRepository.findByCreatedAtBetween(weekStart, weekEnd);
	}
	
	//Get transactions by category
	public List<Transaction> getTransactionsByCategory(String categoryName) {
		return transactionRepository.findByCategoryName(categoryName);
	}
	
    // Get transactions for a specific date range (useful for custom reports)
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidTransactionException("Date range cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidTransactionException("Start date must be before or equal to end date");
        }
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        return transactionRepository.findByCreatedAtBetween(start, end);
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new InvalidTransactionException("Transaction not found with id: " + id));
    }

    public List<Transaction> searchTransactions(String categoryName, String note) {
        if (note == null || note.trim().isEmpty()) {
            return transactionRepository.findByCategoryName(categoryName);
        }
        // You'll need to add this method to your repository
        return transactionRepository.findByCategoryNameAndNoteContaining(categoryName, note);
    }
	
}
