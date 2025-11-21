package cl.milsabores.authservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity          // Mapea la clase a una tabla en la BD
@Data            // Genera getters/setters, toString, equals, hashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @Id          // RUT será la clave primaria
    private String rut;

    private String nombre;
    private String mail;
    private String password;
    private Integer idrol;
    private String idfirebase;

    @Lob        // Imagen como arreglo de bytes (opcional)
    private byte[] imagen;
}
