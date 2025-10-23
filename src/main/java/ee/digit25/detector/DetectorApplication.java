package ee.digit25.detector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ee.digit25", "ee.bitweb.core"})
public class DetectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DetectorApplication.class, args);
    }

}
