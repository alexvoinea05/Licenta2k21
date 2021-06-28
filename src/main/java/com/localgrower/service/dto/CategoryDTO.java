package com.localgrower.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.localgrower.domain.Category} entity.
 */
public class CategoryDTO implements Serializable {

    private Long idCategory;

    @NotNull
    @Size(min = 1)
    private String categoryName;

    private ZonedDateTime createdAt;

    private ZonedDateTime modifiedAt;

    public Long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Long idCategory) {
        this.idCategory = idCategory;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryDTO)) {
            return false;
        }

        CategoryDTO categoryDTO = (CategoryDTO) o;
        if (this.idCategory == null) {
            return false;
        }
        return Objects.equals(this.idCategory, categoryDTO.idCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idCategory);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoryDTO{" +
            "idCategory=" + getIdCategory() +
            ", categoryName='" + getCategoryName() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", modifiedAt='" + getModifiedAt() + "'" +
            "}";
    }
}
