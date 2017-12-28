package com.hillrom.vest.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientTestResult;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.EntityUserRepository;
import com.hillrom.vest.repository.PatientTestResultRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.util.QueryConstants;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.PatientTestResultVO;

@Service
@Transactional
public class PateintTestResultService {

	@Inject
	private PatientTestResultRepository patientTestResultRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserPatientRepository userPatientRepository;
	
	@Inject
	private EntityUserRepository entityUserRepository;
	
	@Inject
	private EntityManager entityManager;

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

	public List<PatientTestResultVO> getPatientTestResultAvgByUserId(Long pid, LocalDate from, LocalDate to) {
			
		String tst = QueryConstants.QUERY_PATIENT_TEST_RESULT;
		String tst1 = tst+"'"+pid+"' and patientTestResult.test_result_date between '" +from+"' and '"+to+"' GROUP BY resDate";
		
		Query query = entityManager.createNativeQuery(tst1);
		List<Object[]> results = query.getResultList();
		List<PatientTestResultVO> patientTestResultVo = new ArrayList<>();
		results.stream().forEach((result) -> {
			Long id = ((BigInteger) result[0]).longValue();
			String patientInfo = (String)result[1];
			Long user= ((BigInteger) result[2]).longValue();
			Date testResultDate = (Date) result[3];
			float fVC_L= (float) result[4];
			float fEV1_L = (float) result[5];
			float pEF_L_Min= (float) result[6];
			double fVC_P = (double) result[7];
			double fEV1_P = (double) result[8];
			float pEF_P = (float) result[9];
			float fEV1_TO_FVC_RATIO = (float) result[10];
			String comments = (String) result[11];
			String lastUpdatedBy = (String) result[12];
			
			patientTestResultVo.add(new PatientTestResultVO(id,patientInfo,user,
					testResultDate,  fVC_L,  fEV1_L,
					pEF_L_Min,  fVC_P,  fEV1_P,  pEF_P,
					fEV1_TO_FVC_RATIO,  comments, lastUpdatedBy));
		});	
				
		return patientTestResultVo;
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
	
	public PatientTestResult createPatientTestResultByHCPOrClinicAdmin(PatientTestResult patientTestResult, Long patientUserId, Long userId)
			throws HillromException {
		
		User user = userRepository.getOne(patientUserId);

		if (Objects.isNull(user))
			throw new HillromException(ExceptionConstants.HR_512);
		
		patientTestResult.setUser(user);
		List<UserPatientAssoc> userPatientAssocs = null;
		if (Objects.isNull(patientTestResult.getPatientInfo())) {
			userPatientAssocs = userPatientRepository.findByUserIdAndUserRole(patientUserId, AuthoritiesConstants.PATIENT);
			for (UserPatientAssoc userPatientAssoc : userPatientAssocs)
				if (RelationshipLabelConstants.SELF.equals(userPatientAssoc.getRelationshipLabel())) {
					patientTestResult.setPatientInfo(userPatientAssoc.getPatient());
					break;
				}
		}
		if(validateAssociation(patientTestResult.getPatientInfo().getId(), userId)){
			String updatedBy = getUpdatedUserName();
			patientTestResult.setLastUpdatedBy(updatedBy);
			return patientTestResultRepository.saveAndFlush(patientTestResult);
		}else{
			throw new HillromException(ExceptionConstants.HR_403);
		}
	}
	
	private boolean validateAssociation(String patientUserId, Long userId){
		if(SecurityUtils.isUserInRole(AuthoritiesConstants.HCP))
			return Objects.nonNull(userPatientRepository.returnUserIdIfAssociationExists(userId, AuthoritiesConstants.HCP, patientUserId, true));
		if(SecurityUtils.isUserInRole(AuthoritiesConstants.CLINIC_ADMIN))
			return Objects.nonNull(entityUserRepository.returnUserIdIfAssociationExists(userId, patientUserId, true));
		return false;
	}
}
