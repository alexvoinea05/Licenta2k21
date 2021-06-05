package com.localgrower.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * not an ignored comment
 */
@Entity
@Table(name = "cart_items")
public class CartItems implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long idCartItems;

    @NotNull
    @DecimalMin(value = "0.1")
    @Column(name = "quantity", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "modified_at")
    private ZonedDateTime modifiedAt;

    @ManyToOne
    @JsonIgnoreProperties(value = { "cartItems", "idCategory", "idGrower" }, allowSetters = true)
    private Product idProduct;

    @ManyToOne
    @JsonIgnoreProperties(value = { "cartItems", "idAppUser" }, allowSetters = true)
    private CartOrderDetails idOrderDetails;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getIdCartItems() {
        return idCartItems;
    }

    public void setIdCartItems(Long idCartItems) {
        this.idCartItems = idCartItems;
    }

    public CartItems idCartItems(Long idCartItems) {
        this.idCartItems = idCartItems;
        return this;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public CartItems quantity(BigDecimal quantity) {
        this.quantity = quantity;
        return this;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public CartItems createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getModifiedAt() {
        return this.modifiedAt;
    }

    public CartItems modifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Product getIdProduct() {
        return this.idProduct;
    }

    public CartItems idProduct(Product product) {
        this.setIdProduct(product);
        return this;
    }

    public void setIdProduct(Product product) {
        this.idProduct = product;
    }

    public CartOrderDetails getIdOrderDetails() {
        return this.idOrderDetails;
    }

    public CartItems idOrderDetails(CartOrderDetails cartOrderDetails) {
        this.setIdOrderDetails(cartOrderDetails);
        return this;
    }

    public void setIdOrderDetails(CartOrderDetails cartOrderDetails) {
        this.idOrderDetails = cartOrderDetails;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartItems)) {
            return false;
        }
        return idCartItems != null && idCartItems.equals(((CartItems) o).idCartItems);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartItems{" +
            "idCartItems=" + getIdCartItems() +
            ", quantity=" + getQuantity() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", modifiedAt='" + getModifiedAt() + "'" +
            "}";
    }
}
