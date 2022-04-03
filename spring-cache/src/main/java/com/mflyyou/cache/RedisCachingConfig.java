package com.mflyyou.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class RedisCachingConfig {
    public static final String PERPETUAL_CACHE = "M_FLY_YOU_PERPETUAL_CACHE";

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(ResourceLoader resourceLoader,
                                                                     ObjectMapper objectMapper,
                                                                     RedisCacheConfiguration redisCacheConfiguration) {
        return (redisCacheManagerBuilder) -> {
            var jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
            jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
            // 配置一个永久的 Cache
            redisCacheManagerBuilder.withCacheConfiguration(PERPETUAL_CACHE,
                    RedisCacheConfiguration.defaultCacheConfig(resourceLoader.getClassLoader())
                            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                            .serializeValuesWith(SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
//                            .disableCachingNullValues()
                            .entryTtl(Duration.ZERO));
            //
            redisCacheManagerBuilder.cacheDefaults(redisCacheConfiguration);
        };
    }

    @Bean
    public CacheManagerCustomizer<?> customizers() {
        return (cacheManager) -> {
            System.out.println(cacheManager.getCacheNames());
        };
    }

    @Bean
    public CacheManagerCustomizers cacheManagerCustomizers(List<CacheManagerCustomizer<?>> customizers) {
        return new CacheManagerCustomizers(customizers);
    }

    @Bean
    public RedisCacheConfiguration createConfiguration(CacheProperties cacheProperties,
                                                       ObjectMapper objectMapper,
                                                       ResourceLoader resourceLoader) {
        var jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig(resourceLoader.getClassLoader())
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
        if (redisProperties.getTimeToLive()!=null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix()!=null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
}
