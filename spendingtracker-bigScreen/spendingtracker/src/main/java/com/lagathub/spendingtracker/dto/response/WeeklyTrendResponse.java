package com.lagathub.spendingtracker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class WeeklyTrendResponse {
    private String weekStart;
    private BigDecimal totalSpent;
    private Integer transactionCount;
}
