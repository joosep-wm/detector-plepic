package ee.digit25.detector.domain.device.external;

import ee.bitweb.core.retrofit.RetrofitRequestExecutor;
import ee.digit25.detector.common.ApiRequestDeduplicator;
import ee.digit25.detector.domain.device.external.api.DeviceModel;
import ee.digit25.detector.domain.device.external.api.DeviceApi;
import ee.digit25.detector.domain.device.external.api.DeviceApiProperties;
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
public class DeviceRequester {

    private final DeviceApi api;
    private final DeviceApiProperties properties;
    private final ApiRequestDeduplicator deduplicator;
    private final CacheManager cacheManager;

    @Cacheable(value = "devices", key = "#mac")
    public DeviceModel get(String mac) {
        return deduplicator.deduplicate("device:" + mac, () -> {
            log.info("Requesting device with mac({})", mac);
            return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), mac));
        });
    }

    public List<DeviceModel> get(List<String> macs) {
        log.info("Requesting devices with macs {}", macs);

        Cache cache = cacheManager.getCache("devices");
        List<DeviceModel> results = new ArrayList<>();
        List<String> missingMacs = new ArrayList<>();

        // Check cache first
        for (String mac : macs) {
            DeviceModel cached = cache.get(mac, DeviceModel.class);
            if (cached != null) {
                results.add(cached);
            } else {
                missingMacs.add(mac);
            }
        }

        // Fetch only missing items via API
        if (!missingMacs.isEmpty()) {
            List<DeviceModel> fetched = RetrofitRequestExecutor.executeRaw(
                api.get(properties.getToken(), missingMacs)
            );

            // Cache newly fetched items
            fetched.forEach(device -> cache.put(device.getMac(), device));
            results.addAll(fetched);
        }

        return results;
    }

    public List<DeviceModel> get(int pageNumber, int pageSize) {
        log.info("Requesting persons page {} of size {}", pageNumber, pageSize);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), pageNumber, pageSize));
    }
}
