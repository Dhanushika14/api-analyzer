package com.apianalyzer.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_summary", indexes = {
        @Index(name = "idx_api_summary_path", columnList = "path", unique = true),
        @Index(name = "idx_api_summary_computed", columnList = "computed_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String path;

    @Column(nullable = false)
    private Long p95Latency; // milliseconds (95th percentile)

    @Column(nullable = false)
    private Long p99Latency; // milliseconds (99th percentile)

    @Column(nullable = false)
    private Long averageLatency;

    @Column(nullable = false)
    private Double errorRate; // percentage (0-100)

    @Column(nullable = false)
    private Long throughput; // requests per minute

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "total_requests")
    private Long totalRequests;

    @Column(name = "total_errors")
    private Long totalErrors;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
