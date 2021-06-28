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
 * A Product.
 */
@Entity
@Table(name = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long idProduct;

    @NotNull
    @Size(min = 1)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @DecimalMin(value = "0.1")
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @DecimalMin(value = "0.1")
    @Column(name = "stock", precision = 21, scale = 2, nullable = false)
    private BigDecimal stock;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "product_url")
    private String productUrl;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "modified_at")
    private ZonedDateTime modifiedAt;

    @OneToMany(mappedBy = "idProduct")
    @JsonIgnoreProperties(value = { "idProduct", "idOrderDetails" }, allowSetters = true)
    private Set<CartItems> cartItems = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Category idCategory;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "cartOrderDetails", "products" }, allowSetters = true)
    private AppUser idGrower;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public Product idProduct(Long idProduct) {
        this.idProduct = idProduct;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Product price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getStock() {
        return this.stock;
    }

    public Product stock(BigDecimal stock) {
        this.stock = stock;
        return this;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Product imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductUrl() {
        return this.productUrl;
    }

    public Product productUrl(String productUrl) {
        this.productUrl = productUrl;
        return this;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Product createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getModifiedAt() {
        return this.modifiedAt;
    }

    public Product modifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Set<CartItems> getCartItems() {
        return this.cartItems;
    }

    public Product cartItems(Set<CartItems> cartItems) {
        this.setCartItems(cartItems);
        return this;
    }

    public Product addCartItems(CartItems cartItems) {
        this.cartItems.add(cartItems);
        cartItems.setIdProduct(this);
        return this;
    }

    public Product removeCartItems(CartItems cartItems) {
        this.cartItems.remove(cartItems);
        cartItems.setIdProduct(null);
        return this;
    }

    public void setCartItems(Set<CartItems> cartItems) {
        if (this.cartItems != null) {
            this.cartItems.forEach(i -> i.setIdProduct(null));
        }
        if (cartItems != null) {
            cartItems.forEach(i -> i.setIdProduct(this));
        }
        this.cartItems = cartItems;
    }

    public Category getIdCategory() {
        return this.idCategory;
    }

    public Product idCategory(Category category) {
        this.setIdCategory(category);
        return this;
    }

    public void setIdCategory(Category category) {
        this.idCategory = category;
    }

    public AppUser getIdGrower() {
        return this.idGrower;
    }

    public Product idGrower(AppUser appUser) {
        this.setIdGrower(appUser);
        return this;
    }

    public void setIdGrower(AppUser appUser) {
        this.idGrower = appUser;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return idProduct != null && idProduct.equals(((Product) o).idProduct);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "idProduct=" + getIdProduct() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", stock=" + getStock() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", productUrl='" + getProductUrl() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", modifiedAt='" + getModifiedAt() + "'" +
            "}";
    }
}
