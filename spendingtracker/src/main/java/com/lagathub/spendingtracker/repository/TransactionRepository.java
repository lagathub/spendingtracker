package com.lagathub.spendingtracker.repository;

import com.lagathub.spendingtracker.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
	//Spring automatically generates SQL from method names
	
	//Find transaction between two dates -- Method name queries (simple)
	List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
	
	//Find transaction by category name
	List<Transaction> findByCategoryName(String categoryName);
	
	//Find transaction ordered by date(newest first)
	List<Transaction> findAllByOrderByCreatedAtDesc();
	
	
	//custom JPQL queries (complex)
	/*
	 * Mathematical Aggregation:
	 * it adds up all transaction amounts within a date range
	 * Business purpose: "What's my total spending this week?"
	 * Returns a single number(BigDecimal) representing total spent
	 */
	@Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end")
	BigDecimal sumAmountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
	
	/*
	 * Conditional Filtering - Business Logic Query
	 * It finds all transactions below a certain amount, newest first
	 * Why JPQL: the threshold is dynamic (passed as parameter), not fixed
	 * Business purpose: "Show me all my small expenses" --my core use case!
	 * Returns a list of complete Transaction objects
	 */
	@Query("SELECT t FROM Transaction t WHERE t.amount < :threshold ORDER BY t.createdAt DESC")
	List<Transaction> findSmallTransactions(@Param("threshold") BigDecimal threshold);
	
	/*
	 * Complex sorting - Multi-Level Organization
	 * Get's week's transactions sorted by category name first, then by date within each category
	 * Why JPQL: Multi-level sorting across r/ships(category.name)
	 * "Show me this week's spending organized by category"
	 * Returns a list of transactions grouped logically for reports 
	 */
	@Query("SELECT t FROM Transaction t WHERE t.createdAt >= :weekStart AND t.createdAt < :weekEnd ORDER BY t.category.name, t.createdAt DESC")
	List<Transaction> findTransactionsForWeekGroupedByCategory(
			@Param("weekStart") LocalDateTime weekStart,
			@Param("weekEnd") LocalDateTime weekEnd
			);

}
