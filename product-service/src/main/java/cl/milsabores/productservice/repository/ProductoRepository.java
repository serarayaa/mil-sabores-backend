package cl.milsabores.productservice.repository;

import cl.milsabores.productservice.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByDisponibleTrue();

    List<Producto> findByCategoriaIgnoreCase(String categoria);
}
