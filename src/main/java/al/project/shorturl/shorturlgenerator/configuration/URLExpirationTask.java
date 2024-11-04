package al.project.shorturl.shorturlgenerator.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import al.project.shorturl.shorturlgenerator.repository.URLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class URLExpirationTask {

        @Value("${url.expiration.default-duration}")
        private int defaultExpirationDuration;

        @Autowired
        private URLRepository urlRepository;


        // Run every hour (or adjust as needed)
        @Scheduled(fixedRate = 60000)
        public void deleteExpiredUrls() {
            LocalDateTime now = LocalDateTime.now();
            urlRepository.findAll().stream()
                    .filter(url -> now.isAfter(url.getCreationTime().plusMinutes(defaultExpirationDuration)))
                    .forEach(urlRepository::delete);
        }
    }
