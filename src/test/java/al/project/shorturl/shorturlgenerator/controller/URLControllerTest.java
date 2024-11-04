package al.project.shorturl.shorturlgenerator.controller;

import al.project.shorturl.shorturlgenerator.config.TestSecurityConfig;
import al.project.shorturl.shorturlgenerator.exceptions.NotFoundException;
import al.project.shorturl.shorturlgenerator.model.ShortenUrlRequest;
import al.project.shorturl.shorturlgenerator.service.URLService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters=false)
@ContextConfiguration(classes = {TestSecurityConfig.class})
public class URLControllerTest {

    @InjectMocks
    private URLController urlController;

    @MockBean
    private URLService urlService;


    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShortenUrl_ShouldReturnShortUrl() {
        String longUrl = "https://www.example.com/long-url";
        Optional<Integer> expirationMinutes = Optional.of(10);
        String shortUrl = "abcd1234";

        when(urlService.shortenURL(longUrl, expirationMinutes)).thenReturn(shortUrl);
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl(longUrl);
        request.setExpirationMinutes(expirationMinutes.get());
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("longUrl", longUrl);
        requestBody.put("expirationMinutes", expirationMinutes.get());

        ResponseEntity<String> response = urlController.shortenUrl(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("abcd1234", response.getBody());
    }

    @Test
    public void testShortenUrl_MissingLongUrl_ShouldReturnError() throws Exception {
        String requestBody = "{\"expirationMinutes\": 10}";


        mockMvc.perform(post("/api/v1/url/shorten")
                        .contentType("application/json")
                        .content(requestBody))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetOriginalUrl_Success() throws Exception {
        String shortUrl = "JF-rqVwI";
        String longUrl = "https://www.example.com/very/long/url";

        // Mock the service response
        when(urlService.getOriginalURL(shortUrl)).thenReturn(longUrl);

        // Test the get original URL endpoint
        mockMvc.perform(get("/api/v1/url/{shortUrl}", shortUrl)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(longUrl));

        // Verify the interaction with the service
        verify(urlService, times(1)).getOriginalURL(shortUrl);
    }


    @Test
    public void testGetOriginalUrl_NotFound_ShouldReturnNotFound() throws Exception {
        String shortUrl = "invalid123";

        // Mock the service to throw an exception for a non-existent URL
        when(urlService.getOriginalURL(shortUrl)).thenThrow(new NotFoundException("URL not found"));

        mockMvc.perform(get("/api/v1/url/{shortUrl}", shortUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateExpiration_Success() throws Exception {
        String shortUrl = "abcd1234";
        int expirationMinutes = 20;

        // Test the update expiration endpoint
        mockMvc.perform(put("/api/v1/url/{shortUrl}/expiration", shortUrl)
                        .param("expirationMinutes", String.valueOf(expirationMinutes))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Expiration time updated successfully."));

        // Verify the interaction with the service
        verify(urlService, times(1)).updateExpirationTime(shortUrl, expirationMinutes);
    }

}
