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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    // ============================
    // REGISTRO DE USUARIO
    // ============================
    public UsuarioResponseDto registrar(CrearUsuarioRequest request) {

        // Validar correo único
        if (usuarioRepository.existsByMail(request.mail())) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado");
        }

        // Parsear fecha de nacimiento en formato "dd-MM-yyyy"
        LocalDate fechaNac = null;
        if (request.fechaNac() != null && !request.fechaNac().isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                fechaNac = LocalDate.parse(request.fechaNac(), formatter);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de fecha inválido. Usa dd-MM-yyyy (ej: 10-05-2020)");
            }
        }

        // Crear entidad Usuario
        Usuario usuario = Usuario.builder()
                .rut(request.rut())
                .nombre(request.nombre())
                .mail(request.mail())
                .password(passwordEncoder.encode(request.password()))
                // 🔹 Forzamos rol cliente = 1
                .idrol(1)
                .idfirebase(request.idfirebase())
                .fechaNac(fechaNac)
                .build();

        usuarioRepository.save(usuario);

        // Generar token JWT
        String token = jwtUtil.generateToken(usuario.getRut(), usuario.getMail());

        return new UsuarioResponseDto(
                usuario.getRut(),
                usuario.getMail(),
                token
        );
    }

    // ============================
    // LOGIN
    // ============================
    public UsuarioResponseDto login(LoginRequest request) {
        // Asumo que LoginRequest tiene: String email, String password
        Optional<Usuario> optionalUsuario = usuarioRepository.findByMail(request.mail());

        Usuario usuario = optionalUsuario
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos");
        }

        String token = jwtUtil.generateToken(usuario.getRut(), usuario.getMail());

        return new UsuarioResponseDto(
                usuario.getRut(),
                usuario.getMail(),
                token
        );
    }

    // ============================
    // BUSCAR POR FIREBASE
    // ============================
    public Optional<Usuario> buscarPorFirebase(String idFirebase) {
        return usuarioRepository.findByIdfirebase(idFirebase);
    }

    // ============================
    // ACTUALIZAR NOMBRE
    // ============================
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
