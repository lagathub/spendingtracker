package com.lagathub.spendingtracker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
@Data
@Builder
public class WeeklyStatsResponse {
    private BigDecimal spent;
    private Integer transactions;
    private Integer categories;
    private BigDecimal dailyAverage;
}
