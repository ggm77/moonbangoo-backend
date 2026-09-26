package com.seohamin.moonbangoo.global.exception.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 정보가 잘못되어 있습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다."),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "올바르지 않은 Enum입니다."),
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다."),
    PRIZE_NOT_EXIST(HttpStatus.BAD_REQUEST, "경품이 존재하지 않습니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    NOT_ENOUGH_PRIZE(HttpStatus.CONFLICT, "뽑을 수 있는 경품이 부족합니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 에러가 발생했습니다."),
    KAKAO_REQUEST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오와 통신 중 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스를 사용할 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
