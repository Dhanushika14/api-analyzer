package com.apianalyzer.analytics.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;
    private String method;
    private Long latency;
    private Integer statusCode;
    private LocalDateTime createdAt;


}