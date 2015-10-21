package com.hillrom.vest.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.SecurityQuestion;
import com.hillrom.vest.domain.UserSecurityQuestion;
import com.hillrom.vest.repository.SecurityQuestionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSecurityQuestionRepository;

@Service
@Transactional
public class UserSecurityQuestionService {

	@Inject
	private UserSecurityQuestionRepository userSecurityQuestionRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private SecurityQuestionRepository questionRepository;
	
	public List<UserSecurityQuestion> findByUserId(Long userId){
		return userSecurityQuestionRepository.findByUserId(userId);
	}
	
	public Optional<UserSecurityQuestion> findOneByQuestionId(Long questionId){
		return userSecurityQuestionRepository.findOneByQuestionId(questionId);
	}
	
	public Optional<UserSecurityQuestion> findOneByUserIdAndQuestionId(Long userId,Long questionId){
		return userSecurityQuestionRepository.findOneByUserIdAndQuestionId(userId,questionId);
	}
	
	public Optional<UserSecurityQuestion> saveOrUpdate(Long userId,Long questionId,String answer){
		if(null == userId || null == questionId || null == answer)
			return Optional.empty();
		Optional<UserSecurityQuestion> fromDatabase = findOneByUserIdAndQuestionId(userId, questionId);
		if(fromDatabase.isPresent()){
			UserSecurityQuestion userSecQ = fromDatabase.get(); 
			userSecQ.setAnswer(answer);
			return Optional.of(userSecurityQuestionRepository.save(userSecQ));
		}else{
			UserSecurityQuestion userSecurityQuestion = new  UserSecurityQuestion();
			userSecurityQuestion.setAnswer(answer);
			SecurityQuestion question = questionRepository.findOne(questionId);
			if(null == question){
				return Optional.empty();
			}
			userSecurityQuestion.setSecurityQuestion(question);
			userSecurityQuestion.setUser(userRepository.findOne(userId));
			return Optional.of(userSecurityQuestionRepository.save(userSecurityQuestion));						
		}
	}

}
