package com.localgrower.web.rest;

import com.localgrower.repository.AppUserRepository;
import com.localgrower.service.AppUserService;
import com.localgrower.service.dto.AppUserDTO;
import com.localgrower.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.localgrower.domain.AppUser}.
 */
@RestController
@RequestMapping("/api")
public class AppUserResource {

    private final Logger log = LoggerFactory.getLogger(AppUserResource.class);

    private static final String ENTITY_NAME = "appUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppUserService appUserService;

    private final AppUserRepository appUserRepository;

    public AppUserResource(AppUserService appUserService, AppUserRepository appUserRepository) {
        this.appUserService = appUserService;
        this.appUserRepository = appUserRepository;
    }

    /**
     * {@code POST  /app-users} : Create a new appUser.
     *
     * @param appUserDTO the appUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appUserDTO, or with status {@code 400 (Bad Request)} if the appUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/app-users")
    public ResponseEntity<AppUserDTO> createAppUser(@Valid @RequestBody AppUserDTO appUserDTO) throws URISyntaxException {
        log.debug("REST request to save AppUser : {}", appUserDTO);
        if (appUserDTO.getIdAppUser() != null) {
            throw new BadRequestAlertException("A new appUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AppUserDTO result = appUserService.save(appUserDTO);
        return ResponseEntity
            .created(new URI("/api/app-users/" + result.getIdAppUser()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getIdAppUser().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /app-users/:idAppUser} : Updates an existing appUser.
     *
     * @param idAppUser the id of the appUserDTO to save.
     * @param appUserDTO the appUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUserDTO,
     * or with status {@code 400 (Bad Request)} if the appUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/app-users/{idAppUser}")
    public ResponseEntity<AppUserDTO> updateAppUser(
        @PathVariable(value = "idAppUser", required = false) final Long idAppUser,
        @Valid @RequestBody AppUserDTO appUserDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AppUser : {}, {}", idAppUser, appUserDTO);
        if (appUserDTO.getIdAppUser() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idAppUser, appUserDTO.getIdAppUser())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appUserRepository.existsById(idAppUser)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AppUserDTO result = appUserService.save(appUserDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, appUserDTO.getIdAppUser().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /app-users/:idAppUser} : Partial updates given fields of an existing appUser, field will ignore if it is null
     *
     * @param idAppUser the id of the appUserDTO to save.
     * @param appUserDTO the appUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUserDTO,
     * or with status {@code 400 (Bad Request)} if the appUserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appUserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/app-users/{idAppUser}", consumes = "application/merge-patch+json")
    public ResponseEntity<AppUserDTO> partialUpdateAppUser(
        @PathVariable(value = "idAppUser", required = false) final Long idAppUser,
        @NotNull @RequestBody AppUserDTO appUserDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AppUser partially : {}, {}", idAppUser, appUserDTO);
        if (appUserDTO.getIdAppUser() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idAppUser, appUserDTO.getIdAppUser())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appUserRepository.existsById(idAppUser)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AppUserDTO> result = appUserService.partialUpdate(appUserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, appUserDTO.getIdAppUser().toString())
        );
    }

    /**
     * {@code GET  /app-users} : get all the appUsers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appUsers in body.
     */
    @GetMapping("/app-users")
    public ResponseEntity<List<AppUserDTO>> getAllAppUsers(Pageable pageable) {
        log.debug("REST request to get a page of AppUsers");
        Page<AppUserDTO> page = appUserService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /app-users/:id} : get the "id" appUser.
     *
     * @param id the id of the appUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/app-users/{id}")
    public ResponseEntity<AppUserDTO> getAppUser(@PathVariable Long id) {
        log.debug("REST request to get AppUser : {}", id);
        Optional<AppUserDTO> appUserDTO = appUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appUserDTO);
    }

    /**
     * {@code DELETE  /app-users/:id} : delete the "id" appUser.
     *
     * @param id the id of the appUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/app-users/{id}")
    public ResponseEntity<Void> deleteAppUser(@PathVariable Long id) {
        log.debug("REST request to delete AppUser : {}", id);
        appUserService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
