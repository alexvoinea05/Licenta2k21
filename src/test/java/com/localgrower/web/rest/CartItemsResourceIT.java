package com.localgrower.web.rest;

import static com.localgrower.web.rest.TestUtil.sameInstant;
import static com.localgrower.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.localgrower.IntegrationTest;
import com.localgrower.domain.CartItems;
import com.localgrower.repository.CartItemsRepository;
import com.localgrower.service.dto.CartItemsDTO;
import com.localgrower.service.mapper.CartItemsMapper;
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
 * Integration tests for the {@link CartItemsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CartItemsResourceIT {

    private static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(0.1);
    private static final BigDecimal UPDATED_QUANTITY = new BigDecimal(1);

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_MODIFIED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_MODIFIED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/cart-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{idCartItems}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private CartItemsMapper cartItemsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartItemsMockMvc;

    private CartItems cartItems;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItems createEntity(EntityManager em) {
        CartItems cartItems = new CartItems().quantity(DEFAULT_QUANTITY).createdAt(DEFAULT_CREATED_AT).modifiedAt(DEFAULT_MODIFIED_AT);
        return cartItems;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItems createUpdatedEntity(EntityManager em) {
        CartItems cartItems = new CartItems().quantity(UPDATED_QUANTITY).createdAt(UPDATED_CREATED_AT).modifiedAt(UPDATED_MODIFIED_AT);
        return cartItems;
    }

    @BeforeEach
    public void initTest() {
        cartItems = createEntity(em);
    }

    @Test
    @Transactional
    void createCartItems() throws Exception {
        int databaseSizeBeforeCreate = cartItemsRepository.findAll().size();
        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);
        restCartItemsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartItemsDTO)))
            .andExpect(status().isCreated());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeCreate + 1);
        CartItems testCartItems = cartItemsList.get(cartItemsList.size() - 1);
        assertThat(testCartItems.getQuantity()).isEqualByComparingTo(DEFAULT_QUANTITY);
        assertThat(testCartItems.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCartItems.getModifiedAt()).isEqualTo(DEFAULT_MODIFIED_AT);
    }

    @Test
    @Transactional
    void createCartItemsWithExistingId() throws Exception {
        // Create the CartItems with an existing ID
        cartItems.setIdCartItems(1L);
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        int databaseSizeBeforeCreate = cartItemsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartItemsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartItemsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = cartItemsRepository.findAll().size();
        // set the field null
        cartItems.setQuantity(null);

        // Create the CartItems, which fails.
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        restCartItemsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartItemsDTO)))
            .andExpect(status().isBadRequest());

        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        // Get all the cartItemsList
        restCartItemsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=idCartItems,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].idCartItems").value(hasItem(cartItems.getIdCartItems().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].modifiedAt").value(hasItem(sameInstant(DEFAULT_MODIFIED_AT))));
    }

    @Test
    @Transactional
    void getCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        // Get the cartItems
        restCartItemsMockMvc
            .perform(get(ENTITY_API_URL_ID, cartItems.getIdCartItems()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.idCartItems").value(cartItems.getIdCartItems().intValue()))
            .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.modifiedAt").value(sameInstant(DEFAULT_MODIFIED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingCartItems() throws Exception {
        // Get the cartItems
        restCartItemsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();

        // Update the cartItems
        CartItems updatedCartItems = cartItemsRepository.findById(cartItems.getIdCartItems()).get();
        // Disconnect from session so that the updates on updatedCartItems are not directly saved in db
        em.detach(updatedCartItems);
        updatedCartItems.quantity(UPDATED_QUANTITY).createdAt(UPDATED_CREATED_AT).modifiedAt(UPDATED_MODIFIED_AT);
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(updatedCartItems);

        restCartItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItemsDTO.getIdCartItems())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isOk());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
        CartItems testCartItems = cartItemsList.get(cartItemsList.size() - 1);
        assertThat(testCartItems.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testCartItems.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCartItems.getModifiedAt()).isEqualTo(UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void putNonExistingCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setIdCartItems(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItemsDTO.getIdCartItems())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setIdCartItems(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setIdCartItems(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartItemsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCartItemsWithPatch() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();

        // Update the cartItems using partial update
        CartItems partialUpdatedCartItems = new CartItems();
        partialUpdatedCartItems.setIdCartItems(cartItems.getIdCartItems());

        partialUpdatedCartItems.quantity(UPDATED_QUANTITY).createdAt(UPDATED_CREATED_AT).modifiedAt(UPDATED_MODIFIED_AT);

        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItems.getIdCartItems())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCartItems))
            )
            .andExpect(status().isOk());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
        CartItems testCartItems = cartItemsList.get(cartItemsList.size() - 1);
        assertThat(testCartItems.getQuantity()).isEqualByComparingTo(UPDATED_QUANTITY);
        assertThat(testCartItems.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCartItems.getModifiedAt()).isEqualTo(UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void fullUpdateCartItemsWithPatch() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();

        // Update the cartItems using partial update
        CartItems partialUpdatedCartItems = new CartItems();
        partialUpdatedCartItems.setIdCartItems(cartItems.getIdCartItems());

        partialUpdatedCartItems.quantity(UPDATED_QUANTITY).createdAt(UPDATED_CREATED_AT).modifiedAt(UPDATED_MODIFIED_AT);

        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItems.getIdCartItems())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCartItems))
            )
            .andExpect(status().isOk());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
        CartItems testCartItems = cartItemsList.get(cartItemsList.size() - 1);
        assertThat(testCartItems.getQuantity()).isEqualByComparingTo(UPDATED_QUANTITY);
        assertThat(testCartItems.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCartItems.getModifiedAt()).isEqualTo(UPDATED_MODIFIED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setIdCartItems(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cartItemsDTO.getIdCartItems())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setIdCartItems(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setIdCartItems(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        int databaseSizeBeforeDelete = cartItemsRepository.findAll().size();

        // Delete the cartItems
        restCartItemsMockMvc
            .perform(delete(ENTITY_API_URL_ID, cartItems.getIdCartItems()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
