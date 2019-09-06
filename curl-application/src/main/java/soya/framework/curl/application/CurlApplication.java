package soya.framework.curl.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"soya.framework.curl.application"})
public class CurlApplication {
    public static void main(String[] args) {
        SpringApplication.run(CurlApplication.class, args);
    }
}
