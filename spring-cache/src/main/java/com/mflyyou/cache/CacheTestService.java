package com.mflyyou.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static com.mflyyou.cache.RedisCachingConfig.PERPETUAL_CACHE;


@Service
@CacheConfig(cacheNames = "test::cache")
public class CacheTestService {
    @Cacheable()
    public Map<String, String> test(String test) {
        var time = LocalDateTime.now().toString();
        System.out.println(time);
        return Map.of(time, "执行了结果" + time);
    }

    @Cacheable(PERPETUAL_CACHE)
    public Map<String, String> test3(String name) {
        var time = LocalDateTime.now().toString();
        System.out.println(time);
        return Map.of(time, "执行了结果" + time);
    }

    @Cacheable(value = PERPETUAL_CACHE)
    public Map<String, String> test444(String name) {
        return Map.of();
    }

    @CacheEvict(allEntries = true)
    public void remove() {

    }
}

