package cl.milsabores.authservice.controller;

import cl.milsabores.authservice.dto.ActualizarNombreRequest;
import cl.milsabores.authservice.dto.CrearUsuarioRequest;
import cl.milsabores.authservice.dto.LoginRequest;
import cl.milsabores.authservice.dto.UsuarioResponseDto;
import cl.milsabores.authservice.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody CrearUsuarioRequest request) {
        try {
            UsuarioResponseDto respuesta = usuarioService.registrarUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            UsuarioResponseDto respuesta = usuarioService.login(request);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/actualizar-nombre/{rut}")
    public ResponseEntity<?> actualizarNombre(
            @PathVariable String rut,
            @RequestBody ActualizarNombreRequest request
    ) {
        try {
            UsuarioResponseDto respuesta = usuarioService.actualizarNombre(rut, request.nuevoNombre());
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
