package com.hillrom.vest.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.repository.PatientInfoRepository;
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
 * REST controller for managing PatientInfo.
 */
@RestController
@RequestMapping("/api")
public class PatientInfoResource {

    private final Logger log = LoggerFactory.getLogger(PatientInfoResource.class);

    @Inject
    private PatientInfoRepository patientInfoRepository;

    /**
     * POST  /patientInfos -> Create a new patientInfo.
     */
    @RequestMapping(value = "/patientInfos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody PatientInfo patientInfo) throws URISyntaxException {
        log.debug("REST request to save PatientInfo : {}", patientInfo);
        if (patientInfo.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new patientInfo cannot already have an ID").build();
        }
        patientInfoRepository.save(patientInfo);
        return ResponseEntity.created(new URI("/api/patientInfos/" + patientInfo.getId())).build();
    }

    /**
     * PUT  /patientInfos -> Updates an existing patientInfo.
     */
    @RequestMapping(value = "/patientInfos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody PatientInfo patientInfo) throws URISyntaxException {
        log.debug("REST request to update PatientInfo : {}", patientInfo);
        if (patientInfo.getId() == null) {
            return create(patientInfo);
        }
        patientInfoRepository.save(patientInfo);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /patientInfos -> get all the patientInfos.
     */
    @RequestMapping(value = "/patientInfos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PatientInfo>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<PatientInfo> page = patientInfoRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/patientInfos", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /patientInfos/:id -> get the "id" patientInfo.
     */
    @RequestMapping(value = "/patientInfos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PatientInfo> get(@PathVariable Long id) {
        log.debug("REST request to get PatientInfo : {}", id);
        return Optional.ofNullable(patientInfoRepository.findOne(id))
            .map(patientInfo -> new ResponseEntity<>(
                patientInfo,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /patientInfos/:id -> delete the "id" patientInfo.
     */
    @RequestMapping(value = "/patientInfos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete PatientInfo : {}", id);
        patientInfoRepository.delete(id);
    }
}
