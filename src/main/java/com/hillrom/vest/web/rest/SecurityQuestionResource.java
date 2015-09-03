package com.hillrom.vest.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.SecurityQuestion;
import com.hillrom.vest.repository.SecurityQuestionRepository;

/**
 * REST controller for managing SecurityQuestion.
 */
@RestController
@RequestMapping("/api")
public class SecurityQuestionResource {

    private final Logger log = LoggerFactory.getLogger(SecurityQuestionResource.class);

    @Inject
    private SecurityQuestionRepository securityQuestionRepository;

    /**
     * POST  /securityQuestions -> Create a new securityQuestion.
     */
    @RequestMapping(value = "/securityQuestions",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> create(@Valid @RequestBody SecurityQuestion securityQuestion) throws URISyntaxException {
        log.debug("REST request to save SecurityQuestion : {}", securityQuestion);
        if (securityQuestion.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new securityQuestion cannot already have an ID").build();
        }
        securityQuestionRepository.save(securityQuestion);
        return ResponseEntity.created(new URI("/api/securityQuestions/" + securityQuestion.getId())).build();
    }

    /**
     * PUT  /securityQuestions -> Updates an existing securityQuestion.
     */
    @RequestMapping(value = "/securityQuestions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> update(@Valid @RequestBody SecurityQuestion securityQuestion) throws URISyntaxException {
        log.debug("REST request to update SecurityQuestion : {}", securityQuestion);
        if (securityQuestion.getId() == null) {
            return create(securityQuestion);
        }
        securityQuestionRepository.save(securityQuestion);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /securityQuestions -> get all the securityQuestions.
     */
    @RequestMapping(value = "/securityQuestions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public List<SecurityQuestion> getAll() {
        log.debug("REST request to get all SecurityQuestions");
        return securityQuestionRepository.findAll();
    }

    /**
     * GET  /securityQuestions/:id -> get the "id" securityQuestion.
     */
    @RequestMapping(value = "/securityQuestions/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<SecurityQuestion> get(@PathVariable Long id) {
        log.debug("REST request to get SecurityQuestion : {}", id);
        return Optional.ofNullable(securityQuestionRepository.findOne(id))
            .map(securityQuestion -> new ResponseEntity<>(
                securityQuestion,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /securityQuestions/:id -> delete the "id" securityQuestion.
     */
    @RequestMapping(value = "/securityQuestions/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete SecurityQuestion : {}", id);
        securityQuestionRepository.delete(id);
    }
}
