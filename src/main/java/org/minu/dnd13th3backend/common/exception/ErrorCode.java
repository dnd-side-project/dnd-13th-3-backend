package org.minu.dnd13th3backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필을 찾을 수 없습니다."),
    PROFILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 프로필이 존재합니다."),
    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 챌린지를 찾을 수 없습니다."),
    INVITE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "유효하지 않은 초대 코드입니다."),
    INVITE_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 초대 코드입니다."),
    CHALLENGE_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "이미 참여한 챌린지입니다."),
    CHALLENGE_FULL(HttpStatus.BAD_REQUEST, "챌린지 인원이 가득 찼습니다."),
    CANNOT_JOIN_OWN_CHALLENGE(HttpStatus.BAD_REQUEST, "본인이 생성한 챌린지에는 참여할 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "해당 작업에 대한 권한이 없습니다."),
    PARTICIPANT_PROFILE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "참가자 중 프로필이 등록되지 않은 사용자가 있습니다."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 입력 값입니다."),
    SCREEN_TIME_GOAL_CUSTOM_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "커스텀 스크린 타임 목표는 12시간(720분)을 초과할 수 없습니다."),
    
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}