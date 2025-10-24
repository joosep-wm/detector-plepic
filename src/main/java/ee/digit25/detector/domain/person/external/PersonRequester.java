package ee.digit25.detector.domain.person.external;

import ee.bitweb.core.retrofit.RetrofitRequestExecutor;
import ee.digit25.detector.common.ApiRequestDeduplicator;
import ee.digit25.detector.domain.person.external.api.PersonModel;
import ee.digit25.detector.domain.person.external.api.PersonApi;
import ee.digit25.detector.domain.person.external.api.PersonApiProperties;
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
public class PersonRequester {

    private final PersonApi api;
    private final PersonApiProperties properties;
    private final ApiRequestDeduplicator deduplicator;
    private final CacheManager cacheManager;

    @Cacheable(value = "persons", key = "#personCode")
    public PersonModel get(String personCode) {
        return deduplicator.deduplicate("person:" + personCode, () -> {
            log.info("Requesting person with personCode {}", personCode);
            return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), personCode));
        });
    }

    public List<PersonModel> get(List<String> personCodes) {
        log.info("Requesting persons with personCodes {}", personCodes);

        Cache cache = cacheManager.getCache("persons");
        List<PersonModel> results = new ArrayList<>();
        List<String> missingCodes = new ArrayList<>();

        // Check cache first
        for (String code : personCodes) {
            PersonModel cached = cache.get(code, PersonModel.class);
            if (cached != null) {
                results.add(cached);
            } else {
                missingCodes.add(code);
            }
        }

        // Fetch only missing items via API
        if (!missingCodes.isEmpty()) {
            List<PersonModel> fetched = RetrofitRequestExecutor.executeRaw(
                api.get(properties.getToken(), missingCodes)
            );

            // Cache newly fetched items
            fetched.forEach(person -> cache.put(person.getPersonCode(), person));
            results.addAll(fetched);
        }

        return results;
    }

    public List<PersonModel> get(int pageNumber, int pageSize) {
        log.info("Requesting persons page {} of size {}", pageNumber, pageSize);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), pageNumber, pageSize));
    }
}
