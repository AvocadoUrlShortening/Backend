package url.shortener.Avocado.domain.url.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import url.shortener.Avocado.domain.member.entity.Member;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Url {

    private Long id;

    @Id
    @Column(nullable = false, unique = true)
    private String shortUrl;

    @Column(nullable = false)
    private String originalUrl;

    private boolean isRandomUrl;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Url(Long id, String shortUrl, String originalUrl, Date createdDate) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.createdDate = createdDate;
    }

    public void updateOwner(Member member, boolean isRandomUrl) {
        this.isRandomUrl = isRandomUrl;
        this.member = member;
    }


}
