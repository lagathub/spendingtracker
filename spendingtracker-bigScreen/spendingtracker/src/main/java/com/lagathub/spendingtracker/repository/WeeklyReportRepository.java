package com.lagathub.spendingtracker.repository;

import org.springframework.stereotype.Repository;
import com.lagathub.spendingtracker.domain.model.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.math.BigDecimal;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long>{
	
	//Find report for a specific week
	Optional<WeeklyReport> findByWeekStartDate(LocalDate weekStartDate);
	
	//Get all reports ordered by date (newest first)
	List<WeeklyReport> findAllByOrderByWeekStartDateDesc();
	
	//Check if report exists for a week
	boolean existsByWeekStartDate(LocalDate weekStartDate);
	
	//Get reports from the last N weeks
	@Query("SELECT wr FROM WeeklyReport wr WHERE wr.weekStartDate >= :fromDate ORDER BY wr.weekStartDate DESC")
	List<WeeklyReport> findReportsFromDate(@Param("fromDate") LocalDate fromDate);
	
	//Get average weekly spending
	@Query("SELECT AVG(wr.totalSpent) FROM WeeklyReport wr WHERE wr.weekStartDate >= :fromDate")
	BigDecimal calculateAverageWeeklySpending(@Param("fromDate") LocalDate fromDate);
	
	/*
	 * introduced!
	 */
	//find reports in date range
	@Query("SELECT wr FROM WeeklyReport wr WHERE wr.weekStartDate BETWEEN :startDate AND :endDate ORDER BY wr.weekStartDate ASC")
	List<WeeklyReport> findReportsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	// Get total spending across all weeks
	@Query("SELECT SUM(wr.totalSpent) FROM WeeklyReport wr")
	BigDecimal getTotalSpendingAllTime();

}
