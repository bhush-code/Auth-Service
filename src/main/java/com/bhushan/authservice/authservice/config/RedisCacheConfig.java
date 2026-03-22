package com.bhushan.authservice.authservice.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * Redis Cache Configuration with fallback mechanism to ConcurrentMapCacheManager
 * if Redis is unavailable. This ensures the application continues to function
 * even when Redis is down, using in-memory caching as a fallback.
 */
@Configuration
public class RedisCacheConfig {

    private static final Logger logger = LogManager.getLogger(RedisCacheConfig.class);

    /**
     * Configures CacheManager with Redis primary and fallback to ConcurrentMapCacheManager
     * when Redis is unavailable.
     */
    @Bean
    public CacheManager cacheManager(ObjectProvider<RedisConnectionFactory> redisConnectionFactory) {
        RedisConnectionFactory factory = redisConnectionFactory.getIfAvailable();

        if (factory != null) {
            try {
                // Try to create Redis cache manager
                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(15))
                        .disableCachingNullValues();

                logger.info("Redis cache manager configured successfully");
                return RedisCacheManager.builder(factory)
                        .cacheDefaults(config)
                        .transactionAware()
                        .build();
            } catch (Exception e) {
                logger.warn("Failed to configure Redis cache manager, falling back to ConcurrentMapCacheManager: {}",
                           e.getMessage());
                return createFallbackCacheManager();
            }
        }

        logger.warn("RedisConnectionFactory not available, using ConcurrentMapCacheManager as fallback");
        return createFallbackCacheManager();
    }

    /**
     * Creates a fallback CacheManager using in-memory ConcurrentMapCacheManager
     */
    private CacheManager createFallbackCacheManager() {
        logger.info("Creating fallback ConcurrentMapCacheManager");
        return new ConcurrentMapCacheManager(
            "User",
            "userByEmail",
            "RefreshToken",
            "UserProfile"
        );
    }

    /**
     * Custom error handler to gracefully handle cache errors and log them
     */
    @Bean
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                logger.warn("Redis GET operation failed for key: {}. Falling back to database. Error: {}",
                           key, exception.getMessage(), exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
                logger.warn("Redis PUT operation failed for key: {}. Application will continue with database fallback. Error: {}",
                           key, exception.getMessage(), exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                logger.warn("Redis EVICT operation failed for key: {}. Error: {}",
                           key, exception.getMessage(), exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                logger.warn("Redis CLEAR operation failed. Error: {}",
                           exception.getMessage(), exception);
            }
        };
    }


}
