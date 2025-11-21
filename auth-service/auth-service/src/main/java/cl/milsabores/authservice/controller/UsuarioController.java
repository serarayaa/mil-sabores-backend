package cl.milsabores.authservice.controller;

import cl.milsabores.authservice.model.Usuario;
import cl.milsabores.authservice.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Crear usuario (registro)
    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        Usuario creado = usuarioService.crear(usuario);
        return ResponseEntity.ok(creado);
    }

    // Buscar usuario por id de Firebase
    @GetMapping("/usuarios/firebase/{idFirebase}")
    public ResponseEntity<?> buscarPorFirebase(@PathVariable String idFirebase) {
        return usuarioService.buscarPorFirebase(idFirebase)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.notFound().build());
    }

    // Login simple: email + password por query o form-data
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestParam String mail,
                                   @RequestParam String password) {

        return usuarioService.login(mail, password)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // Actualizar nombre
    @PatchMapping("/usuarios/{rut}/nombre")
    public ResponseEntity<?> actualizarNombre(
            @PathVariable String rut,
            @RequestParam String nuevoNombre) {

        return usuarioService.buscarPorRut(rut)
                .map(usuario -> {
                    usuario.setNombre(nuevoNombre);
                    usuarioService.crear(usuario);   // reutilizamos save()
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar imagen
    @PatchMapping("/usuarios/{rut}/imagen")
    public ResponseEntity<?> actualizarImagen(
            @PathVariable String rut,
            @RequestParam("file") MultipartFile file) {

        return usuarioService.buscarPorRut(rut)
                .map(usuario -> {
                    try {
                        usuario.setImagen(file.getBytes());
                        usuarioService.crear(usuario);
                        return ResponseEntity.ok(usuario);
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
