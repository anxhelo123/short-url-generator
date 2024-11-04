package al.project.shorturl.shorturlgenerator.service;

import al.project.shorturl.shorturlgenerator.exceptions.NotFoundException;
import al.project.shorturl.shorturlgenerator.entity.URL;
import al.project.shorturl.shorturlgenerator.repository.URLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class URLService {

    @Value("${url.expiration.default-duration}")
    private int defaultExpirationDuration;

    @Autowired
    private URLRepository urlRepository;

    public String shortenURL(String longUrl, Optional<Integer> expirationMinutes) {
        URL url = new URL();

        Optional<URL> existingUrl = urlRepository.findByOriginalUrl(longUrl);

        if (existingUrl.isPresent()) {
            resetExpiration(existingUrl.get().getShortUrl());
            return existingUrl.get().getShortUrl();
        }

        String shortUrl = encodeBase64(longUrl);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expirationMinutes.orElse(defaultExpirationDuration));
        url.setOriginalUrl(longUrl);
        url.setShortUrl(shortUrl);
        url.setCreationTime(LocalDateTime.now());
        url.setExpirationTime(expirationTime);
        urlRepository.save(url);

        return url.getShortUrl();
    }

    public String getOriginalURL(String shortUrl) throws ChangeSetPersister.NotFoundException, NotFoundException {
        URL url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new NotFoundException("URL not found"));

        if (url.getExpirationTime().isBefore(LocalDateTime.now())) {
            urlRepository.delete(url);
            throw new ChangeSetPersister.NotFoundException();
        }

        LocalDateTime expirationTime = url.getExpirationTime();

        if (LocalDateTime.now().isAfter(expirationTime)) {
            urlRepository.delete(url); // Delete the expired URL
            throw new NotFoundException("URL has expired");
        }

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        return url.getOriginalUrl();
    }

    public void resetExpiration(String shortUrl) {
        LocalDateTime newExpirationTime = LocalDateTime.now().plusMinutes(defaultExpirationDuration);
        urlRepository.updateExpirationTime(shortUrl, newExpirationTime);
    }

    public void updateExpirationTime(String shortUrl, Integer expirationMinutes) {
        URL url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        url.setExpirationTime(LocalDateTime.now().plusMinutes(expirationMinutes));
        urlRepository.save(url);
    }

    private String encodeBase64(String longUrl) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(longUrl.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash).substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating short URL", e);
        }
    }
}

