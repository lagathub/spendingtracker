package com.lagathub.spendingtracker.repository;

import com.lagathub.spendingtracker.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	//Find category by name (case-sensitive)
	Optional<Category> findByNameIgnoreCase(String name);
	
	//Get all categories ordered alphabetically
	List<Category> findAllByOrderByNameAsc();
	
	//Checks if category exists by name
	boolean existsByNameIgnoreCase(String name);
	
	//Business-specific queries
	//Find categories that have been used (have transactions)
	@Query("SELECT DISTINCT c FROM Category c INNER JOIN c.transactions t")
	List<Category> findCategoriesWithTransactions();
	/*
	 * // In CategoryRepository - add index hint for better performance
@Query("SELECT DISTINCT c FROM Category c INNER JOIN FETCH c.transactions t")
List<Category> findCategoriesWithTransactionsEager();
	 */
	
	//Count how many transactions each category has
	@Query("Select c.name, COUNT(t) FROM Category c LEFT JOIN c.transactions t GROUP BY c.id, c.name")
	List<Object[]> findCategoryTransactionCounts();
	/*
	 * // Current - returns Object[] which is hard to work with:
@Query("Select c.name, COUNT(t) FROM Category c LEFT JOIN c.transactions t GROUP BY c.id, c.name")
List<Object[]> findCategoryTransactionCounts();

// Better approach - create a projection interface:
@Query("SELECT c.name as categoryName, COUNT(t) as transactionCount FROM Category c LEFT JOIN c.transactions t GROUP BY c.id, c.name")
List<CategoryTransactionCount> findCategoryTransactionCounts();

// Then create this interface in your dto package:
public interface CategoryTransactionCount {
    String getCategoryName();
    Long getTransactionCount();
}
	 */

}
