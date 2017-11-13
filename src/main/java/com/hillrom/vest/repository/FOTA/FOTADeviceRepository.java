package com.hillrom.vest.repository.FOTA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.FOTA.FOTADeviceFWareUpdate;

public interface FOTADeviceRepository extends JpaRepository<FOTADeviceFWareUpdate, Long> {
	@Query(nativeQuery=true,value= "SELECT * from FOTA_DEVICE_FWARE_UPDATE_LOG where device_serial_number =:deviceSerialNumber")
	FOTADeviceFWareUpdate getFOTADeviceFWwareDetailsByDevSN(
			@Param("deviceSerialNumber") String deviceSerialNumber);
/*
	@Query(nativeQuery=true,value= "SELECT * from FOTA_DEVICE_FWARE_UPDATE_LOG d, FOTA_INFO f where d.downloaded_status =:status and lower(f.device_part_number) like lower(:queryString) and d.fota_info_id = f.id")
	List<Object> getFOTADeviceListByStatus(@Param("status") String status,@Param("queryString") String queryString);
	
	@Query(nativeQuery=true,value= "SELECT * from FOTA_DEVICE_FWARE_UPDATE_LOGd, FOTA_INFO f where d.downloaded_status IN (:statusSuccess,:statusFailure,:statusAborted) and ")
	List<Object> getFOTADeviceListByAll(@Param("statusSuccess")String statusSuccess, @Param("statusFailure")	String statusFailure,@Param("statusAborted")String statusAborted,@Param("queryString") String queryString);
	*/

}
