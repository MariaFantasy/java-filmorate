package ru.yandex.practicum.filmorate.exception;

public class ErrorResponse {
    private String error;
    private String errorMessage;

    public ErrorResponse(String error, String errorMessage) {
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public String getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
