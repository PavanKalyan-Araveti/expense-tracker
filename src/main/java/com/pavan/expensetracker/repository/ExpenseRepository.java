package com.pavan.expensetracker.repository;

import com.pavan.expensetracker.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);
    Page<Expense> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<Expense> findByDateBetween(LocalDate from, LocalDate to, Pageable pageable);
    Page<Expense> findByCategoryIgnoreCaseAndDateBetween(String category, LocalDate from, LocalDate to, Pageable pageable);
}
