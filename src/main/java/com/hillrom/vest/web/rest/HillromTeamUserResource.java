package com.hillrom.vest.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
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
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.service.MailService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.web.rest.dto.HillromTeamUserDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

/**
 * REST controller for managing HillromTeamUser.
 */
@RestController
@RequestMapping("/api")
public class HillromTeamUserResource {

    private final Logger log = LoggerFactory.getLogger(HillromTeamUserResource.class);

    @Inject
    private UserService userService;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MailService mailService;

    /**
     * POST  /hillromteamuser -> Create a new hillromteamuser.
     */
    @RequestMapping(value = "/hillromteamuser",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> create(@RequestBody HillromTeamUserDTO hillromTeamUserDTO, HttpServletRequest request) throws URISyntaxException {
        log.debug("REST request to save HillromTeamUser : {}", hillromTeamUserDTO);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "e-mail address already in use");
        return userRepository.findOneByEmail(hillromTeamUserDTO.getEmail())
        		.map(user -> {
        			return ResponseEntity.badRequest().body(jsonObject);
        		})
                .orElseGet(() -> {
                    User user = userService.createUser(hillromTeamUserDTO);
                    String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                    mailService.sendActivationEmail(user, baseUrl);
                    jsonObject.put("message", "User created successfully.");
                    jsonObject.put("user", user);
                    return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
                });
    }

    /**
     * PUT  /hillromteamuser -> Updates an existing hillromteamuser.
     */
    @RequestMapping(value = "/hillromteamuser",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> update(@RequestBody Map<String, String> body, @RequestBody User hillromTeamUser, HttpServletRequest request) throws URISyntaxException {
        log.debug("REST request to update HillromTeamUser : {}", body);
        JSONObject jsonObject = new JSONObject();
        if (body.get("id") == null) {
            return create(new HillromTeamUserDTO(body.get("title"), body.get("firstName"), body.get("middleName"), body.get("lastName"), body.get("email"), body.get("role")), request);
        }
        userRepository.save(hillromTeamUser);
        jsonObject.put("message", "User updated successfully.");
        return ResponseEntity.ok().body(jsonObject);
    }

    /**
     * GET  /hillromteamuser -> get all the hillromteamuser.
     */
    @RequestMapping(value = "/hillromteamuser",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<User>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<User> page = userRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/hillromteamuser", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /hillromteamuser/:id -> get the "id" hillromteamuser.
     */
    @RequestMapping(value = "/hillromteamuser/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User> get(@PathVariable Long id) {
        log.debug("REST request to get HillromTeamUser : {}", id);
        return Optional.ofNullable(userRepository.findOne(id))
            .map(user -> new ResponseEntity<>(
                user,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /hillromteamuser/:id -> delete the "id" hillromteamuser.
     */
    @RequestMapping(value = "/hillromteamuser/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> delete(@PathVariable Long id) {
        log.debug("REST request to delete HillromTeamUser : {}", id);
        JSONObject jsonObject = new JSONObject();
        userRepository.delete(id);
        jsonObject.put("message", "User deleted successfully.");
        return ResponseEntity.ok().body(jsonObject);
    }
}
