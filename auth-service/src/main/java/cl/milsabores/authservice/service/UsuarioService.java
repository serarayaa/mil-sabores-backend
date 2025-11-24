package cl.milsabores.authservice.service;

import cl.milsabores.authservice.dto.CrearUsuarioRequest;
import cl.milsabores.authservice.dto.LoginRequest;
import cl.milsabores.authservice.dto.UsuarioResponseDto;
import cl.milsabores.authservice.model.Usuario;
import cl.milsabores.authservice.repository.UsuarioRepository;
import cl.milsabores.authservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // =====================================
    // REGISTRO
    // =====================================
    public UsuarioResponseDto registrarUsuario(CrearUsuarioRequest request) {

        if (usuarioRepository.existsByMail(request.mail())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .rut(request.rut())
                .nombre(request.nombre())
                .mail(request.mail())
                .password(passwordEncoder.encode(request.password()))
                .idrol(request.idrol())
                .idfirebase(request.idfirebase())
                .build();

        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario.getRut(), usuario.getMail());

        return new UsuarioResponseDto(
                usuario.getRut(),
                usuario.getMail(),
                token
        );
    }

    // =====================================
    // LOGIN
    // =====================================
    public UsuarioResponseDto login(LoginRequest request) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByMail(request.mail());

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario o contraseña inválidos");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new IllegalArgumentException("Usuario o contraseña inválidos");
        }

        String token = jwtUtil.generateToken(usuario.getRut(), usuario.getMail());

        return new UsuarioResponseDto(
                usuario.getRut(),
                usuario.getMail(),
                token
        );
    }

    // =====================================
    // ACTUALIZAR NOMBRE
    // =====================================
    public UsuarioResponseDto actualizarNombre(String rut, String nuevoNombre) {

        Usuario usuario = usuarioRepository.findById(rut)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setNombre(nuevoNombre);
        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario.getRut(), usuario.getMail());

        return new UsuarioResponseDto(
                usuario.getRut(),
                usuario.getMail(),
                token
        );
    }
}
