package br.com.fiap.loginapi.exception;

public record ApiErrorResponse(String message, int status) {
}
