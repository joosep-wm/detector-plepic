package ee.digit25.detector.domain.device.external;

import ee.bitweb.core.retrofit.RetrofitRequestExecutor;
import ee.digit25.detector.common.ApiRequestDeduplicator;
import ee.digit25.detector.domain.device.external.api.DeviceModel;
import ee.digit25.detector.domain.device.external.api.DeviceApi;
import ee.digit25.detector.domain.device.external.api.DeviceApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceRequester {

    private final DeviceApi api;
    private final DeviceApiProperties properties;
    private final ApiRequestDeduplicator deduplicator;

    @Cacheable(value = "devices", key = "#mac")
    public DeviceModel get(String mac) {
        return deduplicator.deduplicate("device:" + mac, () -> {
            log.info("Requesting device with mac({})", mac);
            return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), mac));
        });
    }

    public List<DeviceModel> get(List<String> macs) {
        log.info("Requesting devices with macs {}", macs);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), macs));
    }

    public List<DeviceModel> get(int pageNumber, int pageSize) {
        log.info("Requesting persons page {} of size {}", pageNumber, pageSize);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), pageNumber, pageSize));
    }
}
