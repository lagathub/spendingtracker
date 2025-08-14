package com.lagathub.spendingtracker.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "transactions")
public class Transaction {
	@Id //This field is the primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY) //Auto increment
	private Long id;
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount; //Use BigDecimal for money, not double
	
	@ManyToOne(fetch = FetchType.LAZY) //Many transactions can have one category
	@JoinColumn(name = "category_id", nullable = false) //Foreign key column
	private Category category;
	
	@Column(nullable = false) //Required field
	private LocalDateTime createdAt;
	
	@Column(nullable = true) //Optional field
	private String note;
	
	//Business logic: Is this a small expense?
	public boolean isSmallExpense() {
		return amount.compareTo(new BigDecimal("1000")) < 0;
	}
	
	//Business logic: Get formatted amount
	public String getFormattedAmount() {
		return "Ksh " + amount;
	}
	
	//Validation: Ensure amount is positive
	public void validateAmount() {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}
	}
	
	//Default constructor (Jpa requires this)
	public Transaction() {
		
	}
	
	//Constructor for creating new transactions
	public Transaction (BigDecimal amount, Category category) {
		this.amount = amount;
		this.category = category;
		this.createdAt = LocalDateTime.now();
		validateAmount(); //validate on creation
	}
	
	//Constructor with notes
	public Transaction (BigDecimal amount, Category category, String note) {
		this.amount = amount;
		this.category = category;
		this.note = note;
		this.createdAt = LocalDateTime.now();
		validateAmount(); //validate on creation
	}
	
	public Long getId() {
		return id;
	}
	
	//No setId() - ID is managed by database
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
		validateAmount(); //Validate when setting
	}
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}

	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt() {
		this.createdAt = LocalDateTime.now();
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	


}
