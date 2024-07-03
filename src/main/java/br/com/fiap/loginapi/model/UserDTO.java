package br.com.fiap.loginapi.model;

import br.com.fiap.loginapi.entity.User;
import br.com.fiap.loginapi.enums.UserRole;

public record UserDTO(
        Long id,
        String name,
        String email,
        UserRole role
) {

    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
