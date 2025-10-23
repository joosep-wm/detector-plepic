package ee.digit25.detector.domain.transaction.external.api;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionModel {

    private String id;
    private BigDecimal amount;
    private String sender;
    private String recipient;
    private String senderAccount;
    private String recipientAccount;
    private String deviceMac;
    private LocalDateTime timestamp;
    private LocalDateTime deadline;
}
