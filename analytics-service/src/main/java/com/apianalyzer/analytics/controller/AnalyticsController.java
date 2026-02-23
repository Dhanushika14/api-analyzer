package com.apianalyzer.analytics.controller;

import com.apianalyzer.analytics.model.ApiSummary;
import com.apianalyzer.analytics.repository.ApiSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor

public class AnalyticsController {

    private final ApiSummaryRepository summaryRepository;

    @GetMapping("/summaries")
    public ResponseEntity<List<ApiSummary>> getApiSummaries() {
        return ResponseEntity.ok(summaryRepository.findAll());
    }

    @GetMapping("/summaries/{path}")
    public ResponseEntity<ApiSummary> getApiSummary(@PathVariable String path) {
        ApiSummary summary = summaryRepository.findByPath(path).orElse(null);
        return summary != null ? ResponseEntity.ok(summary) : ResponseEntity.notFound().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Analytics Service Running");
    }
}