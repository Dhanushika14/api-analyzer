package com.apianalyzer.analytics.service;

import com.apianalyzer.analytics.model.ApiSummary;
import com.apianalyzer.analytics.repository.ApiMetricRepository;
import com.apianalyzer.analytics.repository.ApiSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ApiMetricRepository metricRepository;
    private final ApiSummaryRepository summaryRepository;

    @Scheduled(fixedRate = 60000) // Run every minute
    public void computeAnalytics() {
        log.info("Computing analytics...");

        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);

        Map<String, List<Long>> latenciesByPath = new HashMap<>();
        Map<String, Integer> successCountByPath = new HashMap<>();
        Map<String, Integer> failureCountByPath = new HashMap<>();

        metricRepository.findByCreatedAtAfter(oneMinuteAgo).forEach(metric -> {
            latenciesByPath.computeIfAbsent(metric.getPath(), k -> new ArrayList<>())
                    .add(metric.getLatency());

            if (metric.getStatusCode() >= 200 && metric.getStatusCode() < 300) {
                successCountByPath.merge(metric.getPath(), 1, Integer::sum);
            } else {
                failureCountByPath.merge(metric.getPath(), 1, Integer::sum);
            }
        });

        latenciesByPath.forEach((path, latencies) -> {
            ApiSummary summary = new ApiSummary();
            summary.setPath(path);
            summary.setP95Latency(calculatePercentile(latencies, 95));
            summary.setP99Latency(calculatePercentile(latencies, 99));
            summary.setAverageLatency((long) latencies.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0));

            int totalRequests = successCountByPath.getOrDefault(path, 0) +
                    failureCountByPath.getOrDefault(path, 0);
            int failedRequests = failureCountByPath.getOrDefault(path, 0);
            summary.setErrorRate((double) failedRequests / totalRequests * 100);
            summary.setThroughput(totalRequests);

            summary.setRecommendations(generateRecommendations(summary));
            summary.setComputedAt(LocalDateTime.now());

            summaryRepository.save(summary);
        });

        log.info("Analytics computed for {} endpoints", latenciesByPath.size());
    }

    private Long calculatePercentile(List<Long> values, int percentile) {
        if (values.isEmpty()) return 0L;

        Collections.sort(values);
        int index = (int) Math.ceil((percentile / 100.0) * values.size()) - 1;
        return values.get(Math.max(0, index));
    }

    private String generateRecommendations(ApiSummary summary) {
        StringBuilder recommendations = new StringBuilder();

        if (summary.getP95Latency() > 800) {
            recommendations.append("Consider implementing caching. ");
        }
        if (summary.getErrorRate() > 5) {
            recommendations.append("High error rate detected. Consider rate limiting. ");
        }
        if (summary.getThroughput() > 100) {
            recommendations.append("High throughput. Consider horizontal scaling. ");
        }
        if (summary.getAverageLatency() > 500) {
            recommendations.append("Optimize database queries or add CDN. ");
        }

        return recommendations.toString().isEmpty() ? "All metrics healthy" : recommendations.toString();
    }
}
