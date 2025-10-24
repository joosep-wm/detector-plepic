package ee.digit25.detector.domain.device;

import ee.digit25.detector.domain.device.external.DeviceRequester;
import ee.digit25.detector.process.ValidationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceValidator {

    private final DeviceRequester requester;

    public boolean isValid(String mac, ValidationContext context) {
        log.info("Validating device {}", mac);

        return !isBlacklisted(mac, context);
    }

    public boolean isBlacklisted(String mac, ValidationContext context) {
        log.info("Starting to check if device is blacklisted");

        return context.getDevice(mac).getIsBlacklisted();
    }
}
