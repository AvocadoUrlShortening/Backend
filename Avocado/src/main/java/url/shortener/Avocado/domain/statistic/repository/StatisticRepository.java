package url.shortener.Avocado.domain.statistic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import url.shortener.Avocado.domain.statistic.domain.Statistic;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {
}
