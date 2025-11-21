package cl.milsabores.authservice.service;

import cl.milsabores.authservice.model.Usuario;
import cl.milsabores.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Crear usuario (registro)
    public Usuario crear(Usuario u) {

        // Validaciones
        if (repo.existsById(u.getRut())) {
            throw new RuntimeException("El rut ya está registrado");
        }

        if (repo.existsByMail(u.getMail())) {
            throw new RuntimeException("El correo ya existe");
        }

        // Encriptar contraseña
        u.setPassword(encoder.encode(u.getPassword()));

        return repo.save(u);
    }

    // Buscar por ID Firebase
    public Optional<Usuario> buscarPorFirebase(String idFirebase) {
        return repo.findByIdfirebase(idFirebase);
    }

    // Buscar por RUT
    public Optional<Usuario> buscarPorRut(String rut) {
        return repo.findById(rut);
    }

    // Login seguro (BCrypt)
    public Optional<Usuario> login(String email, String password) {
        return repo.findByMail(email)
                .filter(u -> encoder.matches(password, u.getPassword()));
    }

    // Actualizar nombre
    public Usuario actualizarNombre(String rut, String nuevoNombre) {
        Usuario usuario = repo.findById(rut)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(nuevoNombre);
        return repo.save(usuario);
    }

    // Actualizar imagen
    public Usuario actualizarImagen(String rut, byte[] imagen) {
        Usuario usuario = repo.findById(rut)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setImagen(imagen);
        return repo.save(usuario);
    }
}
