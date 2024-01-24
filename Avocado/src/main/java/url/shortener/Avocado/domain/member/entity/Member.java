package url.shortener.Avocado.domain.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
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


    @Builder
    public Member(String email, String password, AuthProvider provider, String oAuth2Id, String profile, boolean activated) {
        this.email = email;
        this.password = password;
        this.authprovider = provider;
        this.oAuth2Id = oAuth2Id;
        this.profile = profile;
        this.activated = activated;
    }

    public void update() {
        this.activated = true;
    }
}
