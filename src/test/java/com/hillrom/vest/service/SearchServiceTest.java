package com.hillrom.vest.service;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.repository.SearchCriteria;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class SearchServiceTest {

	@Inject
	private SearchService<Clinic> clinicSearchService;
	
	@Test
	public void testClinicSearch(){
		SearchCriteria<Clinic> criteria = new SearchCriteria<>(Clinic.class, "%Manipal%", 0, 10);
		//criteria.setQuery("Select clinic from Clinic clinic where clinic.name LIKE "+criteria.getSearchString()+" or clinic.address LIKE "+criteria.getSearchString()+" or clinic.city LIKE "+criteria.getSearchString());
		System.out.println(clinicSearchService.findBy(criteria));
		
	}
}
