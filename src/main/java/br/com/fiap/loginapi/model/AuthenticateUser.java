package br.com.fiap.loginapi.model;

public record AuthenticateUser(
        Long id,

        String email,

        String token
) {
}
