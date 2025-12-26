package com.pavan.expensetracker.service;

import com.pavan.expensetracker.dto.ExpenseRequest;
import com.pavan.expensetracker.exception.ResourceNotFoundException;
import com.pavan.expensetracker.model.Expense;
import com.pavan.expensetracker.repository.ExpenseRepository;
import com.pavan.expensetracker.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository repo;

    @InjectMocks
    private ExpenseServiceImpl service;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createExpense_valid() {
            ExpenseRequest req = ExpenseRequest.builder()
                    .userId(42L)
                    .category("Food")
                    .description("Lunch")
                    .amount(159.0)
                    .date(LocalDate.of(25,11,13))
                    .build();
            when(repo.save(any(Expense.class))).thenAnswer(invocation -> {
                Expense arg = invocation.getArgument(0);
                arg.setId(100L);
                return arg;
            });

            var res = service.createExpense(req);
            assertNotNull(res);
            assertEquals(100L, res.getId());
        assertEquals(req.getUserId(), res.getUserId());
        assertEquals(req.getCategory(), res.getCategory());
        assertEquals(req.getDescription(), res.getDescription());
        assertEquals(req.getAmount(), res.getAmount());
        assertEquals(req.getDate(), res.getDate());
        verify(repo).save(any(Expense.class));
        verifyNoMoreInteractions(repo);
    }

    @Test
    void updateExpense_success(){
        Long id = 111L;
        LocalDate originalDate = LocalDate.of(2025,10,11);
        LocalDate newDate = LocalDate.of(2025,11,19);
        Expense existing = Expense.builder()
                .id(id)
                .userId(1L)
                .category("Old Exp")
                .description("lunch")
                .amount(100.0)
                .date(originalDate)
                .build();
        ExpenseRequest req = ExpenseRequest.builder()
                .userId(2L)
                .category("New Exp")
                .description("Dinner")
                .amount(200.0)
                .date(newDate)
                .build();

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.updateExpense(id, req);
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(req.getUserId(), response.getUserId());
        assertEquals(req.getCategory(), response.getCategory());
        assertEquals(req.getDescription(), response.getDescription());
        assertEquals(req.getAmount(), response.getAmount());
        assertEquals(req.getDate(), response.getDate());

        verify(repo).findById(id);
        verify(repo).save(any(Expense.class));
        verifyNoMoreInteractions(repo);

    }

    @Test
    void deleteExpense_Success(){
        Long id = 100L;
        Expense existing = Expense.builder()
                .id(id)
                .userId(1L)
                .category("Old Exp")
                .description("lunch")
                .amount(100.0)
                .date(LocalDate.now())
                .build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        service.deleteExpense(id);
        verify(repo).findById(id);
        verify(repo).delete(existing);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteExpense_notFound(){
        Long id = 134L;
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deleteExpense(id));
        verify(repo).findById(id);
        verify(repo, never()).delete(any(Expense.class));
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getExpenseById_success() {
        Long id = 111L;
        Expense existing = Expense.builder()
                .id(id)
                .userId(1L)
                .category("Old Exp")
                .description("lunch")
                .amount(100.0)
                .date(LocalDate.now())
                .build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        var res = service.getExpenseById(id);
        assertNotNull(res);
        assertEquals(id, res.getId());
        assertEquals(existing.getUserId(), res.getUserId());
        assertEquals(existing.getCategory(), res.getCategory());
        assertEquals(existing.getDescription(), res.getDescription());
        assertEquals(existing.getAmount(), res.getAmount());
        assertEquals(existing.getDate(), res.getDate());
        verify(repo).findById(id);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getExpenseById_notFound(){
        Long id = 443L;
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getExpenseById(id));
        verify(repo).findById(id);
        verifyNoMoreInteractions(repo);
    }
}
