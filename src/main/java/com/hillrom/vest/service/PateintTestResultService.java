package com.hillrom.vest.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientTestResult;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientTestResultRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;

@Service
@Transactional
public class PateintTestResultService {

	@Inject
	private PatientTestResultRepository patientTestResultRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserPatientRepository userPatientRepository;

	public List<PatientTestResult> getPatientTestResult(LocalDate from, LocalDate to) {

		return patientTestResultRepository.findAll();
	}

	public PatientTestResult getPatientTestResultById(Long id) throws HillromException {
		PatientTestResult patientTestResult = patientTestResultRepository.findOne(id);
		if(Objects.isNull(patientTestResult))
			throw new HillromException(ExceptionConstants.HR_719);
		else
			return patientTestResult;
			
	}

	public List<PatientTestResult> getPatientTestResultByUserId(Long id, LocalDate from, LocalDate to) {
		return patientTestResultRepository.findByUserIdAndBetweenTestResultDate(id, from, to);
	}

	public PatientTestResult createPatientTestResult(PatientTestResult patientTestResult, Long userId, String baseURL)
			throws HillromException {
		
		User user = userRepository.getOne(userId);

		if (Objects.isNull(user))
			throw new HillromException(ExceptionConstants.HR_512);
		
		patientTestResult.setUser(user);
		List<UserPatientAssoc> userPatientAssocs = null;
		if (Objects.isNull(patientTestResult.getPatientInfo())) {
			userPatientAssocs = userPatientRepository.findByUserIdAndUserRole(userId, AuthoritiesConstants.PATIENT);
			for (UserPatientAssoc userPatientAssoc : userPatientAssocs)
				if (RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())) {
					patientTestResult.setPatientInfo(userPatientAssoc.getPatient());
					break;
				}
		}
		String updatedBy = getUpdatedUserName();
		patientTestResult.setLastUpdatedBy(updatedBy);
		return patientTestResultRepository.saveAndFlush(patientTestResult);
	}	
	
	public PatientTestResult updatePatientTestResult(PatientTestResult patientTestResult, Long userId, String baseURL)
			throws HillromException {
		if (!patientTestResultRepository.exists(patientTestResult.getId()))
			throw new HillromException(ExceptionConstants.HR_719);
		User user = userRepository.getOne(userId);

		if (Objects.isNull(user))
			throw new HillromException(ExceptionConstants.HR_512);
		
		patientTestResult.setUser(user);
		List<UserPatientAssoc> userPatientAssocs = null;
		if (Objects.isNull(patientTestResult.getPatientInfo())) {
			userPatientAssocs = userPatientRepository.findByUserIdAndUserRole(userId, AuthoritiesConstants.PATIENT);
			for (UserPatientAssoc userPatientAssoc : userPatientAssocs)
				if (RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())) {
					patientTestResult.setPatientInfo(userPatientAssoc.getPatient());
					break;
				}
		}
		String updatedBy = getUpdatedUserName();
		patientTestResult.setLastUpdatedBy(updatedBy);
		return patientTestResultRepository.saveAndFlush(patientTestResult);
	}
	
	public String getUpdatedUserName() throws HillromException{
		Optional<User> userFromDB = userRepository.findOneByEmailOrHillromId(SecurityUtils.getCurrentLogin());
		if(userFromDB.isPresent()){
			User user = userFromDB.get();
			return new StringBuilder(user.getLastName()).append(" ").append(user.getFirstName()).toString();
		}else{
			throw new HillromException(ExceptionConstants.HR_512);
		}
	}
}
