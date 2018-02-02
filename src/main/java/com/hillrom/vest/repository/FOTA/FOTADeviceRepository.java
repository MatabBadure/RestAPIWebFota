package com.hillrom.vest.repository.FOTA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.FOTA.FOTADeviceFWareUpdate;

public interface FOTADeviceRepository extends JpaRepository<FOTADeviceFWareUpdate, Long> {
	/*@Query(nativeQuery=true,value= "SELECT * FROM FOTA_DEVICE_FWARE_UPDATE_LOG where device_serial_number =:deviceSerialNumber AND downloaded_status =:inprogressList AND fota_info_id =:fotaInfoId")
	FOTADeviceFWareUpdate getFOTADeviceFWwareDetailsByDevSN(
			@Param("deviceSerialNumber") String deviceSerialNumber, @Param("inprogressList") String inprogressList,@Param("fotaInfoId") Long fotaInfoId);*/
	@Query(nativeQuery=true,value= "SELECT * FROM FOTA_DEVICE_FWARE_UPDATE_LOG where id =:deviceId AND downloaded_status =:inprogressList")
	FOTADeviceFWareUpdate getFOTADeviceFWwareDetailsByDevSN(
			@Param("deviceId") Long deviceId, @Param("inprogressList") String inprogressList);
}
