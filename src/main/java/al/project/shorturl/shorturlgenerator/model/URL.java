package al.project.shorturl.shorturlgenerator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
@Getter
@Setter
public class URL {


    public URL() {

    }
    public URL (String originalUrl, String shortUrl, LocalDateTime expirationTime, int clickCount){
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.expirationTime = expirationTime;
        this.clickCount =clickCount;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column(name = "short_url", unique = true, nullable = false)
    private String shortUrl;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "click_count", nullable = false)
    private int clickCount = 0;


//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = true)
//    private User user;
}

