package com.hillrom.vest.repository.FOTA;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.FOTA.FOTAInfo;

public interface FOTARepository extends JpaRepository<FOTAInfo, Long>{

	/*@Query(nativeQuery=true,value= " from FOTAInfo where partNumber = ?1 and isOldFile = ?2 ")
	FOTAInfo findOneById(@Param("partNo") String partNumber, @Param("isOldFile") boolean isOldFile);*/
	
	@Query(nativeQuery=true,value= "SELECT * from FOTA_INFO where device_part_number = :partNoV and soft_delete_flag = :active and active_published_flag = :pending")
	FOTAInfo FOTAByPartNumber(@Param("partNoV") String partNoV, @Param("active") boolean active, @Param("pending") boolean pending);

	@Query(nativeQuery=true,value= "SELECT * from FOTA_INFO where device_part_number = :partNoD and old_soft_flag = :isOldFileD")
	FOTAInfo findFOTAInfo(@Param("partNoD") String partNoD, @Param("isOldFileD") boolean isOldFileD);
	
	@Query(nativeQuery=true,value= "SELECT * from FOTA_INFO where old_soft_flag =:success")
	List<FOTAInfo> getFOTAListByStatus(@Param("success") boolean success);

	@Query(nativeQuery=true,value= "SELECT * from FOTA_INFO where old_soft_flag =:success and old_soft_flag =:failure")
	List<FOTAInfo> getFOTAListByStatus(@Param("success")boolean success, @Param("failure")boolean failure);
}
