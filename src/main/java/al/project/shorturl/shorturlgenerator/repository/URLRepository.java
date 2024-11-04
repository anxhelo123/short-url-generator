package al.project.shorturl.shorturlgenerator.repository;

import al.project.shorturl.shorturlgenerator.model.URL;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.jar.JarInputStream;

@Repository
public interface URLRepository extends JpaRepository<URL, Long> {

    // Find a URL by its short URL code
    Optional<URL> findByShortUrl(String shortUrl);

    // Find a URL by the original long URL
    Optional<URL> findByOriginalUrl(String originalUrl);

    @Modifying
    @Transactional
    @Query("UPDATE URL u SET u.expirationTime = :expirationTime WHERE u.shortUrl = :shortUrl")
    void updateExpirationTime(@Param("shortUrl") String shortUrl, @Param("expirationTime") LocalDateTime expirationTime);

    @Modifying
    @Transactional
    @Query("UPDATE URL u SET u.clickCount = u.clickCount + 1 WHERE u.shortUrl = :shortUrl")
    void incrementClickCount(@Param("shortUrl") String shortUrl);

    @Modifying
    @Transactional
    @Query("DELETE FROM URL u WHERE u.expirationTime < :currentTime")
    void deleteExpiredUrls(@Param("currentTime") LocalDateTime currentTime);

}
