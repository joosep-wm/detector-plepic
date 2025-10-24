package ee.digit25.detector.common;

import ee.digit25.detector.domain.account.external.AccountRequester;
import ee.digit25.detector.domain.device.external.DeviceRequester;
import ee.digit25.detector.domain.person.external.PersonRequester;
import ee.digit25.detector.domain.transaction.common.Transaction;
import ee.digit25.detector.domain.transaction.common.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service that warms up caches on application startup by pre-fetching entities
 * from recent transactions. This eliminates cold-start performance degradation
 * by ensuring the cache has high hit rates from the first batch of processing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmingService {

    private final PersonRequester personRequester;
    private final AccountRequester accountRequester;
    private final DeviceRequester deviceRequester;
    private final TransactionRepository transactionRepository;

    /**
     * Warms caches when application is ready by:
     * 1. Fetching recent transactions from the last 10 minutes
     * 2. Extracting unique person codes, account numbers, and device MACs
     * 3. Pre-fetching these entities using batch API endpoints
     * 4. Logging the number of entities warmed
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmCaches() {
        log.info("Starting cache warming...");

        try {
            LocalDateTime recentCutoff = LocalDateTime.now().minusMinutes(10);
            List<Transaction> recentTransactions = transactionRepository.findAll()
                .stream()
                .filter(tx -> tx.getTimestamp() != null && tx.getTimestamp().isAfter(recentCutoff))
                .toList();

            if (recentTransactions.isEmpty()) {
                log.info("No recent transactions found for cache warming");
                return;
            }

            Set<String> personCodes = new HashSet<>();
            Set<String> accountNumbers = new HashSet<>();
            Set<String> deviceMacs = new HashSet<>();

            // Collect unique entities from recent transactions
            for (Transaction tx : recentTransactions) {
                if (tx.getSender() != null) {
                    personCodes.add(tx.getSender().getPersonCode());
                }
                if (tx.getRecipient() != null) {
                    personCodes.add(tx.getRecipient().getPersonCode());
                }
                if (tx.getSenderAccount() != null) {
                    accountNumbers.add(tx.getSenderAccount().getNumber());
                }
                if (tx.getRecipientAccount() != null) {
                    accountNumbers.add(tx.getRecipientAccount().getNumber());
                }
                if (tx.getDevice() != null) {
                    deviceMacs.add(tx.getDevice().getMac());
                }
            }

            // Pre-fetch and cache hot entities using batch endpoints
            if (!personCodes.isEmpty()) {
                personRequester.get(new ArrayList<>(personCodes));
            }
            if (!accountNumbers.isEmpty()) {
                accountRequester.get(new ArrayList<>(accountNumbers));
            }
            if (!deviceMacs.isEmpty()) {
                deviceRequester.get(new ArrayList<>(deviceMacs));
            }

            log.info("Cache warming complete! Warmed {} persons, {} accounts, {} devices from {} recent transactions",
                personCodes.size(), accountNumbers.size(), deviceMacs.size(), recentTransactions.size());

        } catch (Exception e) {
            log.error("Error during cache warming: {}", e.getMessage(), e);
            // Don't fail application startup due to cache warming errors
        }
    }
}
