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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing PatientVestDeviceRawLog.
 */
@RestController
@RequestMapping("/api")
public class PatientVestDeviceRawLogResource {

    private final Logger log = LoggerFactory.getLogger(PatientVestDeviceRawLogResource.class);

    @Inject
    private PatientVestDeviceRawLogRepository patientVestDeviceRawLogRepository;

    /**
     * POST  /patientVestDeviceRawLogs -> Create a new patientVestDeviceRawLog.
     */
    @RequestMapping(value = "/patientVestDeviceRawLogs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody PatientVestDeviceRawLog patientVestDeviceRawLog) throws URISyntaxException {
        log.debug("REST request to save PatientVestDeviceRawLog : {}", patientVestDeviceRawLog);
        if (patientVestDeviceRawLog.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new patientVestDeviceRawLog cannot already have an ID").build();
        }
        patientVestDeviceRawLogRepository.save(patientVestDeviceRawLog);
        return ResponseEntity.created(new URI("/api/patientVestDeviceRawLogs/" + patientVestDeviceRawLog.getId())).build();
    }

    /**
     * PUT  /patientVestDeviceRawLogs -> Updates an existing patientVestDeviceRawLog.
     */
    @RequestMapping(value = "/patientVestDeviceRawLogs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody PatientVestDeviceRawLog patientVestDeviceRawLog) throws URISyntaxException {
        log.debug("REST request to update PatientVestDeviceRawLog : {}", patientVestDeviceRawLog);
        if (patientVestDeviceRawLog.getId() == null) {
            return create(patientVestDeviceRawLog);
        }
        patientVestDeviceRawLogRepository.save(patientVestDeviceRawLog);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /patientVestDeviceRawLogs -> get all the patientVestDeviceRawLogs.
     */
    @RequestMapping(value = "/patientVestDeviceRawLogs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PatientVestDeviceRawLog>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<PatientVestDeviceRawLog> page = patientVestDeviceRawLogRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/patientVestDeviceRawLogs", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /patientVestDeviceRawLogs/:id -> get the "id" patientVestDeviceRawLog.
     */
    @RequestMapping(value = "/patientVestDeviceRawLogs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PatientVestDeviceRawLog> get(@PathVariable Long id) {
        log.debug("REST request to get PatientVestDeviceRawLog : {}", id);
        return Optional.ofNullable(patientVestDeviceRawLogRepository.findOne(id))
            .map(patientVestDeviceRawLog -> new ResponseEntity<>(
                patientVestDeviceRawLog,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /patientVestDeviceRawLogs/:id -> delete the "id" patientVestDeviceRawLog.
     */
    @RequestMapping(value = "/patientVestDeviceRawLogs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete PatientVestDeviceRawLog : {}", id);
        patientVestDeviceRawLogRepository.delete(id);
    }
}
