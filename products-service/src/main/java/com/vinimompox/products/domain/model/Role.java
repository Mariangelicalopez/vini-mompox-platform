// src/main/java/com/vinimompox/products/domain/model/Role.java

package com.vinimompox.products.domain.model;

import jakarta.persistence.*; // Usa jakarta.persistence para Spring Boot 3+
import java.util.Objects;

@Entity // Indica que esta es una entidad JPA
@Table(name = "roles") // Mapea a la tabla 'roles' en la base de datos
public class Role {

    @Id // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID autoincremental
    private Long id;

    @Column(unique = true, nullable = false) // El nombre del rol debe ser único y no nulo
    private String name; // Aquí guardaremos "ADMIN", "USER", etc.

    // Constructor por defecto (requerido por JPA)
    public Role() {
    }

    // Constructor para facilitar la creación de roles
    public Role(String name) {
        this.name = name;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Métodos equals y hashCode para comparar objetos Role
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        // Compara por 'name' o por 'id' si el 'id' ya ha sido persistido
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Role{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}