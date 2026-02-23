package com.apianalyzer.analytics.repository;

import com.apianalyzer.analytics.model.ApiMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Entity definition for ApiMetric (minimal version for analytics)
@Repository
public interface ApiMetricRepository extends JpaRepository<ApiMetric, Long> {
    List<ApiMetric> findByCreatedAtAfter(LocalDateTime dateTime);

    @Query("SELECT m FROM ApiMetric m WHERE m.path = :path AND m.createdAt BETWEEN :start AND :end")
    List<ApiMetric> findMetricsForPath(String path, LocalDateTime start, LocalDateTime end);
}
