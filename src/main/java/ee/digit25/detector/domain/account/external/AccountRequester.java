package ee.digit25.detector.domain.account.external;

import ee.bitweb.core.retrofit.RetrofitRequestExecutor;
import ee.digit25.detector.common.ApiRequestDeduplicator;
import ee.digit25.detector.domain.account.external.api.AccountModel;
import ee.digit25.detector.domain.account.external.api.AccountApi;
import ee.digit25.detector.domain.account.external.api.AccountApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRequester {

    private final AccountApi api;
    private final AccountApiProperties properties;
    private final ApiRequestDeduplicator deduplicator;
    private final CacheManager cacheManager;

    @Cacheable(value = "accounts", key = "#accountNumber")
    public AccountModel get(String accountNumber) {
        return deduplicator.deduplicate("account:" + accountNumber, () -> {
            log.info("Requesting account {}", accountNumber);
            return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), accountNumber));
        });
    }

    public List<AccountModel> get(List<String> numbers) {
        log.info("Requesting accounts with numbers {}", numbers);

        Cache cache = cacheManager.getCache("accounts");
        List<AccountModel> results = new ArrayList<>();
        List<String> missingNumbers = new ArrayList<>();

        // Check cache first
        for (String number : numbers) {
            AccountModel cached = cache.get(number, AccountModel.class);
            if (cached != null) {
                results.add(cached);
            } else {
                missingNumbers.add(number);
            }
        }

        // Fetch only missing items via API
        if (!missingNumbers.isEmpty()) {
            List<AccountModel> fetched = RetrofitRequestExecutor.executeRaw(
                api.get(properties.getToken(), missingNumbers)
            );

            // Cache newly fetched items
            fetched.forEach(account -> cache.put(account.getNumber(), account));
            results.addAll(fetched);
        }

        return results;
    }

    public List<AccountModel> get(int pageNumber, int pageSize) {
        log.info("Requesting accounts page {} of size {}", pageNumber, pageSize);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), pageNumber, pageSize));
    }
}
