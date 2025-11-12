package com.pavan.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {

    private Long userId;

    @NotBlank(message = "Category required")
    private String category;
    private String description;
    @NotNull(message = "Amount required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    private LocalDate date;
}
