package com.hillrom.vest.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.repository.UserLoginTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing UserLoginToken.
 */
@RestController
@RequestMapping("/api")
public class UserLoginTokenResource {

    private final Logger log = LoggerFactory.getLogger(UserLoginTokenResource.class);

    @Inject
    private UserLoginTokenRepository userLoginTokenRepository;

    /**
     * POST  /userLoginTokens -> Create a new userLoginToken.
     */
    @RequestMapping(value = "/userLoginTokens",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
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
    @Timed
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
    @Timed
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
    @Timed
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
    @Timed
    public void delete(@PathVariable String id) {
        log.debug("REST request to delete UserLoginToken : {}", id);
        userLoginTokenRepository.delete(id);
    }
}
