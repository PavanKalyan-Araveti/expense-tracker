package com.pavan.expensetracker.service.impl;

import com.pavan.expensetracker.dto.ExpenseRequest;
import com.pavan.expensetracker.dto.ExpenseResponse;
import com.pavan.expensetracker.exception.ResourceNotFoundException;
import com.pavan.expensetracker.model.Expense;
import com.pavan.expensetracker.repository.ExpenseRepository;
import com.pavan.expensetracker.service.ExpenseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository repo;

    public ExpenseServiceImpl(ExpenseRepository repo) {
        this.repo = repo;
    }

    private ExpenseResponse toResponse(Expense e){
        return ExpenseResponse.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .category(e.getCategory())
                .description(e.getDescription())
                .amount(e.getAmount())
                .date(e.getDate())
                .build();
    }

    private Expense fromRequest(ExpenseRequest r){
        return Expense.builder()
                .userId(r.getUserId())
                .category(r.getCategory())
                .description(r.getDescription())
                .amount(r.getAmount())
                .date(r.getDate() != null ? r.getDate() : LocalDate.now())
                .build();
    }


    @Override
    @CacheEvict(value = { "allExpenses" }, allEntries = true)
    public ExpenseResponse createExpense(ExpenseRequest request) {
        Expense e = repo.save(fromRequest(request));
        return toResponse(e);
    }

    @Override
    @Cacheable(value = "expenseById", key = "#id")
    public ExpenseResponse getExpenseById(Long id) {
        Expense e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: "+id));
        return toResponse(e);
    }

    @Override
    @Cacheable(
            value = "allExpenses",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
    )
    public Page<ExpenseResponse> getAllExpenses(Pageable pageable) {
        Page<Expense> page = repo.findAll(pageable);
        List<ExpenseResponse> response = page.getContent().stream().map(this::toResponse).collect(Collectors.toList());
        return new PageImpl<>(response, pageable, page.getTotalElements());
    }

    @Override
    public Page<ExpenseResponse> getExpensesFiltered(String category, LocalDate from, LocalDate to, Pageable pageable) {
        Page<Expense> page;
        if (category != null && from != null && to != null) {
            page = repo.findByCategoryIgnoreCaseAndDateBetween(category, from, to, pageable);
        }
        else if(category != null){
            page = repo.findByCategoryIgnoreCase(category, pageable);
        }
        else if(from != null && to != null){
            page = repo.findByDateBetween(from, to, pageable);
        }
        else{
            page = repo.findAll(pageable);
        }
        List<ExpenseResponse> response = page.getContent().stream().map(this::toResponse).collect(Collectors.toList());
        return new PageImpl<>(response, pageable, page.getTotalElements());
    }

    @Override
    public List<ExpenseResponse> getExpensesByUser(Long userId) {
        return repo.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = { "allExpenses", "expenseById" }, allEntries = true)
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        Expense existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: "+id));
        existing.setUserId(request.getUserId());
        existing.setCategory(request.getCategory());
        existing.setDescription(request.getDescription());
        existing.setAmount(request.getAmount());
        existing.setDate(request.getDate() != null ? request.getDate() : existing.getDate());
        Expense saved = repo.save(existing);
        return toResponse(saved);
    }

    @Override
    @CacheEvict(value = { "allExpenses", "expenseById" }, allEntries = true)
    public void deleteExpense(Long id) {
        Expense existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: "+id));
        repo.delete(existing);
    }
}
