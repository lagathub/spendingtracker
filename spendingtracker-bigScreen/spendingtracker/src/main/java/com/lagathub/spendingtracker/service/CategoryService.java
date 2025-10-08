package com.lagathub.spendingtracker.service;

import com.lagathub.spendingtracker.domain.model.Category;
import java.util.Optional;
import java.util.List;

import com.lagathub.spendingtracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lagathub.spendingtracker.exception.CategoryNotFoundException;
import com.lagathub.spendingtracker.exception.DuplicateCategoryException;

@Service
@Transactional //For db consistency
public class CategoryService {
	
	private final CategoryRepository categoryRepository;
	
	//Constructor - Spring will inject the repository
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
		
		//Operation 1: Find existing category or create new one
		public Category findOrCreateCategory(String categoryName) {
			//Business rule: category names should not be empty
			if (categoryName == null || categoryName.trim().isEmpty()) {
				throw new IllegalArgumentException("Category name cannot be empty");
			}
			
			//Keep original case for display, but search case-insensitive
			String trimmedName = categoryName.trim();
			
			//Try to find existing Category
			Optional<Category> existing = categoryRepository.findByNameIgnoreCase(trimmedName);
			
			if (existing.isPresent()) {
				return existing.get();
			} else {
				//Create new category
				Category newCategory = new Category(trimmedName);
				return categoryRepository.save(newCategory);
			}
		}
		
		//Operation 2: Get all categories for dropdown lists
		public List<Category> getAllCategories() {
			return categoryRepository.findAllByOrderByNameAsc();
		}
		
		//Operation 3: Get categories that have been used (have transactions)
		public List<Category> getUsedCategories() {
			return categoryRepository.findCategoriesWithTransactions();
		}
		
		//Operation 4: Rename a category
		public Category renameCategory(Long categoryId, String newName) {
			//Validation
			validateCategoryName(newName);
			
			//Find existing
			Category category = categoryRepository.findById(categoryId)
					.orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found"));
			
			String trimmedNewName = newName.trim();
			
			//Check if new name already exists (but not the same category)
			Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(trimmedNewName); 
	        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(categoryId)) {
	            throw new DuplicateCategoryException("Category with name '" + trimmedNewName + "' already exists");
	        }
			
			//Update and save
			category.setName(trimmedNewName);
			return categoryRepository.save(category);
		}
		
		//Private helper method
		private void validateCategoryName(String name) {
			if (name == null || name.trim().isEmpty()) {
				throw new IllegalArgumentException("Category name cannot be empty");
			}
			if (name.length() > 50) {
				throw new IllegalArgumentException("Categroy name too long (max 50 characters)");
			
		}
	}

}
