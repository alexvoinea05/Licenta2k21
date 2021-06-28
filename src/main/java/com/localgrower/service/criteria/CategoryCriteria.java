package com.localgrower.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.localgrower.domain.Category} entity. This class is used
 * in {@link com.localgrower.web.rest.CategoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CategoryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter idCategory;

    private StringFilter categoryName;

    private ZonedDateTimeFilter createdAt;

    private ZonedDateTimeFilter modifiedAt;

    private LongFilter productId;

    public CategoryCriteria() {}

    public CategoryCriteria(CategoryCriteria other) {
        this.idCategory = other.idCategory == null ? null : other.idCategory.copy();
        this.categoryName = other.categoryName == null ? null : other.categoryName.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.modifiedAt = other.modifiedAt == null ? null : other.modifiedAt.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
    }

    @Override
    public CategoryCriteria copy() {
        return new CategoryCriteria(this);
    }

    public LongFilter getIdCategory() {
        return idCategory;
    }

    public LongFilter idCategory() {
        if (idCategory == null) {
            idCategory = new LongFilter();
        }
        return idCategory;
    }

    public void setIdCategory(LongFilter idCategory) {
        this.idCategory = idCategory;
    }

    public StringFilter getCategoryName() {
        return categoryName;
    }

    public StringFilter categoryName() {
        if (categoryName == null) {
            categoryName = new StringFilter();
        }
        return categoryName;
    }

    public void setCategoryName(StringFilter categoryName) {
        this.categoryName = categoryName;
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

    public LongFilter getProductId() {
        return productId;
    }

    public LongFilter productId() {
        if (productId == null) {
            productId = new LongFilter();
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CategoryCriteria that = (CategoryCriteria) o;
        return (
            Objects.equals(idCategory, that.idCategory) &&
            Objects.equals(categoryName, that.categoryName) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(modifiedAt, that.modifiedAt) &&
            Objects.equals(productId, that.productId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCategory, categoryName, createdAt, modifiedAt, productId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoryCriteria{" +
            (idCategory != null ? "idCategory=" + idCategory + ", " : "") +
            (categoryName != null ? "categoryName=" + categoryName + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (modifiedAt != null ? "modifiedAt=" + modifiedAt + ", " : "") +
            (productId != null ? "productId=" + productId + ", " : "") +
            "}";
    }
}
