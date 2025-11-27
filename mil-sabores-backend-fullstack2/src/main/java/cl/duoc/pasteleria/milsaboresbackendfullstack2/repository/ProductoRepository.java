package cl.duoc.pasteleria.milsaboresbackendfullstack2.repository;

import cl.duoc.pasteleria.milsaboresbackendfullstack2.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
