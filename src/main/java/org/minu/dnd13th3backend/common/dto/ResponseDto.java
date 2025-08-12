package org.minu.dnd13th3backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {

    private final boolean success;
    private final String message;
    private final T data; // 제네릭(Generic) 타입 T를 사용하여 어떤 종류의 데이터든 담을 수 있음

    // 성공 시 사용할 정적 팩토리 메서드
    public static <T> ResponseDto<T> success(String message, T data) {
        return new ResponseDto<>(true, message, data);
    }

    // 성공했지만 데이터가 없을 경우
    public static <T> ResponseDto<T> success(String message) {
        return new ResponseDto<>(true, message, null);
    }

    // 실패 시 사용할 정적 팩토리 메서드
    public static <T> ResponseDto<T> fail(String message) {
        return new ResponseDto<>(false, message, null);
    }
}
