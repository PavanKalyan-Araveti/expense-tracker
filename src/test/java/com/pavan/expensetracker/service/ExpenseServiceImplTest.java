package com.pavan.expensetracker.service;

import com.pavan.expensetracker.dto.ExpenseRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
