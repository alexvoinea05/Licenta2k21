package com.localgrower.web.rest;

import static com.localgrower.web.rest.TestUtil.sameInstant;
import static com.localgrower.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.localgrower.IntegrationTest;
import com.localgrower.domain.CartOrderDetails;
import com.localgrower.repository.CartOrderDetailsRepository;
import com.localgrower.service.dto.CartOrderDetailsDTO;
import com.localgrower.service.mapper.CartOrderDetailsMapper;
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
 * Integration tests for the {@link CartOrderDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CartOrderDetailsResourceIT {

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(0.1);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(1);

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_MODIFIED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_MODIFIED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_STATUS_COMMAND = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_COMMAND = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cart-order-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{idCartOrderDetails}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CartOrderDetailsRepository cartOrderDetailsRepository;

    @Autowired
    private CartOrderDetailsMapper cartOrderDetailsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartOrderDetailsMockMvc;

    private CartOrderDetails cartOrderDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartOrderDetails createEntity(EntityManager em) {
        CartOrderDetails cartOrderDetails = new CartOrderDetails()
            .totalPrice(DEFAULT_TOTAL_PRICE)
            .createdAt(DEFAULT_CREATED_AT)
            .modifiedAt(DEFAULT_MODIFIED_AT)
            .statusCommand(DEFAULT_STATUS_COMMAND);
        return cartOrderDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartOrderDetails createUpdatedEntity(EntityManager em) {
        CartOrderDetails cartOrderDetails = new CartOrderDetails()
            .totalPrice(UPDATED_TOTAL_PRICE)
            .createdAt(UPDATED_CREATED_AT)
            .modifiedAt(UPDATED_MODIFIED_AT)
            .statusCommand(UPDATED_STATUS_COMMAND);
        return cartOrderDetails;
    }

    @BeforeEach
    public void initTest() {
        cartOrderDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createCartOrderDetails() throws Exception {
        int databaseSizeBeforeCreate = cartOrderDetailsRepository.findAll().size();
        // Create the CartOrderDetails
        CartOrderDetailsDTO cartOrderDetailsDTO = cartOrderDetailsMapper.toDto(cartOrderDetails);
        restCartOrderDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartOrderDetailsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        CartOrderDetails testCartOrderDetails = cartOrderDetailsList.get(cartOrderDetailsList.size() - 1);
        assertThat(testCartOrderDetails.getTotalPrice()).isEqualByComparingTo(DEFAULT_TOTAL_PRICE);
        assertThat(testCartOrderDetails.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCartOrderDetails.getModifiedAt()).isEqualTo(DEFAULT_MODIFIED_AT);
        assertThat(testCartOrderDetails.getStatusCommand()).isEqualTo(DEFAULT_STATUS_COMMAND);
    }

    @Test
    @Transactional
    void createCartOrderDetailsWithExistingId() throws Exception {
        // Create the CartOrderDetails with an existing ID
        cartOrderDetails.setIdCartOrderDetails(1L);
        CartOrderDetailsDTO cartOrderDetailsDTO = cartOrderDetailsMapper.toDto(cartOrderDetails);

        int databaseSizeBeforeCreate = cartOrderDetailsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartOrderDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartOrderDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCartOrderDetails() throws Exception {
        // Initialize the database
        cartOrderDetailsRepository.saveAndFlush(cartOrderDetails);

        // Get all the cartOrderDetailsList
        restCartOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=idCartOrderDetails,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].idCartOrderDetails").value(hasItem(cartOrderDetails.getIdCartOrderDetails().intValue())))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].modifiedAt").value(hasItem(sameInstant(DEFAULT_MODIFIED_AT))))
            .andExpect(jsonPath("$.[*].statusCommand").value(hasItem(DEFAULT_STATUS_COMMAND)));
    }

    @Test
    @Transactional
    void getCartOrderDetails() throws Exception {
        // Initialize the database
        cartOrderDetailsRepository.saveAndFlush(cartOrderDetails);

        // Get the cartOrderDetails
        restCartOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, cartOrderDetails.getIdCartOrderDetails()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.idCartOrderDetails").value(cartOrderDetails.getIdCartOrderDetails().intValue()))
            .andExpect(jsonPath("$.totalPrice").value(sameNumber(DEFAULT_TOTAL_PRICE)))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.modifiedAt").value(sameInstant(DEFAULT_MODIFIED_AT)))
            .andExpect(jsonPath("$.statusCommand").value(DEFAULT_STATUS_COMMAND));
    }

    @Test
    @Transactional
    void getNonExistingCartOrderDetails() throws Exception {
        // Get the cartOrderDetails
        restCartOrderDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCartOrderDetails() throws Exception {
        // Initialize the database
        cartOrderDetailsRepository.saveAndFlush(cartOrderDetails);

        int databaseSizeBeforeUpdate = cartOrderDetailsRepository.findAll().size();

        // Update the cartOrderDetails
        CartOrderDetails updatedCartOrderDetails = cartOrderDetailsRepository.findById(cartOrderDetails.getIdCartOrderDetails()).get();
        // Disconnect from session so that the updates on updatedCartOrderDetails are not directly saved in db
        em.detach(updatedCartOrderDetails);
        updatedCartOrderDetails
            .totalPrice(UPDATED_TOTAL_PRICE)
            .createdAt(UPDATED_CREATED_AT)
            .modifiedAt(UPDATED_MODIFIED_AT)
            .statusCommand(UPDATED_STATUS_COMMAND);
        CartOrderDetailsDTO cartOrderDetailsDTO = cartOrderDetailsMapper.toDto(updatedCartOrderDetails);

        restCartOrderDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartOrderDetailsDTO.getIdCartOrderDetails())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartOrderDetailsDTO))
            )
            .andExpect(status().isOk());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeUpdate);
        CartOrderDetails testCartOrderDetails = cartOrderDetailsList.get(cartOrderDetailsList.size() - 1);
        assertThat(testCartOrderDetails.getTotalPrice()).isEqualTo(UPDATED_TOTAL_PRICE);
        assertThat(testCartOrderDetails.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCartOrderDetails.getModifiedAt()).isEqualTo(UPDATED_MODIFIED_AT);
        assertThat(testCartOrderDetails.getStatusCommand()).isEqualTo(UPDATED_STATUS_COMMAND);
    }

    @Test
    @Transactional
    void putNonExistingCartOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = cartOrderDetailsRepository.findAll().size();
        cartOrderDetails.setIdCartOrderDetails(count.incrementAndGet());

        // Create the CartOrderDetails
        CartOrderDetailsDTO cartOrderDetailsDTO = cartOrderDetailsMapper.toDto(cartOrderDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartOrderDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartOrderDetailsDTO.getIdCartOrderDetails())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartOrderDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCartOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = cartOrderDetailsRepository.findAll().size();
        cartOrderDetails.setIdCartOrderDetails(count.incrementAndGet());

        // Create the CartOrderDetails
        CartOrderDetailsDTO cartOrderDetailsDTO = cartOrderDetailsMapper.toDto(cartOrderDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartOrderDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartOrderDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCartOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = cartOrderDetailsRepository.findAll().size();
        cartOrderDetails.setIdCartOrderDetails(count.incrementAndGet());

        // Create the CartOrderDetails
        CartOrderDetailsDTO cartOrderDetailsDTO = cartOrderDetailsMapper.toDto(cartOrderDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartOrderDetailsMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartOrderDetailsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCartOrderDetailsWithPatch() throws Exception {
        // Initialize the database
        cartOrderDetailsRepository.saveAndFlush(cartOrderDetails);

        int databaseSizeBeforeUpdate = cartOrderDetailsRepository.findAll().size();

        // Update the cartOrderDetails using partial update
        CartOrderDetails partialUpdatedCartOrderDetails = new CartOrderDetails();
        partialUpdatedCartOrderDetails.setIdCartOrderDetails(cartOrderDetails.getIdCartOrderDetails());

        partialUpdatedCartOrderDetails.modifiedAt(UPDATED_MODIFIED_AT);

        restCartOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartOrderDetails.getIdCartOrderDetails())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCartOrderDetails))
            )
            .andExpect(status().isOk());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeUpdate);
        CartOrderDetails testCartOrderDetails = cartOrderDetailsList.get(cartOrderDetailsList.size() - 1);
        assertThat(testCartOrderDetails.getTotalPrice()).isEqualByComparingTo(DEFAULT_TOTAL_PRICE);
        assertThat(testCartOrderDetails.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCartOrderDetails.getModifiedAt()).isEqualTo(UPDATED_MODIFIED_AT);
        assertThat(testCartOrderDetails.getStatusCommand()).isEqualTo(DEFAULT_STATUS_COMMAND);
    }

    @Test
    @Transactional
    void fullUpdateCartOrderDetailsWithPatch() throws Exception {
        // Initialize the database
        cartOrderDetailsRepository.saveAndFlush(cartOrderDetails);

        int databaseSizeBeforeUpdate = cartOrderDetailsRepository.findAll().size();

        // Update the cartOrderDetails using partial update
        CartOrderDetails partialUpdatedCartOrderDetails = new CartOrderDetails();
        partialUpdatedCartOrderDetails.setIdCartOrderDetails(cartOrderDetails.getIdCartOrderDetails());

        partialUpdatedCartOrderDetails
            .totalPrice(UPDATED_TOTAL_PRICE)
            .createdAt(UPDATED_CREATED_AT)
            .modifiedAt(UPDATED_MODIFIED_AT)
            .statusCommand(UPDATED_STATUS_COMMAND);

        restCartOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartOrderDetails.getIdCartOrderDetails())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCartOrderDetails))
            )
            .andExpect(status().isOk());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeUpdate);
        CartOrderDetails testCartOrderDetails = cartOrderDetailsList.get(cartOrderDetailsList.size() - 1);
        assertThat(testCartOrderDetails.getTotalPrice()).isEqualByComparingTo(UPDATED_TOTAL_PRICE);
        assertThat(testCartOrderDetails.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCartOrderDetails.getModifiedAt()).isEqualTo(UPDATED_MODIFIED_AT);
        assertThat(testCartOrderDetails.getStatusCommand()).isEqualTo(UPDATED_STATUS_COMMAND);
    }

    @Test
    @Transactional
    void patchNonExistingCartOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = cartOrderDetailsRepository.findAll().size();
        cartOrderDetails.setIdCartOrderDetails(count.incrementAndGet());

        // Create the CartOrderDetails
        CartOrderDetailsDTO cartOrderDetailsDTO = cartOrderDetailsMapper.toDto(cartOrderDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cartOrderDetailsDTO.getIdCartOrderDetails())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cartOrderDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCartOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = cartOrderDetailsRepository.findAll().size();
        cartOrderDetails.setIdCartOrderDetails(count.incrementAndGet());

        // Create the CartOrderDetails
        CartOrderDetailsDTO cartOrderDetailsDTO = cartOrderDetailsMapper.toDto(cartOrderDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cartOrderDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCartOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = cartOrderDetailsRepository.findAll().size();
        cartOrderDetails.setIdCartOrderDetails(count.incrementAndGet());

        // Create the CartOrderDetails
        CartOrderDetailsDTO cartOrderDetailsDTO = cartOrderDetailsMapper.toDto(cartOrderDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cartOrderDetailsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartOrderDetails in the database
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCartOrderDetails() throws Exception {
        // Initialize the database
        cartOrderDetailsRepository.saveAndFlush(cartOrderDetails);

        int databaseSizeBeforeDelete = cartOrderDetailsRepository.findAll().size();

        // Delete the cartOrderDetails
        restCartOrderDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, cartOrderDetails.getIdCartOrderDetails()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        assertThat(cartOrderDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
