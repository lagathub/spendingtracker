package com.lagathub.spendingtracker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardSummaryResponse {
    private BigDecimal totalSpent;
    private WeeklyStatsResponse weeklyStats;
    private TrendDataResponse trendData;
}