package org.example.odiya.common.Fixture;

import org.example.odiya.auth.dto.request.LoginRequest;
import org.example.odiya.auth.dto.request.SigninRequest;
import org.example.odiya.eta.dto.request.EtaRequest;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.dto.request.HurryUpRequest;
import org.example.odiya.mate.dto.request.MateJoinRequest;
import org.example.odiya.meeting.domain.Location;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.dto.request.MeetingCreateRequest;
import org.example.odiya.member.domain.Member;
import org.example.odiya.route.dto.response.GoogleDirectionResponse;

import java.util.Collections;

public class DtoGenerator {

    public SigninRequest generateSigninRequest(Member member) {
        return new SigninRequest(member.getName(), member.getEmail(), "abcd1234");
    }

    public LoginRequest generateLoginRequest(Member member) {
        return new LoginRequest(member.getEmail(), "abcd1234");
    }

    public MateJoinRequest generateMateJoinRequest(Meeting meeting) {
        Location origin = Fixture.ORIGIN_LOCATION;
        return new MateJoinRequest(
                meeting.getInviteCode(),
                origin.getAddress(),
                origin.getCoordinates().getLatitude(),
                origin.getCoordinates().getLongitude()
        );
    }

    public static MeetingCreateRequest generateMeetingCreateRequest(Meeting meeting) {
        Location originLocation = Fixture.ORIGIN_LOCATION;
        return new MeetingCreateRequest(
                meeting.getName(),
                meeting.getDate(),
                meeting.getTime(),
                "1",
                "강남역 10번출구",
                meeting.getTarget().getAddress(),
                meeting.getTarget().getCoordinates().getLatitude(),
                meeting.getTarget().getCoordinates().getLongitude(),
                originLocation.getAddress(),
                originLocation.getCoordinates().getLatitude(),
                originLocation.getCoordinates().getLongitude()
        );
    }

    public EtaRequest generateEtaRequest() {
        Location originLocation = Fixture.ORIGIN_LOCATION;
        return new EtaRequest(
                false,
                originLocation.getCoordinates().getLatitude(),
                originLocation.getCoordinates().getLongitude()
        );
    }

    public HurryUpRequest generateHurryUpRequest(Mate sender, Mate receiver) {
        return new HurryUpRequest(sender.getId(), receiver.getId());
    }

    public GoogleDirectionResponse generateGoogleDirectionResponse() {
        GoogleDirectionResponse response = new GoogleDirectionResponse();

        // Route 설정
        GoogleDirectionResponse.Route route = new GoogleDirectionResponse.Route();

        // Leg 설정
        GoogleDirectionResponse.Leg leg = new GoogleDirectionResponse.Leg();
        GoogleDirectionResponse.TextValue duration = new GoogleDirectionResponse.TextValue();
        duration.setText("4 mins");
        duration.setValue(256L); // 256초 = 약 4분
        leg.setDuration(duration);

        GoogleDirectionResponse.TextValue distance = new GoogleDirectionResponse.TextValue();
        distance.setText("1 km");
        distance.setValue(1000L); // 1000미터 = 1킬로미터
        leg.setDistance(distance);

        // Location 설정 - Fixture의 위치 정보 사용
        GoogleDirectionResponse.Location startLocation = new GoogleDirectionResponse.Location();
        startLocation.setLat(Double.parseDouble(Fixture.ORIGIN_LOCATION.getCoordinates().getLatitude()));
        startLocation.setLng(Double.parseDouble(Fixture.ORIGIN_LOCATION.getCoordinates().getLongitude()));

        GoogleDirectionResponse.Location endLocation = new GoogleDirectionResponse.Location();
        endLocation.setLat(Double.parseDouble(Fixture.TARGET_LOCATION.getCoordinates().getLatitude()));
        endLocation.setLng(Double.parseDouble(Fixture.TARGET_LOCATION.getCoordinates().getLongitude()));

        leg.setStartLocation(startLocation);
        leg.setEndLocation(endLocation);
        leg.setStartAddress(Fixture.ORIGIN_LOCATION.getAddress());
        leg.setEndAddress(Fixture.TARGET_LOCATION.getAddress());

        // Steps 설정
        GoogleDirectionResponse.Step step = new GoogleDirectionResponse.Step();
        step.setDuration(duration);
        step.setStartLocation(startLocation);
        step.setEndLocation(endLocation);

        leg.setSteps(Collections.singletonList(step));
        route.setLegs(Collections.singletonList(leg));
        response.setRoutes(Collections.singletonList(route));

        response.setStatus("OK");

        return response;
    }
}
