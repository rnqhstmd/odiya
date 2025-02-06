package org.example.odiya.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.odiya.common.domain.BaseEntity;
import org.example.odiya.mate.domain.Mate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded
    private DeviceToken deviceToken;

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<Mate> mates = new ArrayList<>();

    // 테스트용 생성자
    public Member(Long id, String name, String email, String password, DeviceToken deviceToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.deviceToken = deviceToken;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
