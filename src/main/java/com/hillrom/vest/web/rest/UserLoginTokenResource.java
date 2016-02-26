package com.hillrom.vest.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.UserLoginTokenRepository;
import com.hillrom.vest.service.UserLoginTokenService;
import com.hillrom.vest.web.rest.dto.Graph;

/**
 * REST controller for managing UserLoginToken.
 */
@RestController
@RequestMapping("/api")
public class UserLoginTokenResource {

    private final Logger log = LoggerFactory.getLogger(UserLoginTokenResource.class);

    @Inject
    private UserLoginTokenRepository userLoginTokenRepository;
    
    @Inject
    private UserLoginTokenService userLoginTokenService;

    /**
     * POST  /userLoginTokens -> Create a new userLoginToken.
     */
    @RequestMapping(value = "/userLoginTokens",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> create(@Valid @RequestBody UserLoginToken userLoginToken) throws URISyntaxException {
        log.debug("REST request to save UserLoginToken : {}", userLoginToken);
        if (userLoginToken.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new userLoginToken cannot already have an ID").build();
        }
        userLoginTokenRepository.save(userLoginToken);
        return ResponseEntity.created(new URI("/api/userLoginTokens/" + userLoginToken.getId())).build();
    }

    /**
     * PUT  /userLoginTokens -> Updates an existing userLoginToken.
     */
    @RequestMapping(value = "/userLoginTokens",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> update(@Valid @RequestBody UserLoginToken userLoginToken) throws URISyntaxException {
        log.debug("REST request to update UserLoginToken : {}", userLoginToken);
        if (userLoginToken.getId() == null) {
            return create(userLoginToken);
        }
        userLoginTokenRepository.save(userLoginToken);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /userLoginTokens -> get all the userLoginTokens.
     */
    @RequestMapping(value = "/userLoginTokens",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public List<UserLoginToken> getAll() {
        log.debug("REST request to get all UserLoginTokens");
        return userLoginTokenRepository.findAll();
    }

    /**
     * GET  /userLoginTokens/:id -> get the "id" userLoginToken.
     */
    @RequestMapping(value = "/userLoginTokens/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<UserLoginToken> get(@PathVariable String id) {
        log.debug("REST request to get UserLoginToken : {}", id);
        return Optional.ofNullable(userLoginTokenRepository.findOne(id))
            .map(userLoginToken -> new ResponseEntity<>(
                userLoginToken,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /userLoginTokens/:id -> delete the "id" userLoginToken.
     */
    @RequestMapping(value = "/userLoginTokens/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public void delete(@PathVariable String id) {
        log.debug("REST request to delete UserLoginToken : {}", id);
        userLoginTokenRepository.delete(id);
    }
    
    /**
     * GET  /loginAnalytics -> Get LoginAnalytics
     * @throws HillromException 
     */
    @RequestMapping(value = "/loginAnalytics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Graph getLoAnalytics(
    		@RequestParam(required=true,value="from")@DateTimeFormat(pattern="yyyy-MM-dd")LocalDate from,
    		@RequestParam(required=true,value="to")@DateTimeFormat(pattern="yyyy-MM-dd")LocalDate to,
    		@RequestParam(required=true,value="filters")String authorityCSV,
    		@RequestParam(required=true,value="duration")String duration) throws HillromException{
    	return userLoginTokenService.getLoginAnalytics(from, to, authorityCSV,duration);
    }
}
