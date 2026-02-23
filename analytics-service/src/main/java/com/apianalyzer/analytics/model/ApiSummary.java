package com.apianalyzer.analytics.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_summary",schema = "api_analyser")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path;

    @Column(name = "p95_latency")
    private Long p95Latency;

    @Column(name = "p99_latency")
    private Long p99Latency;

    @Column(name = "average_latency")
    private Long averageLatency;

    @Column(name = "error_rate")
    private Double errorRate;

    @Column(name = "throughput")
    private Integer throughput;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "computed_at")
    private LocalDateTime computedAt;
}