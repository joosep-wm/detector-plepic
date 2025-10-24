package ee.digit25.detector.domain.transaction.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.sender s " +
           "JOIN FETCH t.device d " +
           "WHERE s.personCode = :senderCode AND t.timestamp > :since")
    List<Transaction> findBySenderAndTimestampAfter(
        @Param("senderCode") String senderCode,
        @Param("since") LocalDateTime since
    );
}
