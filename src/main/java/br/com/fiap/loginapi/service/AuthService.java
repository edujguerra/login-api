package br.com.fiap.loginapi.service;

import br.com.fiap.loginapi.entity.User;
import br.com.fiap.loginapi.enums.UserRole;
import br.com.fiap.loginapi.exception.UnauthorizedException;
import br.com.fiap.loginapi.exception.UserException;
import br.com.fiap.loginapi.model.AuthenticateUser;
import br.com.fiap.loginapi.model.AuthenticationRequest;
import br.com.fiap.loginapi.model.SingUpRequest;
import br.com.fiap.loginapi.model.UserDTO;
import br.com.fiap.loginapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    public UserDTO createUser(SingUpRequest singUpRequest) {
        userRepository.findFirstByEmail(singUpRequest.email())
                .ifPresent(user -> {
                    throw new UserException(
                            HttpStatus.BAD_REQUEST.value(),
                            "Usuário com email  " + singUpRequest.email() + " já existe"
                    );
                });

        var user = singUpRequest.toUser();
        user.setPassword(bCryptPasswordEncoder.encode(singUpRequest.password()));

        final User createdUser = userRepository.save(user);
        return UserDTO.fromUser(createdUser);
    }

    public AuthenticateUser authenticate(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.email(),
                            authenticationRequest.password()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(403, "Usuário e/ou senha inválido(s).");
        }

        UserDetails userDetails = loadUserByUsername(authenticationRequest.email());
        Optional<User> userOptional = userRepository.findFirstByEmail(authenticationRequest.email());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String jwt = generateToken(
                    userDetails.getUsername(),
                    user.getRole(),
                    user.getId()
            );

            return new AuthenticateUser(user.getId(), user.getEmail(), jwt);

        }

        throw new UnauthorizedException(401, "Não autorizado.");
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    public String generateToken(String email, UserRole role, Long id) {
        return jwtService.generateToken(email, role, id);
    }

    private UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findFirstByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
