package configserver.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ConfigClientApplication {


    @Bean
    ApplicationRunner applicationRunner(@Value("${message}") String message, Environment environment) {
        return a -> {
            System.out.println(environment.getProperty("message"));
            System.out.println(message);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }

}
