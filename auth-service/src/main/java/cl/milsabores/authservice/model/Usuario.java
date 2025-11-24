package cl.milsabores.authservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @Id
    private String rut;

    private String nombre;
    private String mail;
    private String password;
    private Integer idrol;
    private String idfirebase;

    @Lob
    private byte[] imagen;
}
