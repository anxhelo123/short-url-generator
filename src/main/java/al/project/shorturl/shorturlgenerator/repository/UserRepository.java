package al.project.shorturl.shorturlgenerator.repository;

import al.project.shorturl.shorturlgenerator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
