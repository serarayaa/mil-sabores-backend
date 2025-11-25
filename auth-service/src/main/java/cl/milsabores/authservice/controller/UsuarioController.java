package cl.milsabores.authservice.controller;

import cl.milsabores.authservice.dto.ActualizarNombreRequest;
import cl.milsabores.authservice.dto.CrearUsuarioRequest;
import cl.milsabores.authservice.dto.LoginRequest;
import cl.milsabores.authservice.dto.UsuarioResponseDto;
import cl.milsabores.authservice.model.Usuario;
import cl.milsabores.authservice.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ============================
    // REGISTRO
    // ============================
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody CrearUsuarioRequest request) {
        try {
            UsuarioResponseDto respuesta = usuarioService.registrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============================
    // LOGIN
    // ============================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            UsuarioResponseDto respuesta = usuarioService.login(request);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // ============================
    // BUSCAR POR ID FIREBASE
    // ============================
    @GetMapping("/usuarios/firebase/{idFirebase}")
    public ResponseEntity<?> buscarPorFirebase(@PathVariable String idFirebase) {
        Optional<Usuario> usuario = usuarioService.buscarPorFirebase(idFirebase);
        return usuario
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ============================
    // ACTUALIZAR NOMBRE
    // ============================
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
