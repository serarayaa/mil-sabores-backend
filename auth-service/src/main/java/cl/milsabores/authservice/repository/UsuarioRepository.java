package cl.milsabores.authservice.repository;

import cl.milsabores.authservice.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    // Buscar por idfirebase
    Optional<Usuario> findByIdfirebase(String idfirebase);

    // Buscar por correo (login)
    Optional<Usuario> findByMail(String mail);

    // Validar correos repetidos
    boolean existsByMail(String mail);
}
