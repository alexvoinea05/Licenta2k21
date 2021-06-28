package com.localgrower.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A CartOrderDetails.
 */
@Entity
@Table(name = "cart_order_details")
public class CartOrderDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long idCartOrderDetails;

    @DecimalMin(value = "0.1")
    @Column(name = "total_price", precision = 21, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "modified_at")
    private ZonedDateTime modifiedAt;

    @Column(name = "status_command")
    private String statusCommand;

    @OneToMany(mappedBy = "idOrderDetails")
    @JsonIgnoreProperties(value = { "idProduct", "idOrderDetails" }, allowSetters = true)
    private Set<CartItems> cartItems = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "cartOrderDetails", "products" }, allowSetters = true)
    private AppUser idAppUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getIdCartOrderDetails() {
        return idCartOrderDetails;
    }

    public void setIdCartOrderDetails(Long idCartOrderDetails) {
        this.idCartOrderDetails = idCartOrderDetails;
    }

    public CartOrderDetails idCartOrderDetails(Long idCartOrderDetails) {
        this.idCartOrderDetails = idCartOrderDetails;
        return this;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public CartOrderDetails totalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public CartOrderDetails createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getModifiedAt() {
        return this.modifiedAt;
    }

    public CartOrderDetails modifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getStatusCommand() {
        return this.statusCommand;
    }

    public CartOrderDetails statusCommand(String statusCommand) {
        this.statusCommand = statusCommand;
        return this;
    }

    public void setStatusCommand(String statusCommand) {
        this.statusCommand = statusCommand;
    }

    public Set<CartItems> getCartItems() {
        return this.cartItems;
    }

    public CartOrderDetails cartItems(Set<CartItems> cartItems) {
        this.setCartItems(cartItems);
        return this;
    }

    public CartOrderDetails addCartItems(CartItems cartItems) {
        this.cartItems.add(cartItems);
        cartItems.setIdOrderDetails(this);
        return this;
    }

    public CartOrderDetails removeCartItems(CartItems cartItems) {
        this.cartItems.remove(cartItems);
        cartItems.setIdOrderDetails(null);
        return this;
    }

    public void setCartItems(Set<CartItems> cartItems) {
        if (this.cartItems != null) {
            this.cartItems.forEach(i -> i.setIdOrderDetails(null));
        }
        if (cartItems != null) {
            cartItems.forEach(i -> i.setIdOrderDetails(this));
        }
        this.cartItems = cartItems;
    }

    public AppUser getIdAppUser() {
        return this.idAppUser;
    }

    public CartOrderDetails idAppUser(AppUser appUser) {
        this.setIdAppUser(appUser);
        return this;
    }

    public void setIdAppUser(AppUser appUser) {
        this.idAppUser = appUser;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartOrderDetails)) {
            return false;
        }
        return idCartOrderDetails != null && idCartOrderDetails.equals(((CartOrderDetails) o).idCartOrderDetails);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartOrderDetails{" +
            "idCartOrderDetails=" + getIdCartOrderDetails() +
            ", totalPrice=" + getTotalPrice() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", modifiedAt='" + getModifiedAt() + "'" +
            ", statusCommand='" + getStatusCommand() + "'" +
            "}";
    }
}
