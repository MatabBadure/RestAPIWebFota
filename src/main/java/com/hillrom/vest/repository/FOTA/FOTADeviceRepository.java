package com.hillrom.vest.repository.FOTA;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.FOTA.FOTADeviceFWareUpdate;

public interface FOTADeviceRepository extends JpaRepository<FOTADeviceFWareUpdate, Long> {

	@Query(nativeQuery=true,value= "SELECT * from FOTA_DEVICE_FWARE_UPDATE_LOG where downloaded_status =:status")
	List<FOTADeviceFWareUpdate> getFOTADeviceListByStatus(@Param("status") String status);
	
	@Query(nativeQuery=true,value= "SELECT * from FOTA_DEVICE_FWARE_UPDATE_LOG where downloaded_status IN (:statusSuccess,:statusFailure")
	List<FOTADeviceFWareUpdate> getFOTADeviceListByAll(@Param("statusSuccess")String statusSuccess, @Param("statusFailure")	String statusFailure);
	

}
