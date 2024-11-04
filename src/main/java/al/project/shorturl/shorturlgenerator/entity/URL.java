package al.project.shorturl.shorturlgenerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
@Getter
@Setter
@RequiredArgsConstructor
public class URL {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column(name = "short_url", unique = true, nullable = false)
    private String shortUrl;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime = LocalDateTime.now();

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "click_count", nullable = false)
    private int clickCount = 0;

    @PrePersist
    public void prePersist() {
        if (creationTime == null) {
            creationTime = LocalDateTime.now();
        }
    }

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = true)
//    private User user;
}

