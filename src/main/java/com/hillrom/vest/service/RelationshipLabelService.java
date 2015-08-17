package com.hillrom.vest.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.RelationshipLabel;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.RelationshipLabelRepository;
import com.hillrom.vest.util.ExceptionConstants;

/**
 * Service class for managing relationship labels.
 */
@Service
@Transactional
public class RelationshipLabelService {
	
	@Inject
	private RelationshipLabelRepository relationshipLabelRepository;

	public List<String> getRelationshipLabels() throws HillromException{
		List<RelationshipLabel> relationshipLabelList = relationshipLabelRepository.findAll();
		if(relationshipLabelList.isEmpty()){
			throw new HillromException(ExceptionConstants.HR_565);
		} else {
			List<String> relationshipLabels = new LinkedList<>();
			relationshipLabelList.stream().forEach(relationshipLabel -> {
				relationshipLabels.add(relationshipLabel.getName());
	    	});
			return relationshipLabels;
		}
	}
}
