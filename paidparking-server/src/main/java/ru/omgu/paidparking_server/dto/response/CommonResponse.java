package ru.omgu.paidparking_server.dto.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommonResponse<T> {
    private final T data;
    private final int status;
    private final String messageError;

    public CommonResponse(int status, String messageError) {
        this.data = null;
        this.status = status;
        this.messageError = messageError;
    }

    public CommonResponse(T data, int status) {
        this.data = data;
        this.status = status;
        this.messageError = null;
    }

    public CommonResponse(int status) {
        this.data = null;
        this.status = status;
        this.messageError = null;
    }
}