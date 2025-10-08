package com.lagathub.spendingtracker.controllers;

import com.lagathub.spendingtracker.dto.response.DashboardSummaryResponse;
import com.lagathub.spendingtracker.dto.response.WeeklyTrendResponse;
import com.lagathub.spendingtracker.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {
	
	private final DashboardService dashboardService;
	
	@Autowired
	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}
	
	@GetMapping("/summary")
	public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
		DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
		return ResponseEntity.ok(summary);
	}
	
	@GetMapping("/weekly-trend")
	public ResponseEntity<List<WeeklyTrendResponse>> getWeeklyTrend() {
		List<WeeklyTrendResponse> trend = dashboardService.getWeeklyTrend();
		return ResponseEntity.ok(trend);
	}

}
