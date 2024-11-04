package al.project.shorturl.shorturlgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShortUrlGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortUrlGeneratorApplication.class, args);
	}

}
