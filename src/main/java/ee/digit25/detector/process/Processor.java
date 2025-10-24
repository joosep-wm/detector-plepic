package ee.digit25.detector.process;

import ee.digit25.detector.domain.account.external.AccountRequester;
import ee.digit25.detector.domain.account.external.api.AccountModel;
import ee.digit25.detector.domain.device.external.DeviceRequester;
import ee.digit25.detector.domain.device.external.api.DeviceModel;
import ee.digit25.detector.domain.person.external.PersonRequester;
import ee.digit25.detector.domain.person.external.api.PersonModel;
import ee.digit25.detector.domain.transaction.TransactionValidator;
import ee.digit25.detector.domain.transaction.common.Transaction;
import ee.digit25.detector.domain.transaction.common.TransactionMapper;
import ee.digit25.detector.domain.transaction.external.TransactionRequester;
import ee.digit25.detector.domain.transaction.external.TransactionVerifier;
import ee.digit25.detector.domain.transaction.external.api.TransactionModel;
import ee.digit25.detector.domain.transaction.feature.PersistTransactionFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Processor {

    private final int TRANSACTION_BATCH_SIZE = 100;
    private final TransactionRequester requester;
    private final PersonRequester personRequester;
    private final AccountRequester accountRequester;
    private final DeviceRequester deviceRequester;
    private final TransactionValidator validator;
    private final TransactionVerifier verifier;
    private final PersistTransactionFeature persistTransactionFeature;
    private final TransactionMapper transactionMapper;


    @Scheduled(fixedDelay = 1) //Runs every 1000 ms after the last run
    public void process() {
        log.info("Starting to process a batch of transactions of size {}", TRANSACTION_BATCH_SIZE);

        List<TransactionModel> transactions = requester.getUnverified(TRANSACTION_BATCH_SIZE);

        if (transactions.isEmpty()) {
            log.info("No transactions to process");
            return;
        }

        // Pre-fetch all data in batch for the entire batch of transactions
        ValidationContext context = prefetchValidationData(transactions);

        // Collections for batch API calls and database persistence
        List<TransactionModel> verifiedTransactions = new ArrayList<>();
        List<TransactionModel> rejectedTransactions = new ArrayList<>();
        List<Transaction> transactionsToPersist = new ArrayList<>();

        // Process all transactions sequentially
        for (TransactionModel transaction : transactions) {
            process(transaction, context, verifiedTransactions, rejectedTransactions, transactionsToPersist);
        }

        // Batch verify/reject API calls
        if (!verifiedTransactions.isEmpty()) {
            verifier.verify(verifiedTransactions);
        }

        if (!rejectedTransactions.isEmpty()) {
            verifier.reject(rejectedTransactions);
        }

        // Batch database persist
        if (!transactionsToPersist.isEmpty()) {
            persistTransactionFeature.saveAll(transactionsToPersist);
        }

        log.info("Finished processing a batch of transactions of size {}", transactions.size());
    }

    private ValidationContext prefetchValidationData(List<TransactionModel> transactions) {
        // Collect all unique identifiers
        Set<String> personCodes = new HashSet<>();
        Set<String> accountNumbers = new HashSet<>();
        Set<String> deviceMacs = new HashSet<>();

        for (TransactionModel transaction : transactions) {
            personCodes.add(transaction.getSender());
            personCodes.add(transaction.getRecipient());
            accountNumbers.add(transaction.getSenderAccount());
            accountNumbers.add(transaction.getRecipientAccount());
            deviceMacs.add(transaction.getDeviceMac());
        }

        log.info("Batch fetching: {} persons, {} accounts, {} devices",
                personCodes.size(), accountNumbers.size(), deviceMacs.size());

        // Fetch all data in batch
        Map<String, PersonModel> persons = personRequester.get(new ArrayList<>(personCodes))
                .stream()
                .collect(Collectors.toMap(PersonModel::getPersonCode, Function.identity()));

        Map<String, AccountModel> accounts = accountRequester.get(new ArrayList<>(accountNumbers))
                .stream()
                .collect(Collectors.toMap(AccountModel::getNumber, Function.identity()));

        Map<String, DeviceModel> devices = deviceRequester.get(new ArrayList<>(deviceMacs))
                .stream()
                .collect(Collectors.toMap(DeviceModel::getMac, Function.identity()));

        return new ValidationContext(persons, accounts, devices);
    }

    private void process(TransactionModel transaction, ValidationContext context,
                         List<TransactionModel> verifiedTransactions,
                         List<TransactionModel> rejectedTransactions,
                         List<Transaction> transactionsToPersist) {
        boolean valid = validator.isLegitimate(transaction, context);
        if (valid) {
            log.info("Legitimate transaction {}", transaction.getId());
            verifiedTransactions.add(transaction);
        } else {
            log.info("Not legitimate transaction {}", transaction.getId());
            rejectedTransactions.add(transaction);
        }

        transactionsToPersist.add(
                transactionMapper.toEntity(
                        transaction,
                        valid
                )
        );
    }
}
