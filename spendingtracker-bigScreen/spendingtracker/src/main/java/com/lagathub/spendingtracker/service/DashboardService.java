package com.lagathub.spendingtracker.service;

import com.lagathub.spendingtracker.dto.response.DashboardSummaryResponse;
import com.lagathub.spendingtracker.dto.response.WeeklyStatsResponse;
import com.lagathub.spendingtracker.dto.response.WeeklyTrendResponse;
import com.lagathub.spendingtracker.dto.response.TrendDataResponse;
import com.lagathub.spendingtracker.repository.TransactionRepository;
import com.lagathub.spendingtracker.repository.WeeklyReportRepository;
import com.lagathub.spendingtracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {
	
	private final TransactionRepository transactionRepository;
	private final WeeklyReportRepository weeklyReportRepository;
	private final CategoryRepository categoryRepository;
	
	@Autowired
	public DashboardService(TransactionRepository transactionRepository,
			                WeeklyReportRepository weeklyReportRepository,
			                CategoryRepository categoryRepository) {
		this.transactionRepository = transactionRepository;
		this.weeklyReportRepository = weeklyReportRepository;
		this.categoryRepository = categoryRepository;
	}
	
	public DashboardSummaryResponse getDashboardSummary() {
		BigDecimal totalSpent = getTotalSpent();
		WeeklyStatsResponse thisWeek = getCurrentWeekStats();
		TrendDataResponse trend = calculateTrend();
		
		return DashboardSummaryResponse.builder()
				.totalSpent(totalSpent)
				.weeklyStats(thisWeek)
				.trendData(trend)
				.build();
	}
	
	public List<WeeklyTrendResponse> getWeeklyTrend() {
		LocalDate sixWeeksAgo = LocalDate.now().minusWeeks(6);
		List<Object[]> rawData = transactionRepository.findWeeklySpendingData(sixWeeksAgo);
		
		return rawData.stream()
				.map(this::mapToWeeklyTrendResponse)
				.collect(Collectors.toList());
	}
	
	private BigDecimal getTotalSpent() {
		return transactionRepository.sumAllTransactionAmounts();
	}
	
	private WeeklyStatsResponse getCurrentWeekStats() {
	    LocalDateTime weekStart = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0);
	    LocalDateTime weekEnd = weekStart.plusDays(6).withHour(23).withMinute(59);

	    BigDecimal weeklySpent = transactionRepository.sumAmountByDateRange(weekStart, weekEnd);
	    if (weeklySpent == null) {
	        weeklySpent = BigDecimal.ZERO;
	    }

	    Long transactionCount = transactionRepository.countTransactionsByDateRange(weekStart, weekEnd);
	    Long categoryCount = categoryRepository.countActiveCategoriesThisWeek(weekStart, weekEnd);

	    BigDecimal dailyAverage = weeklySpent.divide(BigDecimal.valueOf(7), 2, BigDecimal.ROUND_HALF_UP);

	    return WeeklyStatsResponse.builder()
	            .spent(weeklySpent)
	            .transactions(transactionCount.intValue())
	            .categories(categoryCount.intValue())
	            .dailyAverage(dailyAverage)
	            .build();
	}


	private TrendDataResponse calculateTrend() {
	    LocalDateTime thisMonthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0);
	    LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
	    LocalDateTime lastMonthEnd = thisMonthStart.minusSeconds(1);
	    
	    BigDecimal thisMonthSpent = transactionRepository.sumAmountByDateRange(thisMonthStart, LocalDateTime.now());
	    BigDecimal lastMonthSpent = transactionRepository.sumAmountByDateRange(lastMonthStart, lastMonthEnd);
	    
	    if (lastMonthSpent == null) {
	        lastMonthSpent = BigDecimal.ZERO;
	    }
	    if (thisMonthSpent == null) {
	        thisMonthSpent = BigDecimal.ZERO;
	    }
	    
	    if (lastMonthSpent.compareTo(BigDecimal.ZERO) == 0) {
	        return TrendDataResponse.builder()
	            .direction("up")
	            .percentage(0.0)
	            .build();
	    }
	    
	    BigDecimal difference = thisMonthSpent.subtract(lastMonthSpent);
	    BigDecimal percentage = difference.divide(lastMonthSpent, 4, BigDecimal.ROUND_HALF_UP)
	                                      .multiply(BigDecimal.valueOf(100));
	    
	    return TrendDataResponse.builder()
	        .direction(percentage.compareTo(BigDecimal.ZERO) >= 0 ? "up" : "down")
	        .percentage(Math.abs(percentage.doubleValue()))
	        .build();
	}

    
    private WeeklyTrendResponse mapToWeeklyTrendResponse(Object[] rawData) {
        LocalDate weekStart = ((java.sql.Date) rawData[0]).toLocalDate();
        BigDecimal totalSpent = (BigDecimal) rawData[1];
        Long transactionCount = (Long) rawData[2];
        
        return WeeklyTrendResponse.builder()
            .weekStart(weekStart.format(DateTimeFormatter.ofPattern("MMM dd")))
            .totalSpent(totalSpent)
            .transactionCount(transactionCount.intValue())
            .build();
    }
}
