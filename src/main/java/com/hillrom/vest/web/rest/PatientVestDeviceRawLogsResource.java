package com.hillrom.vest.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.hillrom.vest.domain.PATIENT_VEST_DEVICE_RAW_LOGS;
import com.hillrom.vest.repository.PATIENT_VEST_DEVICE_RAW_LOGSRepository;
import com.hillrom.vest.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing PATIENT_VEST_DEVICE_RAW_LOGS.
 */
@RestController
@RequestMapping("/api")
public class PatientVestDeviceRawLogsResource {

    private final Logger log = LoggerFactory.getLogger(PatientVestDeviceRawLogsResource.class);

    @Inject
    private PATIENT_VEST_DEVICE_RAW_LOGSRepository pATIENT_VEST_DEVICE_RAW_LOGSRepository;

    /**
     * POST  /pATIENT_VEST_DEVICE_RAW_LOGSs -> Create a new pATIENT_VEST_DEVICE_RAW_LOGS.
     */
    @RequestMapping(value = "/pATIENT_VEST_DEVICE_RAW_LOGSs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody PATIENT_VEST_DEVICE_RAW_LOGS pATIENT_VEST_DEVICE_RAW_LOGS) throws URISyntaxException {
        log.debug("REST request to save PATIENT_VEST_DEVICE_RAW_LOGS : {}", pATIENT_VEST_DEVICE_RAW_LOGS);
        if (pATIENT_VEST_DEVICE_RAW_LOGS.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new pATIENT_VEST_DEVICE_RAW_LOGS cannot already have an ID").build();
        }
        pATIENT_VEST_DEVICE_RAW_LOGSRepository.save(pATIENT_VEST_DEVICE_RAW_LOGS);
        return ResponseEntity.created(new URI("/api/pATIENT_VEST_DEVICE_RAW_LOGSs/" + pATIENT_VEST_DEVICE_RAW_LOGS.getId())).build();
    }

    /**
     * PUT  /pATIENT_VEST_DEVICE_RAW_LOGSs -> Updates an existing pATIENT_VEST_DEVICE_RAW_LOGS.
     */
    @RequestMapping(value = "/pATIENT_VEST_DEVICE_RAW_LOGSs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody PATIENT_VEST_DEVICE_RAW_LOGS pATIENT_VEST_DEVICE_RAW_LOGS) throws URISyntaxException {
        log.debug("REST request to update PATIENT_VEST_DEVICE_RAW_LOGS : {}", pATIENT_VEST_DEVICE_RAW_LOGS);
        if (pATIENT_VEST_DEVICE_RAW_LOGS.getId() == null) {
            return create(pATIENT_VEST_DEVICE_RAW_LOGS);
        }
        pATIENT_VEST_DEVICE_RAW_LOGSRepository.save(pATIENT_VEST_DEVICE_RAW_LOGS);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /pATIENT_VEST_DEVICE_RAW_LOGSs -> get all the pATIENT_VEST_DEVICE_RAW_LOGSs.
     */
    @RequestMapping(value = "/pATIENT_VEST_DEVICE_RAW_LOGSs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PATIENT_VEST_DEVICE_RAW_LOGS>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<PATIENT_VEST_DEVICE_RAW_LOGS> page = pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pATIENT_VEST_DEVICE_RAW_LOGSs", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pATIENT_VEST_DEVICE_RAW_LOGSs/:id -> get the "id" pATIENT_VEST_DEVICE_RAW_LOGS.
     */
    @RequestMapping(value = "/pATIENT_VEST_DEVICE_RAW_LOGSs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PATIENT_VEST_DEVICE_RAW_LOGS> get(@PathVariable Long id) {
        log.debug("REST request to get PATIENT_VEST_DEVICE_RAW_LOGS : {}", id);
        return Optional.ofNullable(pATIENT_VEST_DEVICE_RAW_LOGSRepository.findOne(id))
            .map(pATIENT_VEST_DEVICE_RAW_LOGS -> new ResponseEntity<>(
                pATIENT_VEST_DEVICE_RAW_LOGS,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /pATIENT_VEST_DEVICE_RAW_LOGSs/:id -> delete the "id" pATIENT_VEST_DEVICE_RAW_LOGS.
     */
    @RequestMapping(value = "/pATIENT_VEST_DEVICE_RAW_LOGSs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete PATIENT_VEST_DEVICE_RAW_LOGS : {}", id);
        pATIENT_VEST_DEVICE_RAW_LOGSRepository.delete(id);
    }
}
