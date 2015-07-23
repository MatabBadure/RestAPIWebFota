package com.hillrom.vest.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.hillrom.vest.repository.SearchCriteria;
import com.hillrom.vest.repository.SearchRepository;

@Service
public class SearchService<T> {
	
	private static final String STR = "%";
	@Inject
	private SearchRepository<T> searchRepository;
	
	public List<T> findBy(SearchCriteria<T> criteria){
		String searchString =criteria.getSearchString();
		criteria.setSearchString(new StringBuilder().append(STR).append(searchString).append(STR).toString());
		return searchRepository.findBy(criteria);
	}

	
}
