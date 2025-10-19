package org.lemanoman.filerepository.data;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

public class ResponseDTO<T> extends ResponseEntity<T> {
    private String message;
    private boolean success;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ResponseDTO<?> that = (ResponseDTO<?>) o;
        return success == that.success && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message, success);
    }

    public ResponseDTO(String message, boolean success, HttpStatusCode statusCode, T body) {
        super(body, statusCode);
        this.message = message;
        this.success = success;
    }

    public ResponseDTO(String message, boolean success, HttpStatusCode status) {
        super(status);
        this.message = message;
        this.success = success;
    }

    public ResponseDTO(HttpStatusCode status) {
        super(status);
    }

    public ResponseDTO(T body, HttpStatusCode status) {
        super(body, status);
    }

    public ResponseDTO(MultiValueMap<String, String> headers, HttpStatusCode status) {
        super(headers, status);
    }

    public ResponseDTO(T body, MultiValueMap<String, String> headers, int rawStatus) {
        super(body, headers, rawStatus);
    }


    public ResponseDTO(T body, MultiValueMap<String, String> headers, HttpStatusCode statusCode) {
        super(body, headers, statusCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
