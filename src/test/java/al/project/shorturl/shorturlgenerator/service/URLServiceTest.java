package al.project.shorturl.shorturlgenerator.service;

import al.project.shorturl.shorturlgenerator.entity.URL;
import al.project.shorturl.shorturlgenerator.repository.URLRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class URLServiceTest {
    @InjectMocks
    private URLService urlService;

    @Mock
    private URLRepository urlRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShortenURL_NewURL_ShouldCreateShortURL() {
        String longUrl = "https://www.example.com/long-url";
        Optional<Integer> expirationMinutes = Optional.of(10);

        when(urlRepository.findByOriginalUrl(longUrl)).thenReturn(Optional.empty());

        String shortUrl = urlService.shortenURL(longUrl, expirationMinutes);

        assertNotNull(shortUrl);
        assertEquals(8, shortUrl.length());
        verify(urlRepository, times(1)).save(any(URL.class));
    }

    @Test
    public void testShortenURL_ExistingURL_ShouldReturnExistingShortURL() {
        String longUrl = "https://www.example.com/existing-url";
        URL existingUrl = new URL();
        existingUrl.setShortUrl("abcd1234");
        existingUrl.setOriginalUrl(longUrl);

        when(urlRepository.findByOriginalUrl(longUrl)).thenReturn(Optional.of(existingUrl));

        String shortUrl = urlService.shortenURL(longUrl, Optional.empty());

        assertEquals("abcd1234", shortUrl);
        verify(urlRepository, never()).save(any(URL.class));
    }

    @Test
    public void testShortenURL_ShouldResetExpirationForExistingShortURL() {
        String longUrl = "https://www.example.com/another-url";
        URL existingUrl = new URL();
        existingUrl.setShortUrl("efgh5678");
        existingUrl.setOriginalUrl(longUrl);

        when(urlRepository.findByOriginalUrl(longUrl)).thenReturn(Optional.of(existingUrl));

        urlService.shortenURL(longUrl, Optional.of(10));

        verify(urlRepository, times(1)).updateExpirationTime(eq("efgh5678"), any(LocalDateTime.class));
    }

    @Test
    public void testEncodeBase64_ShouldGenerateUniqueShortUrl() {
        String longUrl1 = "https://www.example.com/unique-url-1";
        String longUrl2 = "https://www.example.com/unique-url-2";

        String shortUrl1 = urlService.shortenURL(longUrl1, Optional.empty());
        String shortUrl2 = urlService.shortenURL(longUrl2, Optional.empty());

        assertNotEquals(shortUrl1, shortUrl2);
    }
}
