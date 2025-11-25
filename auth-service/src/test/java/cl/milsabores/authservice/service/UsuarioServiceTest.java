package cl.milsabores.authservice.service;

import cl.milsabores.authservice.dto.CrearUsuarioRequest;
import cl.milsabores.authservice.dto.LoginRequest;
import cl.milsabores.authservice.dto.UsuarioResponseDto;
import cl.milsabores.authservice.model.Usuario;
import cl.milsabores.authservice.repository.UsuarioRepository;
import cl.milsabores.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link UsuarioService}.
 *
 * Estas pruebas verifican el flujo básico de registro, login y actualización
 * de nombre utilizando Mockito para simular las dependencias.  Al ejecutar
 * estas pruebas con `mvn test` se contribuye a aumentar la cobertura de la
 * lógica de negocio del servicio de autenticación.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioService usuarioService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void registrarUsuario_deberiaGuardarUsuarioYCrearToken() {
        // Arrange
        CrearUsuarioRequest request = new CrearUsuarioRequest(
                "12345678-9",
                "Usuario Prueba",
                "usuario@ejemplo.com",
                "contrasena",
                1,
                "firebaseId"
        );
        // el correo no está registrado
        when(usuarioRepository.existsByMail(request.mail())).thenReturn(false);
        // simular que se guarda el usuario y se devuelve la entidad
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("tokenGenerado");

        // Act
        UsuarioResponseDto response = usuarioService.registrarUsuario(request);

        // Assert
        // UsuarioResponseDto expone getters convencionales (getEmail, getToken).
        // Verificamos usando el getter proporcionado por Lombok o el POJO
        assertEquals(request.mail(), response.getEmail());
        assertNotNull(response.getToken());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(jwtUtil, times(1)).generateToken(anyString(), anyString());
    }

    @Test
    void registrarUsuario_deberiaLanzarExcepcionSiCorreoExiste() {
        CrearUsuarioRequest request = new CrearUsuarioRequest(
                "12345678-9",
                "Usuario Prueba",
                "usuario@ejemplo.com",
                "contrasena",
                1,
                "firebaseId"
        );
        when(usuarioRepository.existsByMail(request.mail())).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(request));
        assertTrue(ex.getMessage().contains("El correo ya está registrado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void login_deberiaRetornarTokenConCredencialesValidas() {
        String email = "usuario@ejemplo.com";
        String password = "contrasena";
        Usuario usuario = Usuario.builder()
                .rut("12345678-9")
                .mail(email)
                .password(passwordEncoder.encode(password))
                .build();
        when(usuarioRepository.findByMail(email)).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("tokenLogin");

        UsuarioResponseDto response = usuarioService.login(new LoginRequest(email, password));
        assertEquals(email, response.getEmail());
        assertNotNull(response.getToken());
        verify(jwtUtil, times(1)).generateToken(anyString(), anyString());
    }

    @Test
    void login_deberiaLanzarExcepcionConPasswordIncorrecta() {
        String email = "usuario@ejemplo.com";
        String password = "contrasena";
        Usuario usuario = Usuario.builder()
                .rut("12345678-9")
                .mail(email)
                .password(passwordEncoder.encode(password))
                .build();
        when(usuarioRepository.findByMail(email)).thenReturn(Optional.of(usuario));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.login(new LoginRequest(email, "otraClave")));
        assertTrue(ex.getMessage().contains("Usuario o contraseña inválidos"));
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void actualizarNombre_deberiaPersistirNuevoNombre() {
        Usuario usuario = Usuario.builder()
                .rut("12345678-9")
                .mail("usuario@ejemplo.com")
                .nombre("Nombre Antiguo")
                .build();
        when(usuarioRepository.findById("12345678-9")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("tokenActualizar");

        UsuarioResponseDto response = usuarioService.actualizarNombre("12345678-9", "Nombre Nuevo");
        assertEquals("Nombre Nuevo", usuario.getNombre());
        assertNotNull(response.getToken());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}