// src/main/java/com/vinimompox/products/domain/model/User.java

package com.vinimompox.products.domain.model;

import jakarta.persistence.*; // Para anotaciones JPA (asegúrate de que sea jakarta.persistence si usas Spring Boot 3+)
import java.util.HashSet;
import java.util.Set;
import java.util.Objects; // ¡Añadida esta importación!

@Entity
@Table(name = "users") // Mapea a la tabla 'users' en la base de datos
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID autoincremental
    private Long id;

    @Column(unique = true, nullable = false) // 'username' debe ser único y no nulo
    private String username;

    private String password; // ¡Asegúrate de que esto esté encriptado antes de guardarlo en DB!

    private String email; // Opcional, pero buena práctica para tenerlo

    // Relación Many-to-Many con la entidad Role
    @ManyToMany(fetch = FetchType.EAGER) // Carga los roles inmediatamente con el usuario (puede afectar el rendimiento si hay muchos roles)
    @JoinTable(
        name = "user_roles", // Nombre de la tabla de unión (asociación)
        joinColumns = @JoinColumn(name = "user_id"), // Columna en 'user_roles' que referencia a la PK de 'users'
        inverseJoinColumns = @JoinColumn(name = "role_id") // Columna en 'user_roles' que referencia a la PK de 'roles'
    )
    private Set<Role> roles = new HashSet<>(); // Usa la clase de entidad Role

    // Constructor por defecto (requerido por JPA)
    public User() {
    }

    // Getters y Setters para acceder a las propiedades de la entidad
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Métodos equals y hashCode: Cruciales para la comparación de objetos
    // y para el correcto funcionamiento en colecciones basadas en hash (como HashSet)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Compara por 'id' y 'username' para asegurar unicidad
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        // Genera un hash basado en 'id' y 'username'
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}