package org.example.odiya.common.Fixture;

import org.example.odiya.meeting.domain.Location;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.member.domain.DeviceToken;
import org.example.odiya.member.domain.Member;

import java.time.LocalDate;
import java.time.LocalTime;

public class Fixture {

    public static Location ORIGIN_LOCATION = new Location(
            "서울시 강남구 선릉로 427",
            "37.504198",
            "127.049794"
    );

    public static Location TARGET_LOCATION = new Location(
            "서울시 강남구 테헤란로 411",
            "37.505713",
            "127.050691"
    );

    public static Meeting SOJU_MEETING = new Meeting(
            1L,
            "소주먹기",
            TARGET_LOCATION,
            LocalDate.now().plusDays(1),
            LocalTime.parse("18:00"),
            "123456"
    );

    public static Meeting CELEBRATE_MEETING = new Meeting(
            2L,
            "축하파티",
            TARGET_LOCATION,
            LocalDate.now().plusDays(2),
            LocalTime.parse("20:00"),
            "234567"
    );

    public static Member MEMBER1 = new Member(
            1L,
            "구본승",
            "bs@test.com",
            "abcd1234",
            new DeviceToken("aaa")
    );

    public static Member MEMBER2 = new Member(
            2L,
            "경규혁",
            "kh@test.com",
            "abcd1234",
            new DeviceToken("bbb")
    );

    public static Member MEMBER3 = new Member(
            3L,
            "최원유",
            "wu@test.com",
            "abcd1234",
            new DeviceToken("ccc")
    );

    public static Member MEMBER4 = new Member(
            4L,
            "임준형",
            "jh@test.com",
            "abcd1234",
            new DeviceToken("ddd")
    );

    private Fixture() {
    }
}
