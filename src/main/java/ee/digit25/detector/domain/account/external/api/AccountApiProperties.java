package ee.digit25.detector.domain.account.external.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties("accounts.api")
public class AccountApiProperties {

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String token;
}
