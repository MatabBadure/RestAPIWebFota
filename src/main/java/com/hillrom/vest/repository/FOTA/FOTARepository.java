package com.hillrom.vest.repository.FOTA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.FOTA.FOTAInfo;

public interface FOTARepository extends JpaRepository<FOTAInfo, Long>{

	/*@Query(nativeQuery=true,value= " from FOTAInfo where partNumber = ?1 and isOldFile = ?2 ")
	FOTAInfo findOneById(@Param("partNo") String partNumber, @Param("isOldFile") boolean isOldFile);*/
	
	@Query(nativeQuery=true,value= "SELECT software_version from FOTA_INFO where device_part_number = :partNoV and old_soft_flag = :isOldFileV")
	String findOneById(@Param("partNoV") String partNoV, @Param("isOldFileV") boolean isOldFileV);

	@Query(nativeQuery=true,value= "SELECT * from FOTA_INFO where device_part_number = :partNoD and old_soft_flag = :isOldFileD")
	FOTAInfo findFOTAInfo(@Param("partNoD") String partNoD, @Param("isOldFileD") boolean isOldFileD);
}
