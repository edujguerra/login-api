package br.com.fiap.loginapi.exception;

import java.util.List;

public record ValidationErrorResponse(
        List<String> errors
) {
}
