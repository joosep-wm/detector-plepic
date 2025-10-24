package ee.digit25.detector.domain.transaction.feature;

import ee.digit25.detector.domain.transaction.common.Transaction;
import ee.digit25.detector.domain.transaction.common.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersistTransactionFeature {

    private final TransactionRepository repository;

    public Transaction save(Transaction transaction) {
        log.info("Saving transaction: {}", transaction);

        return repository.save(transaction);
    }

    public List<Transaction> saveAll(List<Transaction> transactions) {
        log.info("Batch saving {} transactions", transactions.size());

        return repository.saveAll(transactions);
    }
}
