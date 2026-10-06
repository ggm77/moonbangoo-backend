package com.seohamin.moonbangoo.global.exception.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 정보가 잘못되어 있습니다."),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "올바르지 않은 Enum입니다."),
    PACK_NOT_EXIST(HttpStatus.BAD_REQUEST, "팩이 존재하지 않습니다."),
    PRIZE_NOT_EXIST(HttpStatus.BAD_REQUEST, "경품이 존재하지 않습니다."),
    DRAW_NOT_EXIST(HttpStatus.BAD_REQUEST, "뽑기 기록이 존재하지 않습니다."),
    PRIZE_NOT_IN_DRAW(HttpStatus.BAD_REQUEST, "뽑은 카드에 없는 경품입니다."),

    PACK_NOT_EMPTY(HttpStatus.CONFLICT, "경품이 남아있는 팩은 삭제할 수 없습니다."),
    PACK_INACTIVE(HttpStatus.CONFLICT, "사용할 수 없는 팩입니다."),
    PACK_SOLD_OUT(HttpStatus.CONFLICT, "팩에 남은 경품이 없습니다."),
    PRIZE_SOLD_OUT(HttpStatus.CONFLICT, "경품이 모두 소진되었습니다."),
    DRAW_ALREADY_CONFIRMED(HttpStatus.CONFLICT, "이미 경품을 선택한 뽑기입니다."),
    DRAW_NOT_CONFIRMED(HttpStatus.CONFLICT, "경품을 선택한 뽑기가 아닙니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 에러가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스를 사용할 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
