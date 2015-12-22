package com.hillrom.vest.service;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.SecurityQuestion;
import com.hillrom.vest.domain.UserSecurityQuestion;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.SecurityQuestionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSecurityQuestionRepository;
import com.hillrom.vest.util.ExceptionConstants;

@Service
@Transactional
public class UserSecurityQuestionService {

	@Inject
	private UserSecurityQuestionRepository userSecurityQuestionRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private SecurityQuestionRepository questionRepository;
	
	public Optional<UserSecurityQuestion> findByUserId(Long userId){
		return userSecurityQuestionRepository.findByUserId(userId);
	}
	
	public Optional<UserSecurityQuestion> findOneByQuestionId(Long questionId){
		return userSecurityQuestionRepository.findOneByQuestionId(questionId);
	}
	
	public Optional<UserSecurityQuestion> findOneByUserIdAndQuestionId(Long userId,Long questionId){
		return userSecurityQuestionRepository.findOneByUserIdAndQuestionId(userId,questionId);
	}
	
	public Optional<UserSecurityQuestion> saveOrUpdate(Long userId,Long questionId,String answer) throws HillromException{
		if(Objects.isNull(userId) || Objects.isNull(questionId) || Objects.isNull(answer))
			throw new HillromException(ExceptionConstants.HR_502);
		
		SecurityQuestion question = questionRepository.findOne(questionId);
		if(Objects.isNull(question)){
			throw new HillromException(ExceptionConstants.HR_557);
		}
		
		UserSecurityQuestion userSecurityQuestion;
		Optional<UserSecurityQuestion> fromDatabase = findByUserId(userId);
		if(fromDatabase.isPresent()){
			userSecurityQuestion = fromDatabase.get(); 
			userSecurityQuestion.setSecurityQuestion(question);
			userSecurityQuestion.setAnswer(answer);
			return Optional.of(userSecurityQuestionRepository.save(userSecurityQuestion));
		} else {
			userSecurityQuestion = new  UserSecurityQuestion();
			userSecurityQuestion.setAnswer(answer);
			userSecurityQuestion.setSecurityQuestion(question);
			userSecurityQuestion.setUser(userRepository.findOne(userId));
			return Optional.of(userSecurityQuestionRepository.save(userSecurityQuestion));
		}
	}
}
