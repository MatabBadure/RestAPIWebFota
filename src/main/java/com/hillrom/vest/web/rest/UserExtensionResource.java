package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
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
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.repository.HcpVO;
import com.hillrom.vest.repository.HillRomUserVO;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserSearchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.HCPClinicService;
import com.hillrom.vest.service.PatientHCPService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.web.rest.dto.UserExtensionDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

/**
 * REST controller for managing user.
 */
@RestController
@RequestMapping("/api")
public class UserExtensionResource {

    private final Logger log = LoggerFactory.getLogger(UserExtensionResource.class);

    @Inject
    private UserExtensionRepository userExtensionRepository;
    
    @Inject
    private UserService userService;

    @Inject
    private HCPClinicService hcpClinicService;
    
    @Inject
    private PatientHCPService patientHCPService;
    
    @Inject
    private UserSearchRepository userSearchRepository;
    /**
     * POST  /user -> Create a new User.
     */
    @RequestMapping(value = "/user",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> create(@RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to save User : {}", userExtensionDTO);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        JSONObject jsonObject = userService.createUser(userExtensionDTO, baseUrl);
        if (jsonObject.containsKey("ERROR")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
        }
    }

    /**
     * PUT  /user/:id -> Updates an existing user.
     */
    @RequestMapping(value = "/user/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> update(@PathVariable Long id, @RequestBody UserExtensionDTO userExtensionDTO, HttpServletRequest request) {
        log.debug("REST request to update User : {}", userExtensionDTO);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        JSONObject jsonObject = userService.updateUser(id, userExtensionDTO, baseUrl);
        if (jsonObject.containsKey("ERROR")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        }
    }

    /**
     * GET  /user -> get all the users.
     */
    @RequestMapping(value = "/user",
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
     * GET  /user/:id -> get the "id" user.
     */
    @RequestMapping(value = "/user/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> get(@PathVariable Long id) {
        log.debug("REST request to get UserExtension : {}", id);
        JSONObject jsonObject = userService.getUser(id);
        if (jsonObject.containsKey("ERROR")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        }
    }

    /**
     * DELETE  /user/:id -> delete the "id" user.
     */
    @RequestMapping(value = "/user/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> delete(@PathVariable Long id) {
        log.debug("REST request to delete UserExtension : {}", id);
        JSONObject jsonObject = userService.deleteUser(id);
        if (jsonObject.containsKey("ERROR")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        }
    }
    
    /**
     * PUT  /user/:id/dissociateclinic -> dissociate clinic from the "id" user.
     */
    @RequestMapping(value = "/user/{id}/dissociateclinic",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> dissociateClinicFromHCP(@PathVariable Long id, @RequestBody List<Map<String, String>> clinicList) {
        log.debug("REST request to dissociate clinic from HCP : {}", id);
        JSONObject jsonObject = hcpClinicService.dissociateClinicFromHCP(id, clinicList);
        if (jsonObject.containsKey("message")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * GET  /user/search -> get all HillromTeamUser.
     */
    @RequestMapping(value = "/user/search",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<HillRomUserVO>> search(@RequestParam(required=true,value = "searchString")String searchString,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending)
        throws URISyntaxException {
    	String queryString = new StringBuilder("'%").append(searchString).append("%'").toString();
    	Map<String,Boolean> sortOrder = new HashMap<>();
    	if(sortBy != null  && !sortBy.equals("")) {
    		isAscending =  (isAscending != null)?  isAscending : true;
    		sortOrder.put(sortBy, isAscending);
    	}
    	Page<HillRomUserVO> page = userSearchRepository.findHillRomTeamUsersBy(queryString,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/user/search", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        
    }
    
    /**
     * GET  /user/search -> get all HillromTeamUser.
     */
    @RequestMapping(value = "/user/hcp/search",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> searchHcp(@RequestParam(required=true,value = "searchString")String searchString,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending)
        throws URISyntaxException {
    	String queryString = new StringBuilder("'%").append(searchString).append("%'").toString();
    	Map<String,Boolean> sortOrder = new HashMap<>();
    	if(sortBy != null  && !sortBy.equals("")) {
    		isAscending =  (isAscending != null) ?  isAscending : true;
    		sortOrder.put(sortBy, isAscending);
    	}
    	Page<HcpVO> page = userSearchRepository.findHCPBy(queryString,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/user/hcp/search", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        
    }
    
    /**
     * GET  /user/:id/hcp -> get the "id" HCP user.
     */
    @RequestMapping(value = "/user/{id}/hcp",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> getHCPUser(@PathVariable Long id) {
        log.debug("REST request to get UserExtension : {}", id);
        JSONObject jsonObject = userService.getHCPUser(id);
        if (jsonObject.containsKey("ERROR")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        }
    }
    
    /**
     * PUT  /patient/:id/associatehcp -> associate hcp to the "id" patient user.
     */
    @RequestMapping(value = "/patient/{id}/associatehcp",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> associateHCPToPatient(@PathVariable Long id, @RequestBody List<Map<String, String>> hcpList) {
        log.debug("REST request to dissociate clinic from HCP : {}", id);
        JSONObject jsonObject = patientHCPService.associateHCPToPatient(id, hcpList);
        if (jsonObject.containsKey("ERROR")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        }
    }
    
    /**
     * GET  /patient/:id/hcp -> get the HCP users associated with patient user.
     */
    @RequestMapping(value = "/patient/{id}/hcp",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> getAssociatedHCPUserForPatient(@PathVariable Long id) {
        log.debug("REST request to get Associated HCP users for Patient : {}", id);
        JSONObject jsonObject = patientHCPService.getAssociatedHCPUserForPatient(id);
        if (jsonObject.containsKey("ERROR")) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        }
    }
}
