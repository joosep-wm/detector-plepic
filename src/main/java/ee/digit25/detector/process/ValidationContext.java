package ee.digit25.detector.process;

import ee.digit25.detector.domain.account.external.api.AccountModel;
import ee.digit25.detector.domain.device.external.api.DeviceModel;
import ee.digit25.detector.domain.person.external.api.PersonModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ValidationContext {

    private final Map<String, PersonModel> persons;
    private final Map<String, AccountModel> accounts;
    private final Map<String, DeviceModel> devices;

    public PersonModel getPerson(String personCode) {
        return persons.get(personCode);
    }

    public AccountModel getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public DeviceModel getDevice(String mac) {
        return devices.get(mac);
    }
}
