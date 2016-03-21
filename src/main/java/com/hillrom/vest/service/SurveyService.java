package com.hillrom.vest.service;

import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.Survey;
import com.hillrom.vest.domain.SurveyQuestion;
import com.hillrom.vest.domain.SurveyQuestionAssoc;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserSurveyAnswer;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.SurveyQuestionAssocRepository;
import com.hillrom.vest.repository.SurveyQuestionRepository;
import com.hillrom.vest.repository.SurveyRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSurveyAnswerRepository;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.FiveDaySurveyReportVO;
import com.hillrom.vest.web.rest.dto.FiveDayViewVO;
import com.hillrom.vest.web.rest.dto.NintyDaySurveyReportVO;
import com.hillrom.vest.web.rest.dto.SurveyAnswerResultSetVO;
import com.hillrom.vest.web.rest.dto.SurveyGraph;
import com.hillrom.vest.web.rest.dto.SurveyQuestionVO;
import com.hillrom.vest.web.rest.dto.SurveyVO;
import com.hillrom.vest.web.rest.dto.ThirtyDaySurveyReportVO;
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
	private MailService mailService;

	@Inject
	private UserService userService;

	@Inject
	private PatientNoEventService noEventService;
	
	@Inject
	private SurveyQuestionRepository surveyQuestionRepository;
	
	@Qualifier("surveyGraphService")
	@Inject
	private GraphService graphService;
	
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

	public void createSurveyAnswer(UserSurveyAnswerDTO userSurveyAnswerDTO, String baseUrl) throws HillromException {
		// Take survey only if you are eligible to take
		if (getDueSurveyByUserId(userSurveyAnswerDTO.getUserId()).getId() != userSurveyAnswerDTO.getSurveyId())
			throw new HillromException(ExceptionConstants.HR_806);

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
		// Email
		mailService.sendSurveyEmailReport(userSurveyAnswerDTO, baseUrl);
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

	public Survey getDueSurveyByUserId(Long userId) throws HillromException {
		User user = userRepository.findOne(userId);
		if (Objects.isNull(user))
			throw new HillromException(ExceptionConstants.HR_512);

		if (Objects.isNull(userService.getPatientInfoObjFromPatientUser(user)))
			throw new HillromException(ExceptionConstants.HR_523);

		// Checking whether first transmission was done
		PatientNoEvent noEvent = noEventService.findByPatientUserId(userId);
		if (Objects.isNull(noEvent) || Objects.isNull(noEvent.getFirstTransmissionDate()))
			throw new HillromException(ExceptionConstants.HR_804);

		LocalDate firstTransmissionDate = noEvent.getFirstTransmissionDate();

		int daysDifference = DateUtil.getDaysCountBetweenLocalDates(firstTransmissionDate, LocalDate.now())+1; // days are inclusive for surveys;
		if ((daysDifference >= RandomUtil.FIVE_DAYS && daysDifference < RandomUtil.THIRTY_DAYS)
				&& userSurveyAnswerRepository.findCountByUserIdAndSurveyId(userId, RandomUtil.FIVE_DAY_SURVEY_ID) < 1) {
			return surveyRepository.findOne(RandomUtil.FIVE_DAY_SURVEY_ID);
		} else if (daysDifference >= RandomUtil.THIRTY_DAYS && daysDifference < RandomUtil.NINTY_DAYS
				&& userSurveyAnswerRepository.findCountByUserIdAndSurveyId(userId,
						RandomUtil.THIRTY_DAY_SURVEY_ID) < 1) {
			return surveyRepository.findOne(RandomUtil.THIRTY_DAY_SURVEY_ID);
		} else if (daysDifference >= RandomUtil.NINTY_DAYS && userSurveyAnswerRepository
				.findCountByUserIdAndSurveyId(userId, RandomUtil.NIGHTY_DAY_SURVEY_ID) < 1) {
			return surveyRepository.findOne(RandomUtil.NIGHTY_DAY_SURVEY_ID);
		} else
			throw new HillromException(ExceptionConstants.HR_804);
	}

	public JSONObject getGridView(Long surveyId, LocalDate fromDate, LocalDate toDate) throws HillromException {
		JSONObject responseJSON = new JSONObject();
		List<Long> questionIds = getSurveyQuestionIdsBySurveyId(surveyId);
		Integer surveyCount = userSurveyAnswerRepository.findSurveyCountByDateRange(surveyId,
				fromDate.toDateTime(LocalTime.MIDNIGHT), toDate.plusDays(1).toDateTime(LocalTime.MIDNIGHT));
		if (RandomUtil.FIVE_DAY_SURVEY_ID.equals(surveyId)) {
			responseJSON.put("count", surveyCount);
			responseJSON.put("surveyGridView",
					userSurveyAnswerRepository.fiveDaySurveyReport(fromDate.toString(), toDate.toString(),questionIds));
			return responseJSON;
		} else if (RandomUtil.THIRTY_DAY_SURVEY_ID.equals(surveyId)) {
			responseJSON.put("count", surveyCount);
			responseJSON.put("surveyGridView",
					userSurveyAnswerRepository.thirtyDaySurveyReport(fromDate.toString(), toDate.toString(),questionIds));
			return responseJSON;
		} else if (RandomUtil.NIGHTY_DAY_SURVEY_ID.equals(surveyId)) {
			responseJSON.put("count", surveyCount);
			responseJSON.put("surveyGridView", getNintyDaySurveyResponse(fromDate.toString(), toDate.toString()));
			return responseJSON;
		} else
			throw new HillromException(ExceptionConstants.HR_801);
	}

	private List<NintyDaySurveyReportVO> getNintyDaySurveyResponse(String fromDateTime, String toDateTime) {
		Map<Long, List<SurveyAnswerResultSetVO>> nintyDaysRSGroupedByUserID = (Map<Long, List<SurveyAnswerResultSetVO>>) userSurveyAnswerRepository
				.nintyDaySurveyReport(fromDateTime, toDateTime).stream()
				.collect(Collectors.groupingBy(SurveyAnswerResultSetVO::getUserId));

		List<NintyDaySurveyReportVO> nintyDaySurveyReportVOs = new LinkedList<>();
		NintyDaySurveyReportVO nintyDaySurveyReportVO;

		List<SurveyAnswerResultSetVO> groupedNintyDaysResultSetVO;

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
	
	public List<FiveDayViewVO> getSurveyAnswerByQuestionId(Long id, LocalDate from,LocalDate to) throws HillromException {

		SurveyQuestion surveyQuestion = surveyQuestionRepository.findOne(id);
		if (Objects.isNull(surveyQuestion))
			throw new HillromException(ExceptionConstants.HR_807);
		Map<Long, List<SurveyAnswerResultSetVO>> surveyAnswerRSGroupedByUserID = (Map<Long, List<SurveyAnswerResultSetVO>>) userSurveyAnswerRepository
				.fiveDaySurveyViewReport(id, from.toString(), to.toString()).stream()
				.collect(Collectors.groupingBy(SurveyAnswerResultSetVO::getUserId));

		List<FiveDayViewVO> fiveDayViewVOs = new LinkedList<>();
		FiveDayViewVO fiveDayViewVO;

		List<SurveyAnswerResultSetVO> surveyAnswerResultSetVO;

		for (Long userId : surveyAnswerRSGroupedByUserID.keySet()) {
			surveyAnswerResultSetVO = surveyAnswerRSGroupedByUserID.get(userId);
			//Survey Comments needed only when answered No.
			if(Constants.YES.equals(surveyAnswerResultSetVO.get(3).getAnswerValue()))
				continue;
			fiveDayViewVO = new FiveDayViewVO(
					surveyAnswerResultSetVO.get(0).getAnswerValue(),
					surveyAnswerResultSetVO.get(1).getAnswerValue(),
					surveyAnswerResultSetVO.get(2).getAnswerValue(),
					surveyAnswerResultSetVO.get(3).getAnswerValue2());
			fiveDayViewVOs.add(fiveDayViewVO);
		}
		return fiveDayViewVOs;
	
	}

	public SurveyGraph getSurveyGraphById(Long surveyId,LocalDate from,LocalDate to) throws HillromException{
		List<Long> questionIds = getSurveyQuestionIdsBySurveyId(surveyId);
		Filter filter = new Filter();
		filter.setFrom(from);
		filter.setTo(to);
		Integer surveyCount = userSurveyAnswerRepository.findSurveyCountByDateRange(surveyId,
				from.toDateTime(LocalTime.MIDNIGHT), to.plusDays(1).toDateTime(LocalTime.MIDNIGHT));
		Map<String,Object> surveyStatistics = new HashMap<>();
		surveyStatistics.put(Constants.KEY_COUNT,surveyCount);
		if(surveyId == Constants.FIVE_DAYS_SURVEY_ID){
			List<FiveDaySurveyReportVO> fiveDaysSurveyReport = userSurveyAnswerRepository.fiveDaySurveyReport(from.toString(), to.toString(),
					questionIds.subList(0, questionIds.size()-1));
			surveyStatistics.put(Constants.KEY_FIVE_DAYS_SURVEY_REPORT, fiveDaysSurveyReport);
			return (SurveyGraph) graphService.populateGraphData(surveyStatistics, filter);
		}else{
			List<ThirtyDaySurveyReportVO> thirtyDaysSurveyReport = userSurveyAnswerRepository.thirtyDaySurveyReport(from.toString(), to.toString(),
					questionIds);
			surveyStatistics.put(Constants.KEY_THIRTY_DAYS_SURVEY_REPORT, thirtyDaysSurveyReport);
			return (SurveyGraph) graphService.populateGraphData(surveyStatistics, filter);
		}
	}
	
	public List<Long> getSurveyQuestionIdsBySurveyId(Long id){
		String questionIdsCSV = Constants.FIVE_DAYS_SURVEY_QIDS;
		List<Long> questionIds = new LinkedList<>();
		if(!Constants.FIVE_DAYS_SURVEY_ID.equals(id)){
			questionIdsCSV = Constants.THIRTY_DAYS_SURVEY_QIDS;
		}
		for(String qid : questionIdsCSV.split(",")){
			questionIds.add(Long.parseLong(qid));
		}
		return questionIds;
	}
}
