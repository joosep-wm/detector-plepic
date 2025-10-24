package ee.digit25.detector.common;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache("persons", buildCache(30, 10000));
        cacheManager.registerCustomCache("accounts", buildCache(30, 10000));
        cacheManager.registerCustomCache("devices", buildCache(30, 10000));
        cacheManager.registerCustomCache("transactionHistory", buildCache(5, 5000));
        return cacheManager;
    }

    private com.github.benmanes.caffeine.cache.Cache<Object, Object> buildCache(int ttlSeconds, int maxSize) {
        return Caffeine.newBuilder()
                .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .build();
    }
}
