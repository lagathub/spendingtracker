package com.lagathub.spendingtracker.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrendDataResponse {
    private String direction; // "up" or "down"
    private Double percentage;
}