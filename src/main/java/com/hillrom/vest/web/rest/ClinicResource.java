package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.service.ClinicService;
import com.hillrom.vest.web.rest.dto.ClinicDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Clinic.
 */
@RestController
@RequestMapping("/api")
public class ClinicResource {

    private final Logger log = LoggerFactory.getLogger(ClinicResource.class);

    @Inject
    private ClinicRepository clinicRepository;
    
    @Inject
    private ClinicService clinicService;

    /**
     * POST  /clinics -> Create a new clinic.
     */
    @RequestMapping(value = "/clinics",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> create(@RequestBody ClinicDTO clinicDTO) throws URISyntaxException {
        log.debug("REST request to save Clinic : {}", clinicDTO);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Clinic name already in use");
        return clinicRepository.findOneByName(clinicDTO.getName())
        		.map(newClinic -> {
        			System.out.println("Clinic found : "+newClinic);
        			return ResponseEntity.badRequest().body(jsonObject);
        		})
                .orElseGet(() -> {
                    Clinic newClinic = clinicService.createClinic(clinicDTO);
                    jsonObject.put("message", "Clinic created successfully.");
                    jsonObject.put("clinic", newClinic);
                    return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
                });
    }

    /**
     * PUT  /clinics -> Updates an existing clinic.
     */
    @RequestMapping(value = "/clinics",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> update(@RequestBody Clinic clinic) throws URISyntaxException {
        log.debug("REST request to update Clinic : {}", clinic);
        JSONObject jsonObject = new JSONObject();
        if (clinic.getId() == null) {
            //return create(clinic);
        }
        clinicRepository.save(clinic);
        jsonObject.put("message", "User updated successfully.");
        return ResponseEntity.ok().body(jsonObject);
    }

    /**
     * GET  /clinics -> get all the clinics.
     */
    @RequestMapping(value = "/clinics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Clinic>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Clinic> page = clinicRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/clinics", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /clinics/:id -> get the "id" clinic.
     */
    @RequestMapping(value = "/clinics/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Clinic> get(@PathVariable Long id) {
        log.debug("REST request to get Clinic : {}", id);
        return Optional.ofNullable(clinicRepository.findOneWithEagerRelationships(id))
            .map(clinic -> new ResponseEntity<>(
                clinic,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /clinics/:id -> delete the "id" clinic.
     */
    @RequestMapping(value = "/clinics/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> delete(@PathVariable Long id) {
    	log.debug("REST request to delete Clinic : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	clinicRepository.delete(id);
    	jsonObject.put("message", "Clinic deleted successfully.");
    	return ResponseEntity.ok().body(jsonObject);
    }
}
