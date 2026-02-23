package com.apianalyzer.metrics.repository;

import com.apianalyzer.metrics.model.ApiMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiMetricRepository extends JpaRepository<ApiMetric, Long> {

    List<ApiMetric> findByPathAndCreatedAtAfter(String path, LocalDateTime since);

    List<ApiMetric> findByCreatedAtAfter(LocalDateTime since);

    @Query("SELECT DISTINCT m.path FROM ApiMetric m")
    List<String> findAllDistinctPaths();

}