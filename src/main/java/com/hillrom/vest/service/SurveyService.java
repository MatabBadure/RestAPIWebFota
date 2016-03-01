package com.hillrom.vest.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import net.minidev.json.JSONObject;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.Survey;
import com.hillrom.vest.domain.SurveyQuestion;
import com.hillrom.vest.domain.SurveyQuestionAssoc;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserSurveyAnswer;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.NintyDaySurveyReportVO;
import com.hillrom.vest.repository.NintyDaysResultSetVO;
import com.hillrom.vest.repository.SurveyQuestionAssocRepository;
import com.hillrom.vest.repository.SurveyRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSurveyAnswerRepository;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.web.rest.dto.SurveyQuestionVO;
import com.hillrom.vest.web.rest.dto.SurveyVO;
import com.hillrom.vest.web.rest.dto.UserSurveyAnswerDTO;

@Service
@Transactional
public class SurveyService {

	@Inject
	private SurveyRepository surveyRepository;

	@Inject
	private SurveyQuestionAssocRepository surveyQuestionAssocRepository;

	@Inject
	private UserSurveyAnswerRepository userSurveyAnswerRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private UserService userService;
	
	@Inject
	private PatientNoEventService noEventService;

	public static final Long FIVE_DAY_SURVEY_ID = 1L;
	public static final Long THIRTY_DAY_SURVEY_ID = 2L;
	public static final Long NIGHTY_DAY_SURVEY_ID = 3L;

	public static final Integer FIVE_DAYS = 5;
	public static final Integer THIRTY_DAYS = 30;
	public static final Integer NINTY_DAYS = 90;

	public List<Survey> getAllSurveys() {
		return surveyRepository.findAll();
	}

	public SurveyVO getSurveyById(Long id) throws HillromException {
		Survey survey = surveyRepository.findOne(id);
		SurveyVO surveyVO = new SurveyVO();
		List<SurveyQuestionAssoc> surveyQuestionAssocc;
		if (Objects.nonNull(survey)) {
			surveyVO.setSurveyId(survey.getId());
			surveyVO.setSurveyName(survey.getSurveyName());
			surveyQuestionAssocc = surveyQuestionAssocRepository.findBySurveyId(survey.getId());
			for (SurveyQuestionAssoc surveyQuestionAssoc : surveyQuestionAssocc) {
				surveyVO.getQuestions().add(getQuestionVO(surveyQuestionAssoc.getQuestion()));
			}
		} else
			throw new HillromException(ExceptionConstants.HR_801);
		return surveyVO;
	}

	private SurveyQuestionVO getQuestionVO(SurveyQuestion surveyQues) {

		SurveyQuestionVO questionVO = new SurveyQuestionVO();

		questionVO.setId(surveyQues.getId());
		questionVO.setQuestionText(surveyQues.getQuestionText());
		questionVO.setMandatory(surveyQues.getMandatory());
		questionVO.setTypeCodeFormat(surveyQues.getSurveyAnsFormatId());
		if (Objects.nonNull(surveyQues.getAnswer1()))
			questionVO.getAnswers().add(surveyQues.getAnswer1());
		if (Objects.nonNull(surveyQues.getAnswer2()))
			questionVO.getAnswers().add(surveyQues.getAnswer2());
		if (Objects.nonNull(surveyQues.getAnswer3()))
			questionVO.getAnswers().add(surveyQues.getAnswer3());
		if (Objects.nonNull(surveyQues.getAnswer4()))
			questionVO.getAnswers().add(surveyQues.getAnswer4());
		if (Objects.nonNull(surveyQues.getAnswer5()))
			questionVO.getAnswers().add(surveyQues.getAnswer5());
		if (Objects.nonNull(surveyQues.getAnswer6()))
			questionVO.getAnswers().add(surveyQues.getAnswer6());
		return questionVO;
	}

	public void createSurveyAnswer(UserSurveyAnswerDTO userSurveyAnswerDTO) throws HillromException {
		if (Objects.isNull(userSurveyAnswerDTO.getUserSurveyAnswer())
				|| userSurveyAnswerDTO.getUserSurveyAnswer().isEmpty())
			throw new HillromException(ExceptionConstants.HR_802);

		if (userSurveyAnswerRepository.findCountByUserIdAndSurveyId(userSurveyAnswerDTO.getUserId(),
				userSurveyAnswerDTO.getSurveyId()) > 0)
			throw new HillromException(ExceptionConstants.HR_805);
		User user = userRepository.findOne(userSurveyAnswerDTO.getUserId());
		if (Objects.isNull(user))
			throw new HillromException(ExceptionConstants.HR_512);

		Survey survey = surveyRepository.getOne((userSurveyAnswerDTO.getSurveyId()));
		if (Objects.isNull(survey))
			throw new HillromException(ExceptionConstants.HR_801);
		List<UserSurveyAnswer> userSurveyAnswers = userSurveyAnswerDTO.getUserSurveyAnswer();
		for (UserSurveyAnswer userSurveyAnswer : userSurveyAnswers) {
			userSurveyAnswer.setUser(user);
			userSurveyAnswer.setSurvey(survey);
		}

		userSurveyAnswerRepository.save(userSurveyAnswers);
	}

	public UserSurveyAnswerDTO getSurveyAnswerById(Long id) throws HillromException {

		UserSurveyAnswer userSurveyAnswer = userSurveyAnswerRepository.findOne(id);
		if (Objects.isNull(userSurveyAnswer))
			throw new HillromException(ExceptionConstants.HR_512);
		UserSurveyAnswerDTO userSurveyAnswerDTO = new UserSurveyAnswerDTO();
		userSurveyAnswerDTO.setSurveyId(userSurveyAnswer.getSurvey().getId());
		userSurveyAnswerDTO.setUserId(userSurveyAnswer.getUser().getId());
		userSurveyAnswerDTO.getUserSurveyAnswer().add(userSurveyAnswer);
		return userSurveyAnswerDTO;
	}

	public Survey getDueSurveyByUserId(Long id) throws HillromException {
		User user = userRepository.findOne(id);
		if (Objects.isNull(user))
			throw new HillromException(ExceptionConstants.HR_512);

		if (Objects.isNull(userService.getPatientInfoObjFromPatientUser(user)))
			throw new HillromException(ExceptionConstants.HR_523);

		// Checking whether first transmission was done
		PatientNoEvent noEvent = noEventService.findByPatientUserId(id);
		if( Objects.isNull(noEvent) || Objects.isNull(noEvent.getFirstTransmissionDate()))
			throw new HillromException(ExceptionConstants.HR_804);
		
		LocalDate firstTransmissionDate = noEvent.getFirstTransmissionDate();
		
		int daysDifference = DateUtil.getDaysCountBetweenLocalDates(firstTransmissionDate, LocalDate.now()); 
		if ((daysDifference >= FIVE_DAYS && daysDifference < THIRTY_DAYS)
				&& userSurveyAnswerRepository.findCountByUserIdAndSurveyId(id, FIVE_DAY_SURVEY_ID) < 1) {
			return surveyRepository.findOne(FIVE_DAY_SURVEY_ID);
		} else if (daysDifference >= THIRTY_DAYS
				&& daysDifference < NINTY_DAYS
				&& userSurveyAnswerRepository.findCountByUserIdAndSurveyId(id, THIRTY_DAY_SURVEY_ID) < 1) {
			return surveyRepository.findOne(THIRTY_DAY_SURVEY_ID);
		} else if (daysDifference >= NINTY_DAYS
				&& userSurveyAnswerRepository.findCountByUserIdAndSurveyId(id, NIGHTY_DAY_SURVEY_ID) < 1) {
			return surveyRepository.findOne(NIGHTY_DAY_SURVEY_ID);
		} else
			throw new HillromException(ExceptionConstants.HR_804);
	}

	public JSONObject getGridView(Long surveyId, LocalDate fromDate, LocalDate toDate) throws HillromException {
		JSONObject responseJSON = new JSONObject();
		if (FIVE_DAY_SURVEY_ID.equals(surveyId)) {
			responseJSON.put("count", userSurveyAnswerRepository.findSurveyCountByDateRange(surveyId,
					fromDate.toDateTime(LocalTime.MIDNIGHT), toDate.plusDays(1).toDateTime(LocalTime.MIDNIGHT)));
			responseJSON.put("surveyGridView",
					userSurveyAnswerRepository.fiveDaySurveyReport(fromDate.toString(), toDate.toString()));
			return responseJSON;
		} else if (THIRTY_DAY_SURVEY_ID.equals(surveyId)) {
			responseJSON.put("count", userSurveyAnswerRepository.findSurveyCountByDateRange(surveyId,
					fromDate.toDateTime(LocalTime.MIDNIGHT), toDate.plusDays(1).toDateTime(LocalTime.MIDNIGHT)));
			responseJSON.put("surveyGridView",
					userSurveyAnswerRepository.thirtyDaySurveyReport(fromDate.toString(), toDate.toString()));
			return responseJSON;
		} else if (NIGHTY_DAY_SURVEY_ID.equals(surveyId)) {
			responseJSON.put("count", userSurveyAnswerRepository.findSurveyCountByDateRange(surveyId,
					fromDate.toDateTime(LocalTime.MIDNIGHT), toDate.plusDays(1).toDateTime(LocalTime.MIDNIGHT)));
			responseJSON.put("surveyGridView",getNintyDaySurveyResponse(fromDate.toString(), toDate.toString()));
			return responseJSON;
		} else
			throw new HillromException(ExceptionConstants.HR_801);
	}

	private List<NintyDaySurveyReportVO> getNintyDaySurveyResponse(String fromDateTime, String toDateTime) {
		Map<Long, List<NintyDaysResultSetVO>> nintyDaysRSGroupedByUserID = (Map) userSurveyAnswerRepository
				.nintyDaySurveyReport(fromDateTime,toDateTime).stream()
				.collect(Collectors.groupingBy(NintyDaysResultSetVO::getUserId));

		List<NintyDaySurveyReportVO> nintyDaySurveyReportVOs = new LinkedList<>();
		NintyDaySurveyReportVO nintyDaySurveyReportVO;

		List<NintyDaysResultSetVO> groupedNintyDaysResultSetVO;

		for (Long userId : nintyDaysRSGroupedByUserID.keySet()) {
			groupedNintyDaysResultSetVO = nintyDaysRSGroupedByUserID.get(userId);
			nintyDaySurveyReportVO = new NintyDaySurveyReportVO(groupedNintyDaysResultSetVO.get(0).getAnswerValue(),
					groupedNintyDaysResultSetVO.get(1).getAnswerValue(),
					groupedNintyDaysResultSetVO.get(2).getAnswerValue(),
					groupedNintyDaysResultSetVO.get(3).getAnswerValue(),
					groupedNintyDaysResultSetVO.get(4).getAnswerValue(),
					groupedNintyDaysResultSetVO.get(5).getAnswerValue());
			nintyDaySurveyReportVOs.add(nintyDaySurveyReportVO);
		}
		return nintyDaySurveyReportVOs;

	}
}
