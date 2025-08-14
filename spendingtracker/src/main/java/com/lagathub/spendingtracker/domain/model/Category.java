package com.lagathub.spendingtracker.domain.model;

import java.time.LocalDateTime;

//JPA imports
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;


@Entity //This class maps to a database table
@Table(name = "categories") //Table name in PostgreSQL
public class Category {
	
	@Id //This field is the primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increment
	private Long id;
	
	@Column(unique = true, nullable = false) //Name must be unique and not null
	private String name;
	
	@Column //Optional column (can be null)
	private String description;
	
	@Column(nullable = false) //Required field
	private LocalDateTime createdAt;
	
	//Default constructor (JPA requires this)
	public Category () {
		
	}
	
	//Constructor for creating new categories
	public Category (String name) {
		this.name = name;
		this.createdAt = LocalDateTime.now();
	}
	
	//Constructor with description
	public Category(String name, String description) {
		this.name = name;
		this.description = description;
		this.createdAt = LocalDateTime.now();
	}
	
	public Long getId() {
		return id;
	}
	
	//No setId() - ID is managed by database
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = LocalDateTime.now();
	}

}
