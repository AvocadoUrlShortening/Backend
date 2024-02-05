package url.shortener.Avocado.domain.statistic.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import url.shortener.Avocado.domain.url.domain.Url;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Statistic {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", unique = true)
    private Url url;

    private Long visit = 0L;
    @ElementCollection
    private List<String> statisticInfo = new ArrayList<>();

    public void updateStatistic(String info) {
        visit += 1;
        statisticInfo.add(info);
    }

    public void setUrl(Url url) {
        this.url = url;
    }
}
