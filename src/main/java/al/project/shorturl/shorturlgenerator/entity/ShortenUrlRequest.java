package al.project.shorturl.shorturlgenerator.entity;

import jakarta.validation.constraints.NotNull;

public class ShortenUrlRequest {

    @NotNull(message = "Long URL is required")
    private String longUrl;

    private Integer expirationMinutes;

    // Getters and Setters

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public Integer getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(Integer expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }
}
