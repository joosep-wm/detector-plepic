package ee.digit25.detector.domain.account;

import ee.digit25.detector.domain.account.external.AccountRequester;
import ee.digit25.detector.process.ValidationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountValidator {

    private final AccountRequester requester;

    public boolean isValidSenderAccount(String accountNumber, BigDecimal amount, String senderPersonCode, ValidationContext context) {
        log.info("Validating sender account {}: checking closed, owner, and balance", accountNumber);

        var account = context.getAccount(accountNumber);

        return !account.getClosed()
            && senderPersonCode.equals(account.getOwner())
            && account.getBalance().compareTo(amount) >= 0;
    }

    public boolean isValidRecipientAccount(String accountNumber, String recipientPersonCode, ValidationContext context) {
        log.info("Validating recipient account {}: checking closed and owner", accountNumber);

        var account = context.getAccount(accountNumber);

        return !account.getClosed()
            && recipientPersonCode.equals(account.getOwner());
    }
}
