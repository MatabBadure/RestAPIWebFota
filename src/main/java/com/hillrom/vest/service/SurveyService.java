package com.hillrom.vest.service;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

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
	private UserSurveyAnswerRepository  userSurveyAnswerRepository;
	
	@Inject
	private UserRepository  userRepository;
	
	@Inject
	private SurveyQuestionRepository  surveyQuestionRepository;
	
	

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
//		if(Objects.isNull(userSurveyAnswerDTO.getUserSurveyAnswer()) )
//				|| userSurveyAnswerDTO.getUserSurveyAnswer().isEmpty())
//			throw new HillromException(ExceptionConstants.HR_802);
		
		User user = userRepository.findOne(userSurveyAnswerDTO.getUserId());
		if(Objects.isNull(user))
			throw new HillromException(ExceptionConstants.HR_512);

		Survey survey = surveyRepository.getOne((userSurveyAnswerDTO.getSurveyId()));
		if(Objects.isNull(survey))
			throw new HillromException(ExceptionConstants.HR_801);
		
		List<UserSurveyAnswer> userSurveyAnswers = userSurveyAnswerDTO.getUserSurveyAnswer();
		for(UserSurveyAnswer userSurveyAnswer : userSurveyAnswers){
			userSurveyAnswer.setUser(user);
			userSurveyAnswer.setSurvey(survey);
		}
		userSurveyAnswerRepository.save(userSurveyAnswers);
	}
	
	public UserSurveyAnswer getSurveyAnswerById(Long id) throws HillromException {
		
		UserSurveyAnswer userSurveyAnswer = userSurveyAnswerRepository.findOne(id);
		UserSurveyAnswerDTO userSurveyAnswerDTO = new UserSurveyAnswerDTO();
		userSurveyAnswerDTO.setSurveyId(userSurveyAnswer.getSurvey().getId());
		userSurveyAnswerDTO.setUserId(userSurveyAnswer.getUser().getId());
		userSurveyAnswerDTO.getUserSurveyAnswer().add(userSurveyAnswer);
		if(Objects.isNull(userSurveyAnswer))
			throw new HillromException(ExceptionConstants.HR_512);
		return userSurveyAnswer;
	}
}
