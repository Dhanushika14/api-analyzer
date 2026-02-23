package com.apianalyzer.gateway.filter;

import com.apianalyzer.gateway.model.ApiMetricEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class MetricsFilter implements GlobalFilter, Ordered {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Skip OPTIONS preflight requests completely
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        long startTime = System.currentTimeMillis();
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().toString();
        String userId = extractUserIdFromToken(exchange);

        return chain.filter(exchange)
                .doFinally(signal -> {
                    long latency = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null ?
                            exchange.getResponse().getStatusCode().value() : 500;
                    boolean success = statusCode >= 200 && statusCode < 400;

                    ApiMetricEvent event = ApiMetricEvent.builder()
                            .path(path)
                            .method(method)
                            .latency(latency)
                            .statusCode(statusCode)
                            .success(success)
                            .userId(userId)
                            .build();

                    try {
                        String eventJson = objectMapper.writeValueAsString(event);
                        kafkaTemplate.send("api-metrics", eventJson);
                        log.debug("Metric event sent to Kafka: {} - {}ms", path, latency);
                    } catch (Exception e) {
                        log.error("Error sending metric to Kafka", e);
                    }
                });
    }

    private String extractUserIdFromToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Extract token without "Bearer "
        }
        return "anonymous";
    }

    @Override
    public int getOrder() {
        return -1; // Execute before other filters
    }
}