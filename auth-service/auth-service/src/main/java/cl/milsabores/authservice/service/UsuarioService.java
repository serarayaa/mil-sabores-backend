package cl.milsabores.authservice.service;

import cl.milsabores.authservice.model.Usuario;
import cl.milsabores.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;

    public Usuario crear(Usuario u) {
        return repo.save(u);
    }

    public Optional<Usuario> buscarPorFirebase(String idFirebase) {
        return repo.findByIdfirebase(idFirebase);
    }

    public Optional<Usuario> buscarPorRut(String rut) {
        return repo.findById(rut);
    }

    public Optional<Usuario> login(String email, String password) {
        return repo.findByMail(email)
                .filter(u -> u.getPassword().equals(password));
    }
}
