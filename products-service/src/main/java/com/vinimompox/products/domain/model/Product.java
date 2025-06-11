// products-service/src/main/java/com/vinimompox/products/domain/model/Product.java
package com.vinimompox.products.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version; // ¡Importante: importa esta anotación!

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private Integer vintage;
    private Double price;
    private Integer stock;
    private String description;

    @Version // Anotación clave para el bloqueo optimista
    private Long version; // Campo que Hibernate usará para el control de versiones

    public Product() {
    }

    public Product(String name, String category, Integer vintage, Double price, Integer stock, String description) {
        this.name = name;
        this.category = category;
        this.vintage = vintage;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    // --- Getters y Setters ---

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getVintage() {
        return vintage;
    }

    public void setVintage(Integer vintage) {
        this.vintage = vintage;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // --- Getter y Setter para el campo 'version' ---
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
    // ---------------------------------------------

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", vintage=" + vintage +
                ", price=" + price +
                ", stock=" + stock +
                ", description='" + description + '\'' +
                ", version=" + version + // Incluimos la versión en el toString
                '}';
    }
}