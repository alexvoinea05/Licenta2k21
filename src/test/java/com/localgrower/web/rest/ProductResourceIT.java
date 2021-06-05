package com.localgrower.web.rest;

import static com.localgrower.web.rest.TestUtil.sameInstant;
import static com.localgrower.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.localgrower.IntegrationTest;
import com.localgrower.domain.AppUser;
import com.localgrower.domain.CartItems;
import com.localgrower.domain.Category;
import com.localgrower.domain.Product;
import com.localgrower.repository.ProductRepository;
import com.localgrower.service.criteria.ProductCriteria;
import com.localgrower.service.dto.ProductDTO;
import com.localgrower.service.mapper.ProductMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0.1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(0.1 - 1);

    private static final BigDecimal DEFAULT_STOCK = new BigDecimal(0.1);
    private static final BigDecimal UPDATED_STOCK = new BigDecimal(1);
    private static final BigDecimal SMALLER_STOCK = new BigDecimal(0.1 - 1);

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_PRODUCT_URL = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_URL = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_MODIFIED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_MODIFIED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_MODIFIED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{idProduct}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .stock(DEFAULT_STOCK)
            .imageUrl(DEFAULT_IMAGE_URL)
            .productUrl(DEFAULT_PRODUCT_URL)
            .createdAt(DEFAULT_CREATED_AT)
            .modifiedAt(DEFAULT_MODIFIED_AT);
        return product;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .stock(UPDATED_STOCK)
            .imageUrl(UPDATED_IMAGE_URL)
            .productUrl(UPDATED_PRODUCT_URL)
            .createdAt(UPDATED_CREATED_AT)
            .modifiedAt(UPDATED_MODIFIED_AT);
        return product;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();
        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testProduct.getStock()).isEqualByComparingTo(DEFAULT_STOCK);
        assertThat(testProduct.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testProduct.getProductUrl()).isEqualTo(DEFAULT_PRODUCT_URL);
        assertThat(testProduct.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testProduct.getModifiedAt()).isEqualTo(DEFAULT_MODIFIED_AT);
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setIdProduct(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setName(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setPrice(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStockIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setStock(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=idProduct,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].idProduct").value(hasItem(product.getIdProduct().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(sameNumber(DEFAULT_STOCK))))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].productUrl").value(hasItem(DEFAULT_PRODUCT_URL)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].modifiedAt").value(hasItem(sameInstant(DEFAULT_MODIFIED_AT))));
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getIdProduct()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.idProduct").value(product.getIdProduct().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.stock").value(sameNumber(DEFAULT_STOCK)))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.productUrl").value(DEFAULT_PRODUCT_URL))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.modifiedAt").value(sameInstant(DEFAULT_MODIFIED_AT)));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        Long id = product.getIdProduct();

        defaultProductShouldBeFound("idProduct.equals=" + id);
        defaultProductShouldNotBeFound("idProduct.notEquals=" + id);

        defaultProductShouldBeFound("idProduct.greaterThanOrEqual=" + id);
        defaultProductShouldNotBeFound("idProduct.greaterThan=" + id);

        defaultProductShouldBeFound("idProduct.lessThanOrEqual=" + id);
        defaultProductShouldNotBeFound("idProduct.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name equals to DEFAULT_NAME
        defaultProductShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name not equals to DEFAULT_NAME
        defaultProductShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the productList where name not equals to UPDATED_NAME
        defaultProductShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProductShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name is not null
        defaultProductShouldBeFound("name.specified=true");

        // Get all the productList where name is null
        defaultProductShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNameContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name contains DEFAULT_NAME
        defaultProductShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the productList where name contains UPDATED_NAME
        defaultProductShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name does not contain DEFAULT_NAME
        defaultProductShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the productList where name does not contain UPDATED_NAME
        defaultProductShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price equals to DEFAULT_PRICE
        defaultProductShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the productList where price equals to UPDATED_PRICE
        defaultProductShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price not equals to DEFAULT_PRICE
        defaultProductShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);

        // Get all the productList where price not equals to UPDATED_PRICE
        defaultProductShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultProductShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the productList where price equals to UPDATED_PRICE
        defaultProductShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is not null
        defaultProductShouldBeFound("price.specified=true");

        // Get all the productList where price is null
        defaultProductShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is greater than or equal to DEFAULT_PRICE
        defaultProductShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the productList where price is greater than or equal to UPDATED_PRICE
        defaultProductShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is less than or equal to DEFAULT_PRICE
        defaultProductShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the productList where price is less than or equal to SMALLER_PRICE
        defaultProductShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is less than DEFAULT_PRICE
        defaultProductShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the productList where price is less than UPDATED_PRICE
        defaultProductShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is greater than DEFAULT_PRICE
        defaultProductShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the productList where price is greater than SMALLER_PRICE
        defaultProductShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByStockIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where stock equals to DEFAULT_STOCK
        defaultProductShouldBeFound("stock.equals=" + DEFAULT_STOCK);

        // Get all the productList where stock equals to UPDATED_STOCK
        defaultProductShouldNotBeFound("stock.equals=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByStockIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where stock not equals to DEFAULT_STOCK
        defaultProductShouldNotBeFound("stock.notEquals=" + DEFAULT_STOCK);

        // Get all the productList where stock not equals to UPDATED_STOCK
        defaultProductShouldBeFound("stock.notEquals=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByStockIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where stock in DEFAULT_STOCK or UPDATED_STOCK
        defaultProductShouldBeFound("stock.in=" + DEFAULT_STOCK + "," + UPDATED_STOCK);

        // Get all the productList where stock equals to UPDATED_STOCK
        defaultProductShouldNotBeFound("stock.in=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByStockIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where stock is not null
        defaultProductShouldBeFound("stock.specified=true");

        // Get all the productList where stock is null
        defaultProductShouldNotBeFound("stock.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByStockIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where stock is greater than or equal to DEFAULT_STOCK
        defaultProductShouldBeFound("stock.greaterThanOrEqual=" + DEFAULT_STOCK);

        // Get all the productList where stock is greater than or equal to UPDATED_STOCK
        defaultProductShouldNotBeFound("stock.greaterThanOrEqual=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByStockIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where stock is less than or equal to DEFAULT_STOCK
        defaultProductShouldBeFound("stock.lessThanOrEqual=" + DEFAULT_STOCK);

        // Get all the productList where stock is less than or equal to SMALLER_STOCK
        defaultProductShouldNotBeFound("stock.lessThanOrEqual=" + SMALLER_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByStockIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where stock is less than DEFAULT_STOCK
        defaultProductShouldNotBeFound("stock.lessThan=" + DEFAULT_STOCK);

        // Get all the productList where stock is less than UPDATED_STOCK
        defaultProductShouldBeFound("stock.lessThan=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByStockIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where stock is greater than DEFAULT_STOCK
        defaultProductShouldNotBeFound("stock.greaterThan=" + DEFAULT_STOCK);

        // Get all the productList where stock is greater than SMALLER_STOCK
        defaultProductShouldBeFound("stock.greaterThan=" + SMALLER_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrl equals to DEFAULT_IMAGE_URL
        defaultProductShouldBeFound("imageUrl.equals=" + DEFAULT_IMAGE_URL);

        // Get all the productList where imageUrl equals to UPDATED_IMAGE_URL
        defaultProductShouldNotBeFound("imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrl not equals to DEFAULT_IMAGE_URL
        defaultProductShouldNotBeFound("imageUrl.notEquals=" + DEFAULT_IMAGE_URL);

        // Get all the productList where imageUrl not equals to UPDATED_IMAGE_URL
        defaultProductShouldBeFound("imageUrl.notEquals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrl in DEFAULT_IMAGE_URL or UPDATED_IMAGE_URL
        defaultProductShouldBeFound("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL);

        // Get all the productList where imageUrl equals to UPDATED_IMAGE_URL
        defaultProductShouldNotBeFound("imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrl is not null
        defaultProductShouldBeFound("imageUrl.specified=true");

        // Get all the productList where imageUrl is null
        defaultProductShouldNotBeFound("imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrl contains DEFAULT_IMAGE_URL
        defaultProductShouldBeFound("imageUrl.contains=" + DEFAULT_IMAGE_URL);

        // Get all the productList where imageUrl contains UPDATED_IMAGE_URL
        defaultProductShouldNotBeFound("imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrl does not contain DEFAULT_IMAGE_URL
        defaultProductShouldNotBeFound("imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);

        // Get all the productList where imageUrl does not contain UPDATED_IMAGE_URL
        defaultProductShouldBeFound("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllProductsByProductUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUrl equals to DEFAULT_PRODUCT_URL
        defaultProductShouldBeFound("productUrl.equals=" + DEFAULT_PRODUCT_URL);

        // Get all the productList where productUrl equals to UPDATED_PRODUCT_URL
        defaultProductShouldNotBeFound("productUrl.equals=" + UPDATED_PRODUCT_URL);
    }

    @Test
    @Transactional
    void getAllProductsByProductUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUrl not equals to DEFAULT_PRODUCT_URL
        defaultProductShouldNotBeFound("productUrl.notEquals=" + DEFAULT_PRODUCT_URL);

        // Get all the productList where productUrl not equals to UPDATED_PRODUCT_URL
        defaultProductShouldBeFound("productUrl.notEquals=" + UPDATED_PRODUCT_URL);
    }

    @Test
    @Transactional
    void getAllProductsByProductUrlIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUrl in DEFAULT_PRODUCT_URL or UPDATED_PRODUCT_URL
        defaultProductShouldBeFound("productUrl.in=" + DEFAULT_PRODUCT_URL + "," + UPDATED_PRODUCT_URL);

        // Get all the productList where productUrl equals to UPDATED_PRODUCT_URL
        defaultProductShouldNotBeFound("productUrl.in=" + UPDATED_PRODUCT_URL);
    }

    @Test
    @Transactional
    void getAllProductsByProductUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUrl is not null
        defaultProductShouldBeFound("productUrl.specified=true");

        // Get all the productList where productUrl is null
        defaultProductShouldNotBeFound("productUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByProductUrlContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUrl contains DEFAULT_PRODUCT_URL
        defaultProductShouldBeFound("productUrl.contains=" + DEFAULT_PRODUCT_URL);

        // Get all the productList where productUrl contains UPDATED_PRODUCT_URL
        defaultProductShouldNotBeFound("productUrl.contains=" + UPDATED_PRODUCT_URL);
    }

    @Test
    @Transactional
    void getAllProductsByProductUrlNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where productUrl does not contain DEFAULT_PRODUCT_URL
        defaultProductShouldNotBeFound("productUrl.doesNotContain=" + DEFAULT_PRODUCT_URL);

        // Get all the productList where productUrl does not contain UPDATED_PRODUCT_URL
        defaultProductShouldBeFound("productUrl.doesNotContain=" + UPDATED_PRODUCT_URL);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdAt equals to DEFAULT_CREATED_AT
        defaultProductShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the productList where createdAt equals to UPDATED_CREATED_AT
        defaultProductShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdAt not equals to DEFAULT_CREATED_AT
        defaultProductShouldNotBeFound("createdAt.notEquals=" + DEFAULT_CREATED_AT);

        // Get all the productList where createdAt not equals to UPDATED_CREATED_AT
        defaultProductShouldBeFound("createdAt.notEquals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultProductShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the productList where createdAt equals to UPDATED_CREATED_AT
        defaultProductShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdAt is not null
        defaultProductShouldBeFound("createdAt.specified=true");

        // Get all the productList where createdAt is null
        defaultProductShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultProductShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the productList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultProductShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultProductShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the productList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultProductShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdAt is less than DEFAULT_CREATED_AT
        defaultProductShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the productList where createdAt is less than UPDATED_CREATED_AT
        defaultProductShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdAt is greater than DEFAULT_CREATED_AT
        defaultProductShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the productList where createdAt is greater than SMALLER_CREATED_AT
        defaultProductShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByModifiedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where modifiedAt equals to DEFAULT_MODIFIED_AT
        defaultProductShouldBeFound("modifiedAt.equals=" + DEFAULT_MODIFIED_AT);

        // Get all the productList where modifiedAt equals to UPDATED_MODIFIED_AT
        defaultProductShouldNotBeFound("modifiedAt.equals=" + UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByModifiedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where modifiedAt not equals to DEFAULT_MODIFIED_AT
        defaultProductShouldNotBeFound("modifiedAt.notEquals=" + DEFAULT_MODIFIED_AT);

        // Get all the productList where modifiedAt not equals to UPDATED_MODIFIED_AT
        defaultProductShouldBeFound("modifiedAt.notEquals=" + UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByModifiedAtIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where modifiedAt in DEFAULT_MODIFIED_AT or UPDATED_MODIFIED_AT
        defaultProductShouldBeFound("modifiedAt.in=" + DEFAULT_MODIFIED_AT + "," + UPDATED_MODIFIED_AT);

        // Get all the productList where modifiedAt equals to UPDATED_MODIFIED_AT
        defaultProductShouldNotBeFound("modifiedAt.in=" + UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByModifiedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where modifiedAt is not null
        defaultProductShouldBeFound("modifiedAt.specified=true");

        // Get all the productList where modifiedAt is null
        defaultProductShouldNotBeFound("modifiedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByModifiedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where modifiedAt is greater than or equal to DEFAULT_MODIFIED_AT
        defaultProductShouldBeFound("modifiedAt.greaterThanOrEqual=" + DEFAULT_MODIFIED_AT);

        // Get all the productList where modifiedAt is greater than or equal to UPDATED_MODIFIED_AT
        defaultProductShouldNotBeFound("modifiedAt.greaterThanOrEqual=" + UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByModifiedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where modifiedAt is less than or equal to DEFAULT_MODIFIED_AT
        defaultProductShouldBeFound("modifiedAt.lessThanOrEqual=" + DEFAULT_MODIFIED_AT);

        // Get all the productList where modifiedAt is less than or equal to SMALLER_MODIFIED_AT
        defaultProductShouldNotBeFound("modifiedAt.lessThanOrEqual=" + SMALLER_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByModifiedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where modifiedAt is less than DEFAULT_MODIFIED_AT
        defaultProductShouldNotBeFound("modifiedAt.lessThan=" + DEFAULT_MODIFIED_AT);

        // Get all the productList where modifiedAt is less than UPDATED_MODIFIED_AT
        defaultProductShouldBeFound("modifiedAt.lessThan=" + UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByModifiedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where modifiedAt is greater than DEFAULT_MODIFIED_AT
        defaultProductShouldNotBeFound("modifiedAt.greaterThan=" + DEFAULT_MODIFIED_AT);

        // Get all the productList where modifiedAt is greater than SMALLER_MODIFIED_AT
        defaultProductShouldBeFound("modifiedAt.greaterThan=" + SMALLER_MODIFIED_AT);
    }

    @Test
    @Transactional
    void getAllProductsByCartItemsIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        CartItems cartItems = CartItemsResourceIT.createEntity(em);
        em.persist(cartItems);
        em.flush();
        product.addCartItems(cartItems);
        productRepository.saveAndFlush(product);
        Long cartItemsId = cartItems.getIdCartItems();

        // Get all the productList where cartItems equals to cartItemsId
        defaultProductShouldBeFound("cartItemsId.equals=" + cartItemsId);

        // Get all the productList where cartItems equals to (cartItemsId + 1)
        defaultProductShouldNotBeFound("cartItemsId.equals=" + (cartItemsId + 1));
    }

    @Test
    @Transactional
    void getAllProductsByIdCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        Category idCategory = CategoryResourceIT.createEntity(em);
        em.persist(idCategory);
        em.flush();
        product.setIdCategory(idCategory);
        productRepository.saveAndFlush(product);
        Long idCategoryId = idCategory.getIdCategory();

        // Get all the productList where idCategory equals to idCategoryId
        defaultProductShouldBeFound("idCategoryId.equals=" + idCategoryId);

        // Get all the productList where idCategory equals to (idCategoryId + 1)
        defaultProductShouldNotBeFound("idCategoryId.equals=" + (idCategoryId + 1));
    }

    @Test
    @Transactional
    void getAllProductsByIdGrowerIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        AppUser idGrower = AppUserResourceIT.createEntity(em);
        em.persist(idGrower);
        em.flush();
        product.setIdGrower(idGrower);
        productRepository.saveAndFlush(product);
        Long idGrowerId = idGrower.getIdAppUser();

        // Get all the productList where idGrower equals to idGrowerId
        defaultProductShouldBeFound("idGrowerId.equals=" + idGrowerId);

        // Get all the productList where idGrower equals to (idGrowerId + 1)
        defaultProductShouldNotBeFound("idGrowerId.equals=" + (idGrowerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=idProduct,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].idProduct").value(hasItem(product.getIdProduct().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(sameNumber(DEFAULT_STOCK))))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].productUrl").value(hasItem(DEFAULT_PRODUCT_URL)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].modifiedAt").value(hasItem(sameInstant(DEFAULT_MODIFIED_AT))));

        // Check, that the count call also returns 1
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=idProduct,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=idProduct,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=idProduct,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getIdProduct()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .stock(UPDATED_STOCK)
            .imageUrl(UPDATED_IMAGE_URL)
            .productUrl(UPDATED_PRODUCT_URL)
            .createdAt(UPDATED_CREATED_AT)
            .modifiedAt(UPDATED_MODIFIED_AT);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getIdProduct())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProduct.getStock()).isEqualTo(UPDATED_STOCK);
        assertThat(testProduct.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testProduct.getProductUrl()).isEqualTo(UPDATED_PRODUCT_URL);
        assertThat(testProduct.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testProduct.getModifiedAt()).isEqualTo(UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setIdProduct(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getIdProduct())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setIdProduct(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setIdProduct(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setIdProduct(product.getIdProduct());

        partialUpdatedProduct.stock(UPDATED_STOCK).productUrl(UPDATED_PRODUCT_URL).createdAt(UPDATED_CREATED_AT);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getIdProduct())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testProduct.getStock()).isEqualByComparingTo(UPDATED_STOCK);
        assertThat(testProduct.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testProduct.getProductUrl()).isEqualTo(UPDATED_PRODUCT_URL);
        assertThat(testProduct.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testProduct.getModifiedAt()).isEqualTo(DEFAULT_MODIFIED_AT);
    }

    @Test
    @Transactional
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setIdProduct(product.getIdProduct());

        partialUpdatedProduct
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .stock(UPDATED_STOCK)
            .imageUrl(UPDATED_IMAGE_URL)
            .productUrl(UPDATED_PRODUCT_URL)
            .createdAt(UPDATED_CREATED_AT)
            .modifiedAt(UPDATED_MODIFIED_AT);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getIdProduct())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testProduct.getStock()).isEqualByComparingTo(UPDATED_STOCK);
        assertThat(testProduct.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testProduct.getProductUrl()).isEqualTo(UPDATED_PRODUCT_URL);
        assertThat(testProduct.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testProduct.getModifiedAt()).isEqualTo(UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setIdProduct(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDTO.getIdProduct())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setIdProduct(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setIdProduct(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Delete the product
        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getIdProduct()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
