package com.lagathub.spendingtracker.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;



@Entity //This class maps to a database table
@Table(name = "weekly_reports") //Table name in PostgreSQL -- uses the snake-case for table names
public class WeeklyReport {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private LocalDate weekStartDate;  //just the date, not time
	
	@Column(nullable = false)
	private LocalDate weekEndDate;
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal totalSpent;
	
	@Column(nullable = false)
	private LocalDateTime generatedAt;
	
	/*
	 * @OneToMany: One report has many category breakdowns
	 * mappedBy: Category spending has a field called "weeklyReport" that owns this r/ship
	 * cascade = CascadeType.All: When report is deleted, delete its breakdowns too 
	 */
	@OneToMany(mappedBy = "weeklyReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<CategorySpending> categoryBreakdowns = new ArrayList<>();
	
	public WeeklyReport() {
		
	}
	
	//Business constructor
	public WeeklyReport(LocalDate weekStartDate, LocalDate weekEndDate, BigDecimal totalSpent) {
		this.weekStartDate = weekStartDate;
		this.weekEndDate = weekEndDate;
		this.totalSpent = totalSpent;
		this.generatedAt = LocalDateTime.now();
		this.categoryBreakdowns = new ArrayList<>();
	}
	
	public Long getId() {
		return id;
	}
	
	public LocalDate getWeekStartDate() {
		return weekStartDate;
	}
	
	public void setWeekStartDate(LocalDate weekStartDate) {
		this.weekStartDate = weekStartDate;
	}
	
	public LocalDate getWeekEndDate() {
		return weekEndDate;
	}
	
	public void setWeekEndDate(LocalDate weekEndDate) {
		this.weekEndDate = weekEndDate;
	}
	
	public BigDecimal getTotalSpent() {
		return totalSpent;
	}
	
	public void setTotalSpent(BigDecimal totalSpent) {
		this.totalSpent = totalSpent;
	}
	
	public LocalDateTime getGeneratedAt() {
		return generatedAt;
	}
	
	public void setGeneratedAt(LocalDateTime generatedAt) {
		this.generatedAt = generatedAt;
	}
	
	public List<CategorySpending> getCategoryBreakdowns() {
		return categoryBreakdowns;
	}
	
	public void setCategoryBreakdowns(List<CategorySpending> categoryBreakdowns) {
		this.categoryBreakdowns = categoryBreakdowns;
		//set bidirectional r/ship
		if (categoryBreakdowns != null) {
			categoryBreakdowns.forEach(breakdown -> breakdown.setWeeklyReport(this));
		}
	}
	
	//Add business logic methods: Helper method for adding individual breakdowns
	public void addCategoryBreakdown(CategorySpending breakdown) {
		this.categoryBreakdowns.add(breakdown);
		breakdown.setWeeklyReport(this); //Maintain bidirectional r/ship
	}
	
	//Calculate total from breakdowns(useful for validation)
	public BigDecimal calculateTotalFromBreakdowns() {
		return categoryBreakdowns.stream()
				.map(CategorySpending::getAmountSpent)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}
