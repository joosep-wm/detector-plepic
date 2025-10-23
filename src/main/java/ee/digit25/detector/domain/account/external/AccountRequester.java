package ee.digit25.detector.domain.account.external;

import ee.bitweb.core.retrofit.RetrofitRequestExecutor;
import ee.digit25.detector.domain.account.external.api.AccountModel;
import ee.digit25.detector.domain.account.external.api.AccountApi;
import ee.digit25.detector.domain.account.external.api.AccountApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRequester {

    private final AccountApi api;
    private final AccountApiProperties properties;

    public AccountModel get(String accountNumber) {
        log.info("Requesting account {}", accountNumber);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), accountNumber));
    }

    public List<AccountModel> get(List<String> numbers) {
        log.info("Requesting accounts with numbers {}", numbers);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), numbers));
    }

    public List<AccountModel> get(int pageNumber, int pageSize) {
        log.info("Requesting accounts page {} of size {}", pageNumber, pageSize);

        return RetrofitRequestExecutor.executeRaw(api.get(properties.getToken(), pageNumber, pageSize));
    }
}
