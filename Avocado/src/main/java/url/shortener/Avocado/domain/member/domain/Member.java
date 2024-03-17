package url.shortener.Avocado.domain.member.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import url.shortener.Avocado.domain.url.domain.Url;
import url.shortener.Avocado.global.config.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String password;

    @Column
    private String oAuth2Id;

    @Column
    @Enumerated(EnumType.STRING)
    private AuthProvider authprovider;

    @Column(nullable = false, unique = true)
    private String email;
    @Column
    private String profile;

    @Column
    private boolean activated;

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<Url> urls = new ArrayList<>();

    @Builder
    public Member(String email, String password, AuthProvider provider, String oAuth2Id, String profile, boolean activated) {
        this.email = email;
        this.password = password;
        this.authprovider = provider;
        this.oAuth2Id = oAuth2Id;
        this.profile = profile;
        this.activated = activated;
    }


    public void activateMember() {
        this.activated = true;
    }

    public void addUrl(Url url) {
        urls.add(url);
        url.updateOwner(this, false);
    }
}
