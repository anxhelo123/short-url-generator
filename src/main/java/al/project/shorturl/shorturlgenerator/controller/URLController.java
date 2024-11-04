package al.project.shorturl.shorturlgenerator.controller;

import al.project.shorturl.shorturlgenerator.exceptions.NotFoundException;
import al.project.shorturl.shorturlgenerator.model.ShortenUrlRequest;
import al.project.shorturl.shorturlgenerator.service.URLService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/url")
public class URLController {
    @Autowired
    private URLService urlService;

    @Operation(summary = "Shorten a long URL",
            description = "Generate a unique short URL for the provided long URL, with an optional expiration time.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated a short URL"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@NotNull @RequestBody ShortenUrlRequest requestBody){
        if (requestBody.getLongUrl() == null || requestBody.getLongUrl().length() < 5) {
            return ResponseEntity.status(400).body("LongURL is NULL");
        }

        String longUrl =requestBody.getLongUrl();
        Optional<Integer> expirationMinutes = Optional.ofNullable(requestBody.getExpirationMinutes());

        return ResponseEntity.ok(urlService.shortenURL(longUrl, expirationMinutes));
    }

    @Operation(summary = "Retrieve the original URL",
            description = "Get the original long URL from a given short URL. Increments the click count.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the original URL"),
            @ApiResponse(responseCode = "404", description = "URL not found or expired"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> getOriginalUrl(
            @Parameter(description = "The short URL to be expanded", required = true)
            @PathVariable String shortUrl)  {
        try {
            String originalUrl = urlService.getOriginalURL(shortUrl);
            return ResponseEntity.ok(originalUrl);
        } catch (NotFoundException | ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Update expiration time",
            description = "Allows an authenticated user to overwrite the expiration time of an existing shortened URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated expiration time"),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{shortUrl}/expiration")
    public ResponseEntity<String> updateExpiration(
            @Parameter(description = "The short URL whose expiration is to be updated", required = true)
            @PathVariable String shortUrl,
            @Parameter(description = "New expiration time in minutes", required = true)
            @RequestParam Integer expirationMinutes) {

        urlService.updateExpirationTime(shortUrl, expirationMinutes);
        return ResponseEntity.ok("Expiration time updated successfully.");
    }
}
