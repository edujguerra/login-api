package br.com.fiap.loginapi.controller;

import br.com.fiap.loginapi.entity.User;
import br.com.fiap.loginapi.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarUm(@PathVariable Long id) {

        return service.buscarUm(id);
    }
}
