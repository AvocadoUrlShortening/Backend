package url.shortener.Avocado.domain.url.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import url.shortener.Avocado.domain.member.domain.Member;
import url.shortener.Avocado.global.config.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Url extends BaseEntity {

    private Long id;

    @Id
    @Column(nullable = false, unique = true)
    private String shortUrl;

    @Column(nullable = false)
    private String originalUrl;

    private boolean isRandomUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Long visit = 0L;

    @ElementCollection
    private List<String> statisticInfo = new ArrayList<>();
//    @OneToOne(fetch = FetchType.EAGER, mappedBy = "url")
//    public Statistic statistic;
//
    @Builder
    public Url(Long id, String shortUrl, String originalUrl) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
    }

    public void updateOwner(Member member, boolean isRandomUrl) {
        this.isRandomUrl = isRandomUrl;
        this.member = member;
    }

//    public void setStatistic(Statistic statistic) {
//        this.statistic = statistic;
//        statistic.setUrl(this);
//    }

    public void updateStatistic(String info) {
        visit += 1;
        statisticInfo.add(info);
    }
}
