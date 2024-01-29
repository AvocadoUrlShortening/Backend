package url.shortener.Avocado.domain.url.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import url.shortener.Avocado.domain.url.domain.Url;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByShortUrl(String customUrl);
    boolean existsByShortUrl(String shortUrl);
}
