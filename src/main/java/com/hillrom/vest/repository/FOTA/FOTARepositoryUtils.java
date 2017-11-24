package com.hillrom.vest.repository.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.ABORTED_LIST;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_QUERYSTR;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_QUERYSTR1;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_QUERYSTR2;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_QUERYSTR3;
import static com.hillrom.vest.config.FOTA.FOTAConstants.DEVICE_QUERYSTR4;
import static com.hillrom.vest.config.FOTA.FOTAConstants.FAILURE_LIST;
import static com.hillrom.vest.config.FOTA.FOTAConstants.FOTA_ADMIN;
import static com.hillrom.vest.config.FOTA.FOTAConstants.FOTA_APPROVER;
import static com.hillrom.vest.config.FOTA.FOTAConstants.SUCCESS_LIST;
import static com.hillrom.vest.config.FOTA.FOTAConstants.INPROGRESS_LIST;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class FOTARepositoryUtils {
	private final Logger log = LoggerFactory
			.getLogger(FOTARepositoryUtils.class);
	@Inject
	private EntityManager entityManager;

	public List<Object[]> getFOATUsers() {
		String queryStr = "SELECT u.email, u.last_name FROM USER_AUTHORITY a, USER u where a.authority_name IN ('"
				+ FOTA_APPROVER
				+ "','"
				+ FOTA_ADMIN
				+ "') and a.user_id = u.id";
		Query jpaQuery = entityManager.createNativeQuery(queryStr);
		List<Object[]> resultList = jpaQuery.getResultList();
		log.debug("User list:" + resultList);
		return resultList;
	}

	public List<Object[]> getFOTAAprUsers() {
		String queryStr = "SELECT u.email, u.last_name FROM USER_AUTHORITY a, USER u where a.authority_name = '"
				+ FOTA_APPROVER + "' and a.user_id = u.id";
		Query jpaQuery = entityManager.createNativeQuery(queryStr);

		List<Object[]> resultList = jpaQuery.getResultList();
		return resultList;
	}

	public List<Object[]> getAllList(String status, String queryString,
			String sortBy, boolean isAscending) {
		//SUCCESS_LIST, FAILURE_LIST, ABORTED_LIST
		String queryStr = "";
		String query = getAllDeviceQueryStr(queryString);
		if(sortBy.equals("")&& isAscending == false){
			queryStr = query+" order by d.id desc";
		}else if(sortBy.equals("partNumber")){
			if(isAscending){
				queryStr = query+" order by f.device_part_number desc";
			}else{
				queryStr = query+" order by f.device_part_number asc";
			}
		}else if(sortBy.equals("productName")){
			if(isAscending){
				queryStr = query+" order by f.product_Type desc";
			}else{
				queryStr = query+" order by f.product_Type asc";
			}
		}
		else if(sortBy.equals("serialNumber")){
			if(isAscending){
				queryStr = query+" order by d.device_serial_number desc";
			}else{
				queryStr = query+" order by d.device_serial_number asc";
			}
		}else if(sortBy.equals("connectionType")){
			if(isAscending){
				queryStr = query+" order by d.connection_type desc";
			}else{
				queryStr = query+" order by d.connection_type asc";
			}
		}else if(sortBy.equals("startDatetime")){
			if(isAscending){
				queryStr = query+" order by d.download_start_date_time desc";
			}else{
				queryStr = query+" order by d.download_start_date_time asc";
			}
		}else if(sortBy.equals("endDateTime")){
			if(isAscending){
				queryStr = query+" order by d.download_end_date_time desc";
			}else{
				queryStr = query+" order by d.download_end_date_time asc";
			}
		}else if(sortBy.equals("status")){
			if(isAscending){
				queryStr = query+" order by d.downloaded_status desc";
			}else{
				queryStr = query+" order by d.downloaded_status asc";
			}
		}else if(sortBy.equals("downloadTime")){
				queryStr = query;
		}

		Query jpaQuery = entityManager.createNativeQuery(queryStr);
		
		List<Object[]> resultList = jpaQuery.getResultList();
		return resultList;
	}

	
	private String getAllDeviceQueryStr(String queryString) {
		queryString = "SELECT d.id,d.fota_info_id,d.device_serial_number,d.connection_type,d.device_software_version,d.device_software_date_time,d.updated_software_version,d.checkupdate_date_time,d.download_start_date_time,d.download_end_date_time,d.downloaded_status,f.device_part_number,f.product_Type from FOTA_DEVICE_FWARE_UPDATE_LOG d, FOTA_INFO f where (d.downloaded_status in ('"+SUCCESS_LIST+"','"+INPROGRESS_LIST+"','"+FAILURE_LIST+"','"+ABORTED_LIST+"') and lower(f.device_part_number) like lower("+queryString+") and d.fota_info_id = f.id) or (d.downloaded_status in ('"+SUCCESS_LIST+"','"+FAILURE_LIST+"','"+ABORTED_LIST+"') and lower(f.product_type) like lower("+queryString+") and d.fota_info_id = f.id)";
		return queryString;
	}

	public List<Object[]> getDeviceListByStatus(String status, String queryString,
			String sortBy, boolean isAscending) {
		String queryStr = "";
		String query = getDeviceQueryStr(status,queryString);
		if(sortBy.equals("")&& isAscending == false){
			queryStr =	query+" order by d.id desc";
		}else if(sortBy.equals("partNumber")){
			if(isAscending){
				queryStr =	query+" order by f.device_part_number desc";
			}else{
				queryStr =	query+" order by f.device_part_number asc";
			}
		}else if(sortBy.equals("productName")){
			if(isAscending){
				queryStr =	query+" order by f.product_Type desc";
			}else{
				queryStr =	query+" order by f.product_Type asc";
			}
		}
		else if(sortBy.equals("serialNumber")){
			if(isAscending){
				queryStr =	query+" order by d.device_serial_number desc";
			}else{
				queryStr =	query+" order by d.device_serial_number asc";
			}
		}else if(sortBy.equals("connectionType")){
			if(isAscending){
				queryStr =	query+" order by d.connection_type desc";
			}else{
				queryStr =	query+" order by d.connection_type asc";
			}
		}else if(sortBy.equals("startDatetime")){
			if(isAscending){
				queryStr =	query+" order by d.download_start_date_time desc";
			}else{
				queryStr =	query+" order by d.download_start_date_time asc";
			}
		}else if(sortBy.equals("endDateTime")){
			if(isAscending){
				queryStr =	query+" order by d.download_end_date_time desc";
			}else{
				queryStr =	query+" order by d.download_end_date_time asc";
			}
		}else if(sortBy.equals("status")){
			if(isAscending){
				queryStr =	query+" order by d.downloaded_status desc";
			}else{
				queryStr =	query+" order by d.downloaded_status asc";
			}
		}else if(sortBy.equals("downloadTime")){
			queryStr =	query;
		}
		Query jpaQuery = entityManager.createNativeQuery(queryStr);
		
		List<Object[]> resultList = jpaQuery.getResultList();

		return resultList;
	}

	private String getDeviceQueryStr(String status, String queryString) {
		String query = DEVICE_QUERYSTR+DEVICE_QUERYSTR1+status+DEVICE_QUERYSTR2+queryString+DEVICE_QUERYSTR4+" or "+DEVICE_QUERYSTR1+status+DEVICE_QUERYSTR3+queryString+DEVICE_QUERYSTR4;
		return query;
	}

}
