package com.hillrom.vest.service.util.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.HANDLE_RAW;

import java.util.Map;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.FOTA.FOTADeviceFWareUpdate;
import com.hillrom.vest.repository.FOTA.FOTADeviceRepository;
import com.hillrom.vest.web.rest.FOTA.dto.HandleHolder;

@Service
@Transactional
public class FOTAServiceUtil {
	private static final Logger log = LoggerFactory
			.getLogger(FOTAServiceUtil.class);

	@Inject
	private FOTADeviceRepository fotaDeviceRepository;
	
	private CommonFOTAUtil  coUtil = new CommonFOTAUtil();
	
	public void saveDeviceDetails(String handleId, String status,
			String rawMessage, Map<String, HandleHolder> handleHolderBin) {
		//Get handle from request
		handleId = coUtil.getValuesFromRequest(rawMessage,HANDLE_RAW);
		log.debug("handleId from Request:" + handleId);
		//Initially 
		HandleHolder holder = new HandleHolder();
		holder = handleHolderBin.get(handleId);
		FOTADeviceFWareUpdate fotaDeviceFWareUpdate = new FOTADeviceFWareUpdate();
		//Get FOTADevice 
		fotaDeviceFWareUpdate = fotaDeviceRepository.getFOTADeviceFWwareDetailsByDevSN(holder.getDeviceSerialNumber());
		if(fotaDeviceFWareUpdate != null){
			fotaDeviceFWareUpdate.setDownloadStartDateTime(holder.getDownloadStartDateTime());
			fotaDeviceFWareUpdate.setDownloadEndDateTime(new DateTime());
			fotaDeviceFWareUpdate.setDownloadStatus(status);
			fotaDeviceRepository.save(fotaDeviceFWareUpdate);
		}
	}
	
	public void saveInprogressDeviceDetails(HandleHolder holder) {
		try{
			FOTADeviceFWareUpdate fotaDeviceFWareUpdate = new FOTADeviceFWareUpdate();
				fotaDeviceFWareUpdate.setFotaInfoId(holder.getFotaInfoId());
				fotaDeviceFWareUpdate.setDeviceSerialNumber(holder.getDeviceSerialNumber());
				fotaDeviceFWareUpdate.setDeviceSoftVersion(holder.getSoftwareVersion());
				fotaDeviceFWareUpdate.setUpdatedSoftVersion(holder.getUpdatedSoftVersion());
				fotaDeviceFWareUpdate.setDeviceSoftwareDateTime(holder.getDeviceSoftwareDateTime());
				fotaDeviceFWareUpdate.setCheckupdateDateTime(holder.getCheckupdateDateTime());
				fotaDeviceFWareUpdate.setConnectionType(holder.getConnectionType());
				fotaDeviceFWareUpdate.setDownloadStatus("In progress");
				fotaDeviceRepository.save(fotaDeviceFWareUpdate);	
			
		}catch(Exception ex){
		log.error(ex.getMessage());
		ex.printStackTrace();
		}
	}

}
