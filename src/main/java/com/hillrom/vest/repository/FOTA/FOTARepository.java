package com.hillrom.vest.repository.FOTA;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.FOTA.FOTAInfo;

public interface FOTARepository extends JpaRepository<FOTAInfo, Long>{

	@Query("from FOTAInfo where id = ?1")
	FOTAInfo findOneById (Long id);
	
	@Query(nativeQuery=true,value= "SELECT * from FOTA_INFO where device_part_number = :partNoV and soft_delete_flag = :active and active_published_flag = :pending")
	FOTAInfo FOTAByPartNumber(@Param("partNoV") String partNoV, @Param("active") boolean active, @Param("pending") boolean pending);
	
	@Query(nativeQuery=true,value= "SELECT * from FOTA_INFO where ((soft_delete_flag =:softDeleteFlag and active_published_flag =:activePublishedFlag and delete_request_flag =:deleteRequest and lower(device_part_number) like lower(:queryString))or (soft_delete_flag =:softDeleteFlag and active_published_flag =:activePublishedFlag and delete_request_flag =:deleteRequest and lower(product_type) like lower(:queryString))) or ((soft_delete_flag =:softDeleteFlag1 and active_published_flag =:activePublishedFlag1 and delete_request_flag =:deleteRequest1 and lower(device_part_number) like lower(:queryString)) or (soft_delete_flag =:softDeleteFlag1 and active_published_flag =:activePublishedFlag1 and delete_request_flag =:deleteRequest1 and lower(product_type) like lower(:queryString)))")
	List<FOTAInfo> getFOTAListByPendingAndSearchStr(@Param("softDeleteFlag") boolean softDeleteFlag,@Param("activePublishedFlag") boolean activePublishedFlag, @Param("deleteRequest") boolean deleteRequest,@Param("softDeleteFlag1") boolean softDeleteFlag1,@Param("activePublishedFlag1") boolean activePublishedFlag1, @Param("deleteRequest1") boolean deleteRequest1, @Param("queryString") String queryString);

	@Query(nativeQuery=true,value= "SELECT * from FOTA_INFO where (soft_delete_flag =:softDeleteFlag and active_published_flag =:activePublishedFlag and lower(device_part_number) like lower(:queryString)) or (soft_delete_flag =:softDeleteFlag and active_published_flag =:activePublishedFlag and lower(product_type) like lower(:queryString))")
	List<FOTAInfo> getFOTAListByPublishedAndSearchStr(@Param("softDeleteFlag") boolean softDeleteFlag, @Param("activePublishedFlag") boolean activePublishedFlag, @Param("queryString") String queryString);

	@Query(nativeQuery=true,value= "SELECT * from FOTA_INFO where lower(device_part_number) like lower(:queryString) or lower(product_type) like lower(:queryString)")
	List<FOTAInfo> getFOTAListByAllAndSearchStr(@Param("queryString") String queryString);
}
