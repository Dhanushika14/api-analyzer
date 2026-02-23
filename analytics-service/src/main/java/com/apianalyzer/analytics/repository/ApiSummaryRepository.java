package com.apianalyzer.analytics.repository;

import com.apianalyzer.analytics.model.ApiSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ApiSummaryRepository extends JpaRepository<ApiSummary, Long> {
    Optional<ApiSummary> findByPath(String path);
    List<ApiSummary> findAll();
}