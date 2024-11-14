package com.example.usersservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfig {
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                // CircuitBreaker를 열지 결정하는 failure rate threshold percentage
                // - default : 50
                .failureRateThreshold(4)
                // CircuitBreaker를 open한 상태를 유지하는 지속 기간
                // - 이 기간 이후 half-open 상태
                // default : 60seconds
                .waitDurationInOpenState(Duration.ofMillis(1000))
                // CircuitBreaker가 닫힐 때 통화 결과를 기록하는데 사용되는 슬라이딩 창의 유형 구성
                // - 카운트 기반 또는 시간 기반
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                // CircuitBreaker가 닫힐 때 호출 결과를 기록하는데 사용되는 슬라이딩 윈도우 창의 크기
                // - default : 100
                .slidingWindowSize(2)
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                // TimeLimiter는 future supplier의 time limit 설정
                // - default : 1초
                .timeoutDuration(Duration.ofSeconds(4))
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
    }
}