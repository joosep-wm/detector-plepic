package ee.digit25.detector.domain.transaction.external.api;

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
@ConfigurationProperties("transactions.api")
public class TransactionApiProperties {

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String token;
}
