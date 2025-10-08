package com.lagathub.spendingtracker.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.math.RoundingMode;


import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "category_spending",
		uniqueConstraints = @UniqueConstraint(columnNames = {"weekly_report_id", "category_id"}))
public class CategorySpending {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amountSpent; //Total spent in this category this week
	
	@Column(nullable = false)
	private Integer transactionCount; //How many transactions in this category
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "weekly_report_id", nullable = false)
	private WeeklyReport weeklyReport;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;
	
	public CategorySpending() {
		
	}
	
	public CategorySpending(BigDecimal amountSpent, Integer transactionCount, WeeklyReport weeklyReport, Category category) {
		//validation
		if (amountSpent == null || amountSpent.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Amount spent cannot be null or negative");
		}
		if (transactionCount == null || transactionCount < 0) {
			throw new IllegalArgumentException("Transaction count cannot be null or negative");
		}
		if (weeklyReport == null) {
			throw new IllegalArgumentException("Weekly report cannot be null");
		}
		if (category == null) {
			throw new IllegalArgumentException("Category cannot be null");
		}
		
		this.amountSpent = amountSpent;
		this.transactionCount = transactionCount;
		this.weeklyReport = weeklyReport;
		this.category = category;
		
	}
	
	public Long getId() {
		return id;
	}
	
	public BigDecimal getAmountSpent() {
		return amountSpent;
	}
	
	public void setAmountSpent(BigDecimal amountSpent) {
		this.amountSpent = amountSpent;
	}
	
	public Integer getTransactionCount() {
		return transactionCount;
	}
	
	public void setTransactionCount(Integer transactionCount) {
		this.transactionCount = transactionCount;
	}
	
	public WeeklyReport getWeeklyReport() {
		return weeklyReport;
	}
	
	public void setWeeklyReport(WeeklyReport weeklyReport) {
		this.weeklyReport = weeklyReport;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	//Business methods that make the class more useful:
	
	/**
	 * Calculate average amount per transaction in this category
	 */
	public BigDecimal getAveragePerTransaction() {
		if (transactionCount == 0) {
			return BigDecimal.ZERO;
		}
		return amountSpent.divide(new BigDecimal(transactionCount), 2, RoundingMode.HALF_UP);
	}
	
	/**
	 * Get percentage of total week spending (useful for reports)
	 */
	public BigDecimal getPercentageOfWeekTotal() {
		BigDecimal weekTotal = weeklyReport.getTotalSpent();
		if (weekTotal.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		return amountSpent.divide(weekTotal, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
	}
	
	/**
	 * Override equals() and hashCode() ---Important for collections
 	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CategorySpending)) return false;
		CategorySpending that = (CategorySpending) o;
		return Objects.equals(weeklyReport, that.weeklyReport) &&
				Objects.equals(category,  that.category);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(weeklyReport, category);
	}
	
	//toString() for Debugging
	@Override
	public String toString() {
		return "CategorySpending{" +
				"category=" + (category != null ? category.getName() : "null") +
				", amountSpent=" +amountSpent +
				", transactionCount=" + transactionCount +
				", weekStartDate=" + (weeklyReport != null ? weeklyReport.getWeekStartDate() : "null") +
				'}';
	}
	

}
