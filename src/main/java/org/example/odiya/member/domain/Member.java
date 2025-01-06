package org.example.odiya.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
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

    @Column
    private String inviteCode;

    @Embedded
    private DeviceToken deviceToken;

    @OneToMany(mappedBy = "member")
    private List<Mate> mates = new ArrayList<>();

    // 6자리 숫자로 구성된 초대 코드를 생성
    public void generateInviteCode() {
        this.inviteCode = RandomStringUtils.randomNumeric(6);
    }
}
