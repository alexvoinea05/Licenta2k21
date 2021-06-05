package com.localgrower.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Category.
 */
@Entity
@Table(name = "category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long idCategory;

    @NotNull
    @Size(min = 1)
    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "modified_at")
    private ZonedDateTime modifiedAt;

    @OneToMany(mappedBy = "idCategory")
    @JsonIgnoreProperties(value = { "cartItems", "idCategory", "idGrower" }, allowSetters = true)
    private Set<Product> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Long idCategory) {
        this.idCategory = idCategory;
    }

    public Category idCategory(Long idCategory) {
        this.idCategory = idCategory;
        return this;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public Category categoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Category createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getModifiedAt() {
        return this.modifiedAt;
    }

    public Category modifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public Category products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public Category addProduct(Product product) {
        this.products.add(product);
        product.setIdCategory(this);
        return this;
    }

    public Category removeProduct(Product product) {
        this.products.remove(product);
        product.setIdCategory(null);
        return this;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setIdCategory(null));
        }
        if (products != null) {
            products.forEach(i -> i.setIdCategory(this));
        }
        this.products = products;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return idCategory != null && idCategory.equals(((Category) o).idCategory);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "idCategory=" + getIdCategory() +
            ", categoryName='" + getCategoryName() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", modifiedAt='" + getModifiedAt() + "'" +
            "}";
    }
}
