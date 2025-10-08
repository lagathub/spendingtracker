package com.lagathub.spendingtracker.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lagathub.spendingtracker.domain.model.WeeklyReport;
import com.lagathub.spendingtracker.domain.model.Transaction;
import com.lagathub.spendingtracker.domain.model.Category;
import com.lagathub.spendingtracker.domain.model.CategorySpending;
import com.lagathub.spendingtracker.repository.TransactionRepository;
import com.lagathub.spendingtracker.repository.WeeklyReportRepository;


@Service
@Transactional
public class WeeklyReportService {
	
	private final WeeklyReportRepository weeklyReportRepository;
	private final TransactionRepository transactionRepository;
	
	public WeeklyReportService(WeeklyReportRepository weeklyReportRepository,
		                      TransactionRepository transactionRepository,
		                      TransactionService transactionService) {
		this.weeklyReportRepository = weeklyReportRepository;
		this.transactionRepository = transactionRepository;
	}
	
	//Main operation: Generate weekly report
	public WeeklyReport generateWeeklyReport(LocalDate weekStartDate) {
		
		//Step 1: Ensures it starts on Monday
		LocalDate weekStart = weekStartDate.with(DayOfWeek.MONDAY);
		LocalDate weekEnd = weekStart.plusDays(6);
		
		//Step 2: Check if report already exists
		Optional<WeeklyReport> existing = weeklyReportRepository.findByWeekStartDate(weekStartDate);
		if (existing.isPresent()) {
			return existing.get(); //Don't regenerate
		}
		
		//Step 3: Get all transactions for this week
		List<Transaction> weekTransactions = getTransactionsForWeek(weekStart, weekEnd);
		
		//Step 4: Calculate totals
		BigDecimal totalSpent = calculateTotalSpent(weekTransactions);
		
		//Step 5: Create report
		WeeklyReport report = new WeeklyReport(weekStart, weekEnd, totalSpent);
		report.setGeneratedAt(LocalDateTime.now());
		
		//Step 6: Calculate category breakdowns
		List<CategorySpending> breakdowns = calculateCategoryBreakdowns(weekTransactions, report);
		report.setCategoryBreakdowns(breakdowns);
		
		//Step 7: SAve and return
		return weeklyReportRepository.save(report);
	}
	
	//Helper: Get transactions for week
	private List<Transaction> getTransactionsForWeek(LocalDate weekStart, LocalDate weekEnd) {
		LocalDateTime startDateTime = weekStart.atStartOfDay();
		LocalDateTime endDateTime = weekEnd.atTime(23, 59, 59);
		
		return transactionRepository.findByCreatedAtBetween(startDateTime, endDateTime);
	}
	
	//Helper: Calculate total
	private BigDecimal calculateTotalSpent(List<Transaction> transactions) {
		return transactions.stream()
				.map(Transaction::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	//Helper: Category breakdowns
	private List<CategorySpending> calculateCategoryBreakdowns(List<Transaction> transactions, WeeklyReport report) {
		
		//Group transactions by category
		Map<Category, List<Transaction>> transactionsByCategory = transactions.stream()
				.collect(Collectors.groupingBy(Transaction::getCategory));
		
		//Create breakdown for each category
		List<CategorySpending> breakdowns = new ArrayList<>();
		
		transactionsByCategory.forEach((category, categoryTransactions) -> {
			BigDecimal categoryTotal = calculateTotalSpent(categoryTransactions);
			
			CategorySpending breakdown = new CategorySpending();
			breakdown.setWeeklyReport(report);
			breakdown.setCategory(category);
			breakdown.setAmountSpent(categoryTotal);
			breakdown.setTransactionCount(categoryTransactions.size());
			
			breakdowns.add(breakdown);
		});
		
		return breakdowns;
	}
	
	//Get current week report (generate if doesn't exist)
	public WeeklyReport getCurrentWeekReport() {
		LocalDate currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
		
		//Try to find existing report first
		Optional<WeeklyReport> existing = weeklyReportRepository.findByWeekStartDate(currentWeekStart);
		
		if (existing.isPresent()) {
			return existing.get();
		} else {
			//Generate new report if doesn't exist
			return generateWeeklyReport(currentWeekStart);
		}
	}
	
	//Get reports for last N weeks
	public List<WeeklyReport> getRecentReports(int numberOfWeeks) {
		LocalDate cutoffDate = LocalDate.now().minusWeeks(numberOfWeeks);
		return weeklyReportRepository.findReportsFromDate(cutoffDate);
	}
	
	/*
	//Compare this week vs last week
	public WeeklyComparison compareWithPreviousWeek() {
		WeeklyReport currentWeek = getCurrentWeekReport();
		LocalDate previousWeekStart = currentWeek.getWeekStartDate().minusWeeks(1);
		
		Optional<WeeklyReport> previousWeek = weeklyReportRepository.findByWeekStartDate(previousWeekStart);
		
		if (previousWeek.isPresent()) {
			return new WeeklyComparison(currentWeek, previousWeek.get());
		} else {
			return new WeeklyComparison(currentWeek, null);
		}
	}
	*/

}
