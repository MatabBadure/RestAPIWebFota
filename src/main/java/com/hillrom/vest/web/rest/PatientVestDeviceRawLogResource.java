package com.hillrom.vest.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.repository.PatientVestDeviceRawLogRepository;
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
 * REST controller for managing PatientVestDeviceRawLogs.
 */
@RestController
@RequestMapping("/api")
public class PatientVestDeviceRawLogResource {

/*    private final Logger log = LoggerFactory.getLogger(PatientVestDeviceRawLogResource.class);

    @Inject
    private PatientVestDeviceRawLogRepository patientVestDeviceRawLogsRepository;

    *//**
     * POST  /patientVestDeviceRawLogs -> Create a new patientVestDeviceRawLogs.
     *//*
    @RequestMapping(value = "/patientVestDeviceRawLogs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody PatientVestDeviceRawLog patientVestDeviceRawLogs) throws URISyntaxException {
        log.debug("REST request to save PATIENT_VEST_DEVICE_RAW_LOGS : {}", patientVestDeviceRawLogs);
        if (patientVestDeviceRawLogs.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new pATIENT_VEST_DEVICE_RAW_LOGS cannot already have an ID").build();
        }
        patientVestDeviceRawLogsRepository.save(patientVestDeviceRawLogs);
        return ResponseEntity.created(new URI("/api/patientVestDeviceRawLogs/" + patientVestDeviceRawLogs.getId())).build();
    }

    *//**
     * PUT  /patientVestDeviceRawLogs -> Updates an existing PatientVestDeviceRawLogs.
     *//*
    @RequestMapping(value = "/patientVestDeviceRawLogs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody PatientVestDeviceRawLog patientVestDeviceRawLogs) throws URISyntaxException {
        log.debug("REST request to update PatientVestDeviceRawLogs : {}", patientVestDeviceRawLogs);
        if (patientVestDeviceRawLogs.getId() == null) {
            return create(patientVestDeviceRawLogs);
        }
        patientVestDeviceRawLogsRepository.save(patientVestDeviceRawLogs);
        return ResponseEntity.ok().build();
    }

    *//**
     * GET  /patientVestDeviceRawLogs -> get all the patientVestDeviceRawLogs.
     *//*
    @RequestMapping(value = "/patientVestDeviceRawLogs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PatientVestDeviceRawLog>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<PatientVestDeviceRawLog> page = patientVestDeviceRawLogsRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/patientVestDeviceRawLogs", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    *//**
     * GET  /patientVestDeviceRawLogs/:id -> get the "id" patientVestDeviceRawLogs.
     *//*
    @RequestMapping(value = "/patientVestDeviceRawLogs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PatientVestDeviceRawLog> get(@PathVariable Long id) {
        log.debug("REST request to get PatientVestDeviceRawLogs : {}", id);
        return Optional.ofNullable(patientVestDeviceRawLogsRepository.findOne(id))
            .map(pATIENT_VEST_DEVICE_RAW_LOGS -> new ResponseEntity<>(
                pATIENT_VEST_DEVICE_RAW_LOGS,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    *//**
     * DELETE  /patientVestDeviceRawLogs/:id -> delete the "id" patientVestDeviceRawLogs.
     *//*
    @RequestMapping(value = "/patientVestDeviceRawLogs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete PatientVestDeviceRawLogs : {}", id);
        patientVestDeviceRawLogsRepository.delete(id);
    }*/
}
