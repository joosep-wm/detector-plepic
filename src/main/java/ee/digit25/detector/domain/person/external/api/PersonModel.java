package ee.digit25.detector.domain.person.external.api;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PersonModel {

    private String firstName;
    private String lastName;
    private String personCode;
    private Boolean warrantIssued;
    private Boolean hasContract;
    private Boolean blacklisted;
}
