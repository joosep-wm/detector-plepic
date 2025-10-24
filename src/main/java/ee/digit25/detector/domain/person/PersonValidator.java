package ee.digit25.detector.domain.person;

import ee.digit25.detector.domain.person.external.PersonRequester;
import ee.digit25.detector.process.ValidationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonValidator {

    private final PersonRequester requester;

    public boolean isValid(String personCode, ValidationContext context) {
        log.info("Validating person ({}): checking warrant, contract, and blacklist status", personCode);

        var person = context.getPerson(personCode);

        return !person.getWarrantIssued()
            && person.getHasContract()
            && !person.getBlacklisted();
    }
}
