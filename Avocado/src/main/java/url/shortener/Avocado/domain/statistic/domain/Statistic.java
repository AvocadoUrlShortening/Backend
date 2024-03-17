package url.shortener.Avocado.domain.statistic.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import url.shortener.Avocado.domain.url.domain.Url;
import url.shortener.Avocado.global.config.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
public class Statistic extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id")
    private Url url;

    @Setter
    @Embedded
    private StatisticInfo info;

    public Statistic(Url url, StatisticInfo info) {
        this.url = url;
        this.info = info;
    }

    public void setUrl(Url url) {
        this.url = url;
        if (!url.getStatistics().contains(this)) {
            url.getStatistics().add(this);
        }
    }
}
