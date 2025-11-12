package com.pavan.expensetracker.service;

import com.pavan.expensetracker.dto.ExpenseRequest;
import com.pavan.expensetracker.dto.ExpenseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseRequest request);
    ExpenseResponse getExpenseById(Long id);
    Page<ExpenseResponse> getAllExpenses(Pageable pageable);
    Page<ExpenseResponse> getExpensesFiltered(String category, LocalDate from, LocalDate to, Pageable pageable);
    List<ExpenseResponse> getExpensesByUser(Long userId);
    ExpenseResponse updateExpense(Long id, ExpenseRequest request);
    void deleteExpense(Long id);
}
