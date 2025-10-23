package ee.digit25.detector.domain.device.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceSpecification {

    public static Specification<Device> macEquals(String mac) {
        return ((root, query, builder) -> builder.equal(root.get(Device_.mac), mac));
    }
}
