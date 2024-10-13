package vn.ptit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IOTApplication {

    private static final Logger log = LoggerFactory.getLogger(IOTApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(IOTApplication.class);
        Environment env = app.run(args).getEnvironment();

        log.info(
            "\n----------------------------------------------------------\n\t" +
                    "Application '{}' is running! Access URLs:\n\t" +
                    "Local: \t\thttp://localhost:{}\n\t" +
            "\n----------------------------------------------------------",
            IOTApplication.class,
            env.getProperty("server.port")
        );
    }
}
