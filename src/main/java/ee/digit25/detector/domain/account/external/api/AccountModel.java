package ee.digit25.detector.domain.account.external.api;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountModel {

    private String number;
    private String owner;
    private Boolean closed;
    private BigDecimal balance;
}
