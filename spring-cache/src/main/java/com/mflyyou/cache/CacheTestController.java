package com.mflyyou.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CacheTestController {
    private final CacheTestService service;

    @GetMapping("/")
    public Map<String, String> test() {
        service.test3("test3");
        service.test3("test4");
        service.test("test2");
        return service.test("test");
    }

    @GetMapping("/cache")
    public Map map() {
        return service.test444("test3");
    }

    @GetMapping("/remove")
    public void remove() {
        service.remove();
    }

}

