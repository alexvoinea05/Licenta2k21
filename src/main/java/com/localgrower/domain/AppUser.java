package com.localgrower.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A AppUser.
 */
@Entity
@Table(name = "app_user")
public class AppUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long idAppUser;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @NotNull
    @Column(name = "adress", nullable = false)
    private String adress;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "modified_at")
    private ZonedDateTime modifiedAt;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "idAppUser")
    @JsonIgnoreProperties(value = { "cartItems", "idAppUser" }, allowSetters = true)
    private Set<CartOrderDetails> cartOrderDetails = new HashSet<>();

    @OneToMany(mappedBy = "idGrower")
    @JsonIgnoreProperties(value = { "cartItems", "idCategory", "idGrower" }, allowSetters = true)
    private Set<Product> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getIdAppUser() {
        return idAppUser;
    }

    public void setIdAppUser(Long idAppUser) {
        this.idAppUser = idAppUser;
    }

    public AppUser idAppUser(Long idAppUser) {
        this.idAppUser = idAppUser;
        return this;
    }

    public String getCertificateUrl() {
        return this.certificateUrl;
    }

    public AppUser certificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
        return this;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public String getAdress() {
        return this.adress;
    }

    public AppUser adress(String adress) {
        this.adress = adress;
        return this;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public AppUser createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getModifiedAt() {
        return this.modifiedAt;
    }

    public AppUser modifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public User getUser() {
        return this.user;
    }

    public AppUser user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<CartOrderDetails> getCartOrderDetails() {
        return this.cartOrderDetails;
    }

    public AppUser cartOrderDetails(Set<CartOrderDetails> cartOrderDetails) {
        this.setCartOrderDetails(cartOrderDetails);
        return this;
    }

    public AppUser addCartOrderDetails(CartOrderDetails cartOrderDetails) {
        this.cartOrderDetails.add(cartOrderDetails);
        cartOrderDetails.setIdAppUser(this);
        return this;
    }

    public AppUser removeCartOrderDetails(CartOrderDetails cartOrderDetails) {
        this.cartOrderDetails.remove(cartOrderDetails);
        cartOrderDetails.setIdAppUser(null);
        return this;
    }

    public void setCartOrderDetails(Set<CartOrderDetails> cartOrderDetails) {
        if (this.cartOrderDetails != null) {
            this.cartOrderDetails.forEach(i -> i.setIdAppUser(null));
        }
        if (cartOrderDetails != null) {
            cartOrderDetails.forEach(i -> i.setIdAppUser(this));
        }
        this.cartOrderDetails = cartOrderDetails;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public AppUser products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public AppUser addProduct(Product product) {
        this.products.add(product);
        product.setIdGrower(this);
        return this;
    }

    public AppUser removeProduct(Product product) {
        this.products.remove(product);
        product.setIdGrower(null);
        return this;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setIdGrower(null));
        }
        if (products != null) {
            products.forEach(i -> i.setIdGrower(this));
        }
        this.products = products;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser)) {
            return false;
        }
        return idAppUser != null && idAppUser.equals(((AppUser) o).idAppUser);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUser{" +
            "idAppUser=" + getIdAppUser() +
            ", certificateUrl='" + getCertificateUrl() + "'" +
            ", adress='" + getAdress() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", modifiedAt='" + getModifiedAt() + "'" +
            "}";
    }
}
