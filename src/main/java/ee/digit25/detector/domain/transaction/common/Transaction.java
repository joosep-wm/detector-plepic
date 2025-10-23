package ee.digit25.detector.domain.transaction.common;

import ee.digit25.detector.domain.account.common.Account;
import ee.digit25.detector.domain.device.common.Device;
import ee.digit25.detector.domain.person.common.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Accessors(chain = true)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean legitimate;

    private BigDecimal amount;

    @ManyToOne
    private Person sender;

    @ManyToOne
    private Account senderAccount;

    @ManyToOne
    private Person recipient;

    @ManyToOne
    private Account recipientAccount;

    @ManyToOne
    private Device device;

    private LocalDateTime timestamp;

    private LocalDateTime deadline;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
