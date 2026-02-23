package com.apianalyzer.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiMetricEvent {
    private String path;
    private String method;
    private Long latency;
    private Integer statusCode;
    private Boolean success;
    private String errorMessage;
    private String userId;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();
}