package com.pavan.expensetracker.controller;

import com.pavan.expensetracker.dto.ExpenseRequest;
import com.pavan.expensetracker.dto.ExpenseResponse;
import com.pavan.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService service;

    public ExpenseController(ExpenseService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest req){
        ExpenseResponse res = service.createExpense(req);
        return ResponseEntity.status(201).body(res);
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(service.getExpensesFiltered(category, fromDate, toDate, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(service.getExpenseById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseResponse>> getByUser(@PathVariable Long userId){
        return ResponseEntity.ok(service.getExpensesByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest req){
        return ResponseEntity.ok(service.updateExpense(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id){
        service.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

}
