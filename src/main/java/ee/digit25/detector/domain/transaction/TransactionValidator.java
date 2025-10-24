package ee.digit25.detector.domain.transaction;

import ee.digit25.detector.domain.account.AccountValidator;
import ee.digit25.detector.domain.device.DeviceValidator;
import ee.digit25.detector.domain.person.PersonValidator;
import ee.digit25.detector.domain.transaction.common.Transaction;
import ee.digit25.detector.domain.transaction.external.api.TransactionModel;
import ee.digit25.detector.domain.transaction.feature.FindTransactionsFeature;
import ee.digit25.detector.process.ValidationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionValidator {

    private final PersonValidator personValidator;
    private final DeviceValidator deviceValidator;
    private final AccountValidator accountValidator;
    private final FindTransactionsFeature findTransactionsFeature;

    public boolean isLegitimate(TransactionModel transaction, ValidationContext context) {
        LocalDateTime validationTime = LocalDateTime.now();

        boolean isLegitimate = true;

        isLegitimate &= personValidator.isValid(transaction.getRecipient(), context);
        isLegitimate &= personValidator.isValid(transaction.getSender(), context);
        isLegitimate &= deviceValidator.isValid(transaction.getDeviceMac(), context);
        isLegitimate &= accountValidator.isValidSenderAccount(transaction.getSenderAccount(), transaction.getAmount(), transaction.getSender(), context);
        isLegitimate &= accountValidator.isValidRecipientAccount(transaction.getRecipientAccount(), transaction.getRecipient(), context);
        isLegitimate &= validateNoBurstTransaction(transaction, validationTime);
        isLegitimate &= validateNoMultideviceTransactions(transaction, validationTime);
        isLegitimate &= validateValidHistory(transaction, validationTime);

        return isLegitimate;
    }

    private boolean validateNoBurstTransaction(TransactionModel transaction, LocalDateTime validationTime) {
        LocalDateTime since = validationTime.minusSeconds(30);

        long transactionCountSince = findTransactionsFeature.bySender(transaction.getSender(), since)
                .size();

        return countBelowThreshold(transactionCountSince, 10);
    }

    private boolean validateNoMultideviceTransactions(TransactionModel transaction, LocalDateTime validationTime) {
        LocalDateTime since = validationTime.minusSeconds(10);

        long differentDeviceCountSince = findTransactionsFeature.bySender(transaction.getSender(), since)
                .stream()
                .map(t -> t.getDevice().getMac())
                .distinct()
                .count();

        return countBelowThreshold(differentDeviceCountSince, 2);
    }

    private boolean validateValidHistory(TransactionModel transaction, LocalDateTime validationTime) {
        LocalDateTime since = validationTime.minusMinutes(1);

        return findTransactionsFeature.bySender(transaction.getSender(), since)
                .stream()
                .allMatch(Transaction::isLegitimate);
    }

    private boolean countBelowThreshold(long count, int threshold) {
        return count < threshold;
    }
}
