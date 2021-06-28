package com.localgrower.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BigDecimalFilter;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.localgrower.domain.Product} entity. This class is used
 * in {@link com.localgrower.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter idProduct;

    private StringFilter name;

    private BigDecimalFilter price;

    private BigDecimalFilter stock;

    private StringFilter imageUrl;

    private StringFilter productUrl;

    private ZonedDateTimeFilter createdAt;

    private ZonedDateTimeFilter modifiedAt;

    private LongFilter cartItemsId;

    private LongFilter idCategoryId;

    private LongFilter idGrowerId;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.idProduct = other.idProduct == null ? null : other.idProduct.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.price = other.price == null ? null : other.price.copy();
        this.stock = other.stock == null ? null : other.stock.copy();
        this.imageUrl = other.imageUrl == null ? null : other.imageUrl.copy();
        this.productUrl = other.productUrl == null ? null : other.productUrl.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.modifiedAt = other.modifiedAt == null ? null : other.modifiedAt.copy();
        this.cartItemsId = other.cartItemsId == null ? null : other.cartItemsId.copy();
        this.idCategoryId = other.idCategoryId == null ? null : other.idCategoryId.copy();
        this.idGrowerId = other.idGrowerId == null ? null : other.idGrowerId.copy();
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
    }

    public LongFilter getIdProduct() {
        return idProduct;
    }

    public LongFilter idProduct() {
        if (idProduct == null) {
            idProduct = new LongFilter();
        }
        return idProduct;
    }

    public void setIdProduct(LongFilter idProduct) {
        this.idProduct = idProduct;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public BigDecimalFilter price() {
        if (price == null) {
            price = new BigDecimalFilter();
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public BigDecimalFilter getStock() {
        return stock;
    }

    public BigDecimalFilter stock() {
        if (stock == null) {
            stock = new BigDecimalFilter();
        }
        return stock;
    }

    public void setStock(BigDecimalFilter stock) {
        this.stock = stock;
    }

    public StringFilter getImageUrl() {
        return imageUrl;
    }

    public StringFilter imageUrl() {
        if (imageUrl == null) {
            imageUrl = new StringFilter();
        }
        return imageUrl;
    }

    public void setImageUrl(StringFilter imageUrl) {
        this.imageUrl = imageUrl;
    }

    public StringFilter getProductUrl() {
        return productUrl;
    }

    public StringFilter productUrl() {
        if (productUrl == null) {
            productUrl = new StringFilter();
        }
        return productUrl;
    }

    public void setProductUrl(StringFilter productUrl) {
        this.productUrl = productUrl;
    }

    public ZonedDateTimeFilter getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTimeFilter createdAt() {
        if (createdAt == null) {
            createdAt = new ZonedDateTimeFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTimeFilter createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTimeFilter getModifiedAt() {
        return modifiedAt;
    }

    public ZonedDateTimeFilter modifiedAt() {
        if (modifiedAt == null) {
            modifiedAt = new ZonedDateTimeFilter();
        }
        return modifiedAt;
    }

    public void setModifiedAt(ZonedDateTimeFilter modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public LongFilter getCartItemsId() {
        return cartItemsId;
    }

    public LongFilter cartItemsId() {
        if (cartItemsId == null) {
            cartItemsId = new LongFilter();
        }
        return cartItemsId;
    }

    public void setCartItemsId(LongFilter cartItemsId) {
        this.cartItemsId = cartItemsId;
    }

    public LongFilter getIdCategoryId() {
        return idCategoryId;
    }

    public LongFilter idCategoryId() {
        if (idCategoryId == null) {
            idCategoryId = new LongFilter();
        }
        return idCategoryId;
    }

    public void setIdCategoryId(LongFilter idCategoryId) {
        this.idCategoryId = idCategoryId;
    }

    public LongFilter getIdGrowerId() {
        return idGrowerId;
    }

    public LongFilter idGrowerId() {
        if (idGrowerId == null) {
            idGrowerId = new LongFilter();
        }
        return idGrowerId;
    }

    public void setIdGrowerId(LongFilter idGrowerId) {
        this.idGrowerId = idGrowerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(idProduct, that.idProduct) &&
            Objects.equals(name, that.name) &&
            Objects.equals(price, that.price) &&
            Objects.equals(stock, that.stock) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(productUrl, that.productUrl) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(modifiedAt, that.modifiedAt) &&
            Objects.equals(cartItemsId, that.cartItemsId) &&
            Objects.equals(idCategoryId, that.idCategoryId) &&
            Objects.equals(idGrowerId, that.idGrowerId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            idProduct,
            name,
            price,
            stock,
            imageUrl,
            productUrl,
            createdAt,
            modifiedAt,
            cartItemsId,
            idCategoryId,
            idGrowerId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            (idProduct != null ? "idProduct=" + idProduct + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (price != null ? "price=" + price + ", " : "") +
            (stock != null ? "stock=" + stock + ", " : "") +
            (imageUrl != null ? "imageUrl=" + imageUrl + ", " : "") +
            (productUrl != null ? "productUrl=" + productUrl + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (modifiedAt != null ? "modifiedAt=" + modifiedAt + ", " : "") +
            (cartItemsId != null ? "cartItemsId=" + cartItemsId + ", " : "") +
            (idCategoryId != null ? "idCategoryId=" + idCategoryId + ", " : "") +
            (idGrowerId != null ? "idGrowerId=" + idGrowerId + ", " : "") +
            "}";
    }
}
