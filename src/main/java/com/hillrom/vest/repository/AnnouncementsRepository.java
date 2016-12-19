package com.hillrom.vest.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.hillrom.vest.domain.Announcements;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface AnnouncementsRepository extends JpaRepository<Announcements, Long> {
	
	 	@Query(" from Announcements announcement where id = ? and isDeleted = ? ")
	    Announcements findOneById(Long id, boolean isDeleted);
	    
	    @Query(" from Announcements announcement where isDeleted = ? ")
	    List<Announcements> findAnnouncements(boolean isDeleted);
	 
	
}
