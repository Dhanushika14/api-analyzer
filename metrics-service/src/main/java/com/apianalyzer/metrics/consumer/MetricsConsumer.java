package com.apianalyzer.metrics.consumer;

import com.apianalyzer.metrics.model.ApiMetric;
import com.apianalyzer.metrics.repository.ApiMetricRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsConsumer {

    private final ApiMetricRepository metricRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "api-metrics", groupId = "metrics-service")
    public void consumeMetric(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, Map.class);

            ApiMetric metric = new ApiMetric();
            metric.setPath((String) data.get("path"));
            metric.setMethod((String) data.get("method"));
            metric.setLatency(Long.parseLong(data.get("latency").toString()));
            metric.setStatusCode((Integer) data.get("statusCode"));
            metric.setCreatedAt(LocalDateTime.now());

            metricRepository.save(metric);
            log.info("Metric saved: {}", metric.getPath());
        } catch (Exception e) {
            log.error("Error consuming metric", e);
        }
    }
}