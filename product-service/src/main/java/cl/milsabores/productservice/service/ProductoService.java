package cl.milsabores.productservice.service;

import cl.milsabores.productservice.model.Producto;
import cl.milsabores.productservice.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository repo;

    public List<Producto> listarTodos() {
        return repo.findAll();
    }

    public List<Producto> listarDisponibles() {
        return repo.findByDisponibleTrue();
    }

    public Producto buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public List<Producto> buscarPorCategoria(String categoria) {
        return repo.findByCategoriaIgnoreCase(categoria);
    }

    public Producto crear(Producto p) {
        return repo.save(p);
    }

    public Producto actualizar(Long id, Producto nuevo) {
        Producto existente = buscarPorId(id);

        existente.setNombre(nuevo.getNombre());
        existente.setDescripcion(nuevo.getDescripcion());
        existente.setPrecio(nuevo.getPrecio());
        existente.setCategoria(nuevo.getCategoria());
        existente.setDisponible(nuevo.isDisponible());
        existente.setUrlImagen(nuevo.getUrlImagen());

        return repo.save(existente);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Producto no encontrado");
        }
        repo.deleteById(id);
    }
}
