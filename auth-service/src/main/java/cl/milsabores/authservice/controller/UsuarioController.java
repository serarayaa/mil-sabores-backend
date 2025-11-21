package cl.milsabores.authservice.controller;

import cl.milsabores.authservice.dto.ActualizarNombreRequest;
import cl.milsabores.authservice.dto.CrearUsuarioRequest;
import cl.milsabores.authservice.dto.LoginRequest;
import cl.milsabores.authservice.dto.UsuarioResponseDto;
import cl.milsabores.authservice.model.Usuario;
import cl.milsabores.authservice.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Crear usuario (registro)
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> crearUsuario(@RequestBody CrearUsuarioRequest request) {
        Usuario usuario = Usuario.builder()
                .rut(request.rut())
                .nombre(request.nombre())
                .mail(request.mail())
                .password(request.password())
                .idrol(request.idrol())
                .idfirebase(request.idfirebase())
                .build();

        Usuario creado = usuarioService.crear(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(creado));
    }

    // Buscar usuario por id de Firebase
    @GetMapping("/firebase/{idFirebase}")
    public ResponseEntity<UsuarioResponseDto> buscarPorFirebase(@PathVariable String idFirebase) {
        return usuarioService.buscarPorFirebase(idFirebase)
                .map(u -> ResponseEntity.ok(toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Login: JSON en el body
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDto> login(@RequestBody LoginRequest request) {
        return usuarioService.login(request.mail(), request.password())
                .map(u -> ResponseEntity.ok(toDto(u)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // Actualizar nombre
    @PatchMapping("/{rut}/nombre")
    public ResponseEntity<UsuarioResponseDto> actualizarNombre(
            @PathVariable String rut,
            @RequestBody ActualizarNombreRequest request) {

        Usuario actualizado = usuarioService.actualizarNombre(rut, request.nuevoNombre());
        return ResponseEntity.ok(toDto(actualizado));
    }

    // Actualizar imagen
    @PatchMapping(value = "/{rut}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioResponseDto> actualizarImagen(
            @PathVariable String rut,
            @RequestParam("file") MultipartFile file) throws IOException {

        Usuario actualizado = usuarioService.actualizarImagen(rut, file.getBytes());
        return ResponseEntity.ok(toDto(actualizado));
    }

    // Mapper entidad -> DTO
    private UsuarioResponseDto toDto(Usuario u) {
        return new UsuarioResponseDto(
                u.getRut(),
                u.getNombre(),
                u.getMail(),
                u.getIdrol(),
                u.getIdfirebase()
        );
    }
}
