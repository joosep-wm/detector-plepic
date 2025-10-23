package ee.digit25.detector.domain.device.external.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DeviceModel {

    private String mac;
    private Boolean isBlacklisted;
}
