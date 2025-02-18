package org.example.odiya.apicall.util;

import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.common.exception.NotFoundException;

import java.util.Arrays;

import static org.example.odiya.common.exception.type.ErrorType.CLIENT_TYPE_NOT_FOUND_ERROR;

public enum ClientTypeMapper {

    GOOGLE("google", ClientType.GOOGLE),
    TMAP("tmap", ClientType.TMAP),
    KAKAO("kakao", ClientType.KAKAO)
    ;

    private String name;
    private ClientType type;

    ClientTypeMapper(String name, ClientType type) {
        this.name = name;
        this.type = type;
    }

    public static ClientType from(String targetName) {
        return Arrays.stream(values())
                .filter(routeClient -> routeClient.name.equals(targetName.toLowerCase()))
                .findAny()
                .map(pathVariable -> pathVariable.type)
                .orElseThrow(() -> new NotFoundException(CLIENT_TYPE_NOT_FOUND_ERROR));
    }
}
