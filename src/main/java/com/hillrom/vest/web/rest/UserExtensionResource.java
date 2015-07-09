package com.hillrom.vest.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.MailService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.web.rest.dto.UserExtensionDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

/**
 * REST controller for managing UserExtension.
 */
@RestController
@RequestMapping("/api")
public class UserExtensionResource {

    private final Logger log = LoggerFactory.getLogger(UserExtensionResource.class);

    @Inject
    private UserExtensionRepository userExtensionRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MailService mailService;
    
    @Inject
    private UserService userService;

    /**
     * POST  /userExtensions -> Create a new userExtension.
     */
    @RequestMapping(value = "/doctor",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> create(@RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) throws URISyntaxException {
        log.debug("REST request to save UserExtension : {}", userExtensionDTO);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "e-mail address already in use");
        return userRepository.findOneByEmail(userExtensionDTO.getEmail())
        		.map(user -> {
        			return ResponseEntity.badRequest().body(jsonObject);
        		})
                .orElseGet(() -> {
                	if (userExtensionDTO.getRole() == AuthoritiesConstants.DOCTOR) {
                		UserExtension user = userService.createDoctor(userExtensionDTO);
                        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                        mailService.sendActivationEmail(user, baseUrl);
                        jsonObject.put("message", "Doctor created successfully.");
                        jsonObject.put("user", user);
                        return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
                	} else {
                		jsonObject.put("message", "Incorrect data.");
                		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.PARTIAL_CONTENT);
                	}
                });
    }

    /**
     * PUT  /userExtensions -> Updates an existing userExtension.
     */
    @RequestMapping(value = "/doctor",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody UserExtension userExtension, HttpServletRequest request) throws URISyntaxException {
        log.debug("REST request to update UserExtension : {}", userExtension);
        if (userExtension.getId() == null) {
            //return create(userExtension, request);
        }
        userExtensionRepository.save(userExtension);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /userExtensions -> get all the userExtensions.
     */
    @RequestMapping(value = "/doctor",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<UserExtension>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<UserExtension> page = userExtensionRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/userExtensions", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /userExtensions/:id -> get the "id" userExtension.
     */
    @RequestMapping(value = "/doctor/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserExtension> get(@PathVariable Long id) {
        log.debug("REST request to get UserExtension : {}", id);
        return Optional.ofNullable(userExtensionRepository.findOne(id))
            .map(userExtension -> new ResponseEntity<>(
                userExtension,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /userExtensions/:id -> delete the "id" userExtension.
     */
    @RequestMapping(value = "/doctor/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> delete(@PathVariable Long id) {
        log.debug("REST request to delete UserExtension : {}", id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "No such user exists.");
        return Optional.ofNullable(userExtensionRepository.findOne(id))
                .map(user -> {
                	userExtensionRepository.delete(user);
                    jsonObject.put("message", "User deleted successfully.");
                    return ResponseEntity.ok().body(jsonObject);
                }).orElse(new ResponseEntity<JSONObject>(jsonObject, HttpStatus.NOT_FOUND));
    }
}
