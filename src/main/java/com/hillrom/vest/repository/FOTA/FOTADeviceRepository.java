package com.hillrom.vest.repository.FOTA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.FOTA.FOTADeviceFWareUpdate;

public interface FOTADeviceRepository extends JpaRepository<FOTADeviceFWareUpdate, Long> {
	@Query(nativeQuery=true,value= "SELECT * from FOTA_DEVICE_FWARE_UPDATE_LOG where device_serial_number =:deviceSerialNumber")
	FOTADeviceFWareUpdate getFOTADeviceFWwareDetailsByDevSN(
			@Param("deviceSerialNumber") String deviceSerialNumber);
}
