package org.minu.dnd13th3backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {

    private final boolean success;
    private final String message;
    private final T data;

    public static <T> ResponseDto<T> success(String message, T data) {
        return new ResponseDto<>(true, message, data);
    }

    public static <T> ResponseDto<T> success(String message) {
        return new ResponseDto<>(true, message, null);
    }

    public static <T> ResponseDto<T> fail(String message) {
        return new ResponseDto<>(false, message, null);
    }
}
