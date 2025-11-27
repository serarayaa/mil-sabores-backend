package cl.milsabores.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

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

    // Rol del usuario (1 = cliente)
    private Integer idrol;

    // Integración futura con Firebase (puede quedar null)
    private String idfirebase;

    @Lob
    private byte[] imagen;

    // Nueva columna: Fecha de nacimiento
    @Column(name = "FECHANAC")
    private LocalDate fechaNac;

    // Getter calculado (NO se persiste en la BD)
    public Integer getEdad() {
        if (fechaNac == null) return null;
        return Period.between(fechaNac, LocalDate.now()).getYears();
    }
}
