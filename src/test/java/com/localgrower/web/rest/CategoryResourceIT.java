package com.localgrower.web.rest;

import static com.localgrower.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.localgrower.IntegrationTest;
import com.localgrower.domain.Category;
import com.localgrower.domain.Product;
import com.localgrower.repository.CategoryRepository;
import com.localgrower.service.criteria.CategoryCriteria;
import com.localgrower.service.dto.CategoryDTO;
import com.localgrower.service.mapper.CategoryMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CategoryResourceIT {

    private static final String DEFAULT_CATEGORY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY_NAME = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_MODIFIED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_MODIFIED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_MODIFIED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{idCategory}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCategoryMockMvc;

    private Category category;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Category createEntity(EntityManager em) {
        Category category = new Category()
            .categoryName(DEFAULT_CATEGORY_NAME)
            .createdAt(DEFAULT_CREATED_AT)
            .modifiedAt(DEFAULT_MODIFIED_AT);
        return category;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Category createUpdatedEntity(EntityManager em) {
        Category category = new Category()
            .categoryName(UPDATED_CATEGORY_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .modifiedAt(UPDATED_MODIFIED_AT);
        return category;
    }

    @BeforeEach
    public void initTest() {
        category = createEntity(em);
    }

    @Test
    @Transactional
    void createCategory() throws Exception {
        int databaseSizeBeforeCreate = categoryRepository.findAll().size();
        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        restCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isCreated());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate + 1);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getCategoryName()).isEqualTo(DEFAULT_CATEGORY_NAME);
        assertThat(testCategory.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCategory.getModifiedAt()).isEqualTo(DEFAULT_MODIFIED_AT);
    }

    @Test
    @Transactional
    void createCategoryWithExistingId() throws Exception {
        // Create the Category with an existing ID
        category.setIdCategory(1L);
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        int databaseSizeBeforeCreate = categoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCategoryNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = categoryRepository.findAll().size();
        // set the field null
        category.setCategoryName(null);

        // Create the Category, which fails.
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        restCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isBadRequest());

        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCategories() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=idCategory,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].idCategory").value(hasItem(category.getIdCategory().intValue())))
            .andExpect(jsonPath("$.[*].categoryName").value(hasItem(DEFAULT_CATEGORY_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].modifiedAt").value(hasItem(sameInstant(DEFAULT_MODIFIED_AT))));
    }

    @Test
    @Transactional
    void getCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get the category
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, category.getIdCategory()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.idCategory").value(category.getIdCategory().intValue()))
            .andExpect(jsonPath("$.categoryName").value(DEFAULT_CATEGORY_NAME))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.modifiedAt").value(sameInstant(DEFAULT_MODIFIED_AT)));
    }

    @Test
    @Transactional
    void getCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        Long id = category.getIdCategory();

        defaultCategoryShouldBeFound("idCategory.equals=" + id);
        defaultCategoryShouldNotBeFound("idCategory.notEquals=" + id);

        defaultCategoryShouldBeFound("idCategory.greaterThanOrEqual=" + id);
        defaultCategoryShouldNotBeFound("idCategory.greaterThan=" + id);

        defaultCategoryShouldBeFound("idCategory.lessThanOrEqual=" + id);
        defaultCategoryShouldNotBeFound("idCategory.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCategoriesByCategoryNameIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryName equals to DEFAULT_CATEGORY_NAME
        defaultCategoryShouldBeFound("categoryName.equals=" + DEFAULT_CATEGORY_NAME);

        // Get all the categoryList where categoryName equals to UPDATED_CATEGORY_NAME
        defaultCategoryShouldNotBeFound("categoryName.equals=" + UPDATED_CATEGORY_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByCategoryNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryName not equals to DEFAULT_CATEGORY_NAME
        defaultCategoryShouldNotBeFound("categoryName.notEquals=" + DEFAULT_CATEGORY_NAME);

        // Get all the categoryList where categoryName not equals to UPDATED_CATEGORY_NAME
        defaultCategoryShouldBeFound("categoryName.notEquals=" + UPDATED_CATEGORY_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByCategoryNameIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryName in DEFAULT_CATEGORY_NAME or UPDATED_CATEGORY_NAME
        defaultCategoryShouldBeFound("categoryName.in=" + DEFAULT_CATEGORY_NAME + "," + UPDATED_CATEGORY_NAME);

        // Get all the categoryList where categoryName equals to UPDATED_CATEGORY_NAME
        defaultCategoryShouldNotBeFound("categoryName.in=" + UPDATED_CATEGORY_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByCategoryNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryName is not null
        defaultCategoryShouldBeFound("categoryName.specified=true");

        // Get all the categoryList where categoryName is null
        defaultCategoryShouldNotBeFound("categoryName.specified=false");
    }

    @Test
    @Transactional
    void getAllCategoriesByCategoryNameContainsSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryName contains DEFAULT_CATEGORY_NAME
        defaultCategoryShouldBeFound("categoryName.contains=" + DEFAULT_CATEGORY_NAME);

        // Get all the categoryList where categoryName contains UPDATED_CATEGORY_NAME
        defaultCategoryShouldNotBeFound("categoryName.contains=" + UPDATED_CATEGORY_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByCategoryNameNotContainsSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where categoryName does not contain DEFAULT_CATEGORY_NAME
        defaultCategoryShouldNotBeFound("categoryName.doesNotContain=" + DEFAULT_CATEGORY_NAME);

        // Get all the categoryList where categoryName does not contain UPDATED_CATEGORY_NAME
        defaultCategoryShouldBeFound("categoryName.doesNotContain=" + UPDATED_CATEGORY_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdAt equals to DEFAULT_CREATED_AT
        defaultCategoryShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the categoryList where createdAt equals to UPDATED_CREATED_AT
        defaultCategoryShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdAt not equals to DEFAULT_CREATED_AT
        defaultCategoryShouldNotBeFound("createdAt.notEquals=" + DEFAULT_CREATED_AT);

        // Get all the categoryList where createdAt not equals to UPDATED_CREATED_AT
        defaultCategoryShouldBeFound("createdAt.notEquals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultCategoryShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the categoryList where createdAt equals to UPDATED_CREATED_AT
        defaultCategoryShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdAt is not null
        defaultCategoryShouldBeFound("createdAt.specified=true");

        // Get all the categoryList where createdAt is null
        defaultCategoryShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultCategoryShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the categoryList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultCategoryShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultCategoryShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the categoryList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultCategoryShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdAt is less than DEFAULT_CREATED_AT
        defaultCategoryShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the categoryList where createdAt is less than UPDATED_CREATED_AT
        defaultCategoryShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdAt is greater than DEFAULT_CREATED_AT
        defaultCategoryShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the categoryList where createdAt is greater than SMALLER_CREATED_AT
        defaultCategoryShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByModifiedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where modifiedAt equals to DEFAULT_MODIFIED_AT
        defaultCategoryShouldBeFound("modifiedAt.equals=" + DEFAULT_MODIFIED_AT);

        // Get all the categoryList where modifiedAt equals to UPDATED_MODIFIED_AT
        defaultCategoryShouldNotBeFound("modifiedAt.equals=" + UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByModifiedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where modifiedAt not equals to DEFAULT_MODIFIED_AT
        defaultCategoryShouldNotBeFound("modifiedAt.notEquals=" + DEFAULT_MODIFIED_AT);

        // Get all the categoryList where modifiedAt not equals to UPDATED_MODIFIED_AT
        defaultCategoryShouldBeFound("modifiedAt.notEquals=" + UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByModifiedAtIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where modifiedAt in DEFAULT_MODIFIED_AT or UPDATED_MODIFIED_AT
        defaultCategoryShouldBeFound("modifiedAt.in=" + DEFAULT_MODIFIED_AT + "," + UPDATED_MODIFIED_AT);

        // Get all the categoryList where modifiedAt equals to UPDATED_MODIFIED_AT
        defaultCategoryShouldNotBeFound("modifiedAt.in=" + UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByModifiedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where modifiedAt is not null
        defaultCategoryShouldBeFound("modifiedAt.specified=true");

        // Get all the categoryList where modifiedAt is null
        defaultCategoryShouldNotBeFound("modifiedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCategoriesByModifiedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where modifiedAt is greater than or equal to DEFAULT_MODIFIED_AT
        defaultCategoryShouldBeFound("modifiedAt.greaterThanOrEqual=" + DEFAULT_MODIFIED_AT);

        // Get all the categoryList where modifiedAt is greater than or equal to UPDATED_MODIFIED_AT
        defaultCategoryShouldNotBeFound("modifiedAt.greaterThanOrEqual=" + UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByModifiedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where modifiedAt is less than or equal to DEFAULT_MODIFIED_AT
        defaultCategoryShouldBeFound("modifiedAt.lessThanOrEqual=" + DEFAULT_MODIFIED_AT);

        // Get all the categoryList where modifiedAt is less than or equal to SMALLER_MODIFIED_AT
        defaultCategoryShouldNotBeFound("modifiedAt.lessThanOrEqual=" + SMALLER_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByModifiedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where modifiedAt is less than DEFAULT_MODIFIED_AT
        defaultCategoryShouldNotBeFound("modifiedAt.lessThan=" + DEFAULT_MODIFIED_AT);

        // Get all the categoryList where modifiedAt is less than UPDATED_MODIFIED_AT
        defaultCategoryShouldBeFound("modifiedAt.lessThan=" + UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByModifiedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where modifiedAt is greater than DEFAULT_MODIFIED_AT
        defaultCategoryShouldNotBeFound("modifiedAt.greaterThan=" + DEFAULT_MODIFIED_AT);

        // Get all the categoryList where modifiedAt is greater than SMALLER_MODIFIED_AT
        defaultCategoryShouldBeFound("modifiedAt.greaterThan=" + SMALLER_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllCategoriesByProductIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);
        Product product = ProductResourceIT.createEntity(em);
        em.persist(product);
        em.flush();
        category.addProduct(product);
        categoryRepository.saveAndFlush(category);
        Long productId = product.getIdProduct();

        // Get all the categoryList where product equals to productId
        defaultCategoryShouldBeFound("productId.equals=" + productId);

        // Get all the categoryList where product equals to (productId + 1)
        defaultCategoryShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCategoryShouldBeFound(String filter) throws Exception {
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=idCategory,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].idCategory").value(hasItem(category.getIdCategory().intValue())))
            .andExpect(jsonPath("$.[*].categoryName").value(hasItem(DEFAULT_CATEGORY_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].modifiedAt").value(hasItem(sameInstant(DEFAULT_MODIFIED_AT))));

        // Check, that the count call also returns 1
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=idCategory,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCategoryShouldNotBeFound(String filter) throws Exception {
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=idCategory,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=idCategory,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCategory() throws Exception {
        // Get the category
        restCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category
        Category updatedCategory = categoryRepository.findById(category.getIdCategory()).get();
        // Disconnect from session so that the updates on updatedCategory are not directly saved in db
        em.detach(updatedCategory);
        updatedCategory.categoryName(UPDATED_CATEGORY_NAME).createdAt(UPDATED_CREATED_AT).modifiedAt(UPDATED_MODIFIED_AT);
        CategoryDTO categoryDTO = categoryMapper.toDto(updatedCategory);

        restCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categoryDTO.getIdCategory())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getCategoryName()).isEqualTo(UPDATED_CATEGORY_NAME);
        assertThat(testCategory.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCategory.getModifiedAt()).isEqualTo(UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void putNonExistingCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setIdCategory(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categoryDTO.getIdCategory())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setIdCategory(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setIdCategory(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCategoryWithPatch() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category using partial update
        Category partialUpdatedCategory = new Category();
        partialUpdatedCategory.setIdCategory(category.getIdCategory());

        restCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategory.getIdCategory())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCategory))
            )
            .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getCategoryName()).isEqualTo(DEFAULT_CATEGORY_NAME);
        assertThat(testCategory.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCategory.getModifiedAt()).isEqualTo(DEFAULT_MODIFIED_AT);
    }

    @Test
    @Transactional
    void fullUpdateCategoryWithPatch() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category using partial update
        Category partialUpdatedCategory = new Category();
        partialUpdatedCategory.setIdCategory(category.getIdCategory());

        partialUpdatedCategory.categoryName(UPDATED_CATEGORY_NAME).createdAt(UPDATED_CREATED_AT).modifiedAt(UPDATED_MODIFIED_AT);

        restCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategory.getIdCategory())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCategory))
            )
            .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getCategoryName()).isEqualTo(UPDATED_CATEGORY_NAME);
        assertThat(testCategory.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCategory.getModifiedAt()).isEqualTo(UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setIdCategory(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, categoryDTO.getIdCategory())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setIdCategory(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setIdCategory(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeDelete = categoryRepository.findAll().size();

        // Delete the category
        restCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, category.getIdCategory()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
