package com.hillrom.vest.repository.util;

import static com.hillrom.vest.security.AuthoritiesConstants.HCP;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;
import static com.hillrom.vest.util.RelationshipLabelConstants.SELF;

public class QueryConstants {
	

	public static String QUERY_PATIENT_SEARCH_UNDER_HCP_USER_WHERE_CLAUSE_FOR_MONARCH = " and (lower(user.first_name) "

			+ " like lower(:queryString) or lower(user.last_name) like lower(:queryString) "
			+ " or lower(user.email) like lower(:queryString) or lower(CONCAT(user.first_name,' ',user.last_name)) "
			+ " like lower(:queryString) or lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) "
			+ " or lower(user.hillrom_id) like lower(:queryString) or lower(IFNULL(patient_clinic.mrn_id,0)) like lower(:queryString)) ";
	
	public static String QUERY_PATIENT_SEARCH_UNDER_HCP_USER_WHERE_CLAUSE_FOR_VEST = " and "

			+ " patient_dev_assoc.ptype in ('VEST','ALL') "

			+ " and (lower(user.first_name) "
			+ " like lower(:queryString) or lower(user.last_name) like lower(:queryString) "
			+ " or lower(user.email) like lower(:queryString) or lower(CONCAT(user.first_name,' ',user.last_name)) "
			+ " like lower(:queryString) or lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) "
			+ " or lower(user.hillrom_id) like lower(:queryString) or lower(IFNULL(patient_clinic.mrn_id,0)) like lower(:queryString)) ";
	
	
	public static String QUERY_PATIENT_SEARCH_UNDER_HCP_USER_FOR_VEST = " select user.id,user.email,user.first_name as firstName,user.last_name as lastName, "
			+ " IF(user.is_deleted=true,1,IF(patient_clinic.is_active=true,0,IF(patient_clinic.is_active = NULL,user.is_deleted,1))) as isDeleted, user.zipcode,patInfo.address,patInfo.city,user.dob as patientDoB,"
			+ " user.gender,user.title,user.hillrom_id,user.created_date as createdAt,"
			+ " user.activated as isActivated, patInfo.state as state, pc.compliance_score adherence,"
			+ " pc.last_therapy_session_date as last_date," + " (select  GROUP_CONCAT(clinicc.name)"
			+ " from USER userc "
			+ " left outer join USER_AUTHORITY user_authorityc on user_authorityc.user_id = userc.id  "
			+ "and user_authorityc.authority_name = 'PATIENT' "
			+ " join USER_PATIENT_ASSOC  upac on userc.id = upac.user_id and upac.relation_label = 'Self' "
			+ " left outer join PATIENT_INFO patInfoc on upac.patient_id = patInfoc.id "
			+ " left outer join CLINIC_PATIENT_ASSOC user_clinicc on user_clinicc.patient_id = patInfoc.id  "
			+ " left outer join CLINIC clinicc on user_clinicc.clinic_id = clinicc.id and user_clinicc.patient_id = patInfoc.id "
			+ " where upac.user_id = user.id "
			+ " group by patInfoc.id) as clinicname, (select  GROUP_CONCAT(userh.first_name,' ', userh.last_name) "
			+ " from USER userh "
			+ " left outer join USER_AUTHORITY user_authorityh on user_authorityh.user_id = userh.id  "
			+ "and user_authorityh.authority_name = 'HCP' "
			+ " join USER_PATIENT_ASSOC  upah on userh.id = upah.user_id and upah.relation_label = 'HCP' "
			+ " left outer join PATIENT_INFO patInfoh on upah.patient_id = patInfoh.id "
			+ " where patInfo.id = patInfoh.id"
			+ " group by patInfoh.id) as hcpname, patient_clinic.mrn_id as mrnid,"
			+ " patient_clinic.expired as isExpired, pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated,"
			+ " pc.missed_therapy_count as isMissedTherapy,clinic.adherence_setting as adherencesetting,"

			+ " patient_dev_assoc.ptype as devType from USER user"

			+ " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '" + SELF + "'"
			+ " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id"
			+ " join USER_PATIENT_ASSOC upa_hcp on patInfo.id = upa_hcp.patient_id  "
			+ " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate())  "
			+ " left outer join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id "
			+ " left outer join CLINIC clinic on patient_clinic.clinic_id = clinic.id "
			+ " left outer join USER_AUTHORITY user_authority on user_authority.user_id = user.id"
			+ " and user_authority.authority_name = '" + PATIENT + "' "

			+ " left outer join " 
			+ " (SELECT pda.*, IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
			+ "	from "
			+ "	PATIENT_DEVICES_ASSOC pda "  
			+ "	where "
			+ " is_active=1 group by patient_id) patient_dev_assoc on patient_dev_assoc.patient_id = patInfo.id ";

	
	public static String QUERY_PATIENT_SEARCH_UNDER_HCP_USER_FOR_MONARCH = " select user.id,user.email,user.first_name as firstName,user.last_name as lastName, "
			+ " IF(user.is_deleted=true,1,IF(patient_clinic.is_active=true,0,IF(patient_clinic.is_active = NULL,user.is_deleted,1))) as isDeleted, user.zipcode,patInfo.address,patInfo.city,user.dob as patientDoB,"
			+ " user.gender,user.title,user.hillrom_id,user.created_date as createdAt,"
			+ " user.activated as isActivated, patInfo.state as state, pc.compliance_score adherence,"
			+ " pc.last_therapy_session_date as last_date," + " (select  GROUP_CONCAT(clinicc.name)"
			+ " from USER userc "
			+ " left outer join USER_AUTHORITY user_authorityc on user_authorityc.user_id = userc.id  "
			+ "and user_authorityc.authority_name = 'PATIENT' "
			+ " join USER_PATIENT_ASSOC  upac on userc.id = upac.user_id and upac.relation_label = 'Self' "
			+ " left outer join PATIENT_INFO patInfoc on upac.patient_id = patInfoc.id "
			+ " left outer join CLINIC_PATIENT_ASSOC user_clinicc on user_clinicc.patient_id = patInfoc.id  "
			+ " left outer join CLINIC clinicc on user_clinicc.clinic_id = clinicc.id and user_clinicc.patient_id = patInfoc.id "
			+ " where upac.user_id = user.id "
			+ " group by patInfoc.id) as clinicname, (select  GROUP_CONCAT(userh.first_name,' ', userh.last_name) "
			+ " from USER userh "
			+ " left outer join USER_AUTHORITY user_authorityh on user_authorityh.user_id = userh.id  "
			+ " and user_authorityh.authority_name = 'HCP' "
			+ " join USER_PATIENT_ASSOC  upah on userh.id = upah.user_id and upah.relation_label = 'HCP' "
			+ " left outer join PATIENT_INFO patInfoh on upah.patient_id = patInfoh.id "
			+ " where patInfo.id = patInfoh.id"
			+ " group by patInfoh.id) as hcpname, patient_clinic.mrn_id as mrnid,"
			+ " patient_clinic.expired as isExpired, pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated,"
			+ " pc.missed_therapy_count as isMissedTherapy,clinic.adherence_setting as adherencesetting,"

			+ " patient_dev_assoc.ptype as devType from USER user"

			+ " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '" + SELF + "'"
			+ " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id"
			+ " join USER_PATIENT_ASSOC upa_hcp on patInfo.id = upa_hcp.patient_id  "
			+ " left outer join PATIENT_COMPLIANCE_MONARCH pc on user.id = pc.user_id AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate())  "
			+ " left outer join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id "
			+ " left outer join CLINIC clinic on patient_clinic.clinic_id = clinic.id "
			+ " left outer join USER_AUTHORITY user_authority on user_authority.user_id = user.id"
			+ " and user_authority.authority_name = '" + PATIENT + "' "

			+ " left outer join " 
			+ " (SELECT pda.*, IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
			+ "	from "
			+ "	PATIENT_DEVICES_ASSOC pda "  
			+ "	where "
			+ " is_active=1 group by patient_id) patient_dev_assoc on patient_dev_assoc.patient_id = patInfo.id ";
	
	public static String QUERY_PATIENT_SEARCH_FOR_ALL_DEVICETYPE_HILLROM_LOGIN = "  select patient_id as id,pemail,pfirstName,plastName, "
			+ "	isDeleted,pzipcode,paddress,pcity,pdob,pgender,ptitle,"
			+ "	phillrom_id,createdAt,isActivated, state ,"
			+ "	adherence,last_date,mrnid,hName,clinicName,isExpired,"
			+ "	isHMRNonCompliant, isSettingsDeviated, isMissedTherapy,"
			+ "	adherencesetting,devType "
			+ "	from (select user.id as patient_id,user.email as pemail,"
			+ "	user.first_name as pfirstName,user.last_name as  plastName,"
			+ " user.is_deleted as isDeleted, user.zipcode as pzipcode,"
			+ " patInfo.address paddress,patInfo.city  as pcity,user.dob "
			+ " as pdob, user.gender as pgender,user.title as ptitle, "
			+ " user.hillrom_id as phillrom_id, user.created_date"
			+ " as createdAt, user.activated as isActivated, "
			+ " patInfo.state as state ,  user_clinic.mrn_id as mrnid,"
			+ " clinic.id as pclinicid,  GROUP_CONCAT(clinic.name)"
			+ " as clinicName,  user.expired as isExpired, "
			+ " pc.compliance_score as adherence,   "
			+ " pc.last_therapy_session_date as last_date, "
			+ " pc.is_hmr_compliant as isHMRNonCompliant,"
			+ " pc.is_settings_deviated as isSettingsDeviated,"
			+ " pc.missed_therapy_count as isMissedTherapy, "
			+ " clinic.adherence_setting as adherencesetting,"
			+ " patient_dev_assoc.ptype as devType "
			+ " from USER user  "
			+ " join  USER_PATIENT_ASSOC  upa on user.id = upa.user_id  and upa.relation_label = 'Self' "
			+ " join  PATIENT_INFO patInfo on upa.patient_id = patInfo.id  "
			+ " left outer join  CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id "
			+ " left outer join  "
			+ " (SELECT pda.*, IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
			+ "	from "
			+ "	PATIENT_DEVICES_ASSOC pda  "
			+ "	where "
			+ "	is_active=1 group by patient_id) patient_dev_assoc on patient_dev_assoc.patient_id = patInfo.id  "
			+ " join  USER_AUTHORITY user_authority on user_authority.user_id = user.id  and user_authority.authority_name = 'PATIENT'  "
			+ "	and (lower(user.first_name) like lower(:queryString) or  lower(user.last_name) like lower(:queryString) or  lower(user.email) "
			+ " like lower(:queryString) or  lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
			+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or  lower(user.hillrom_id) like lower(:queryString))  "
			+ " left outer join PATIENT_COMPLIANCE_MONARCH pc on user.id = pc.user_id  AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate())  "
			+ " left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and  user_clinic.patient_id = patInfo.id "
			+ " where (patient_dev_assoc.device_type = 'MONARCH' AND patient_dev_assoc.patient_type='SD')  or"
			+ " (patient_dev_assoc.patient_type='CD')  group by user.id) as associated_patient  "
			+ " left outer join  (select  GROUP_CONCAT(huser.last_name ,' ',huser.first_name ) as hName,  clinic.id as hclinicid  "
			+ " from USER huser  "
			+ " join USER_AUTHORITY user_authorityh on user_authorityh.user_id = huser.id and user_authorityh.authority_name = 'HCP'  "
			+ " left outer join CLINIC_USER_ASSOC user_clinic  on user_clinic.users_id = huser.id  "
			+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = huser.id "
			+ " left outer join PATIENT_COMPLIANCE_MONARCH pc on huser.id = pc.user_id  AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate())  "
			+ " group by clinic.id) as associated_hcp  on associated_patient.pclinicid = associated_hcp.hclinicid  "
			+ " UNION  "
			+ " select patient_id as id,pemail,pfirstName,plastName, isDeleted,pzipcode,paddress,pcity,pdob,pgender,ptitle, phillrom_id,createdAt,isActivated, "
			+ " state,adherence,last_date,mrnid,hName,clinicName,isExpired,isHMRNonCompliant,isSettingsDeviated, isMissedTherapy,adherencesetting,devType "
			+ " from  (select  user.id as patient_id,user.email as pemail,user.first_name as pfirstName,  user.last_name as plastName, user.is_deleted as"
			+ " isDeleted, user.zipcode as pzipcode,  patInfo.address paddress,patInfo.city as pcity,user.dob as pdob, user.gender as pgender,  "
			+ " user.title as ptitle,  user.hillrom_id as phillrom_id,user.created_date as createdAt,  user.activated as isActivated, patInfo.state as state ,"
			+ " user_clinic.mrn_id as mrnid,  clinic.id as pclinicid,  GROUP_CONCAT(clinic.name) as clinicName, user.expired as isExpired, "
			+ " pc.compliance_score as adherence,   pc.last_therapy_session_date  as last_date,pc.is_hmr_compliant as isHMRNonCompliant,"
			+ " pc.is_settings_deviated  as isSettingsDeviated, pc.missed_therapy_count as isMissedTherapy,  clinic.adherence_setting as adherencesetting,"
			+ " patient_dev_assoc.ptype as devType  "
			+ " from USER user "
			+ " join  USER_PATIENT_ASSOC  upa on user.id = upa.user_id  and upa.relation_label = 'Self'  "
			+ " join  PATIENT_INFO patInfo on upa.patient_id = patInfo.id  "
			+ " left outer join  CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id  "
			+ " left outer join "
			+ "	(SELECT pda.*,IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
			+ "	from "
			+ "	PATIENT_DEVICES_ASSOC pda  "
			+ "	where "
			+ "	is_active=1 group by patient_id) patient_dev_assoc on patient_dev_assoc.patient_id = patInfo.id  "
			+ " join  USER_AUTHORITY user_authority on user_authority.user_id = user.id  and user_authority.authority_name = 'PATIENT' "
			+ " and (lower(user.first_name)  like lower(:queryString) or  lower(user.last_name) like lower(:queryString) or  lower(user.email) "
			+ " like lower(:queryString) or  lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
			+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or  lower(user.hillrom_id) like lower(:queryString)) "
			+ " left outer join  PATIENT_COMPLIANCE pc on user.id = pc.user_id AND  pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate())"
			+ " left outer join  CLINIC clinic on user_clinic.clinic_id = clinic.id and  user_clinic.patient_id = patInfo.id  "
			+ " where (patient_dev_assoc.device_type = 'VEST' AND patient_dev_assoc.patient_type='SD')   group by user.id) as associated_patient  "
			+ " left outer join (select  GROUP_CONCAT(huser.last_name ,' ',huser.first_name ) as hName,  clinic.id as hclinicid  "
			+ " from USER huser	 "
			+ " join USER_AUTHORITY user_authorityh on user_authorityh.user_id = huser.id  and user_authorityh.authority_name = 'HCP' "
			+ " left outer join	 CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = huser.id	 "
			+ " left outer join	 CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = huser.id "
			+ " left outer join	 PATIENT_COMPLIANCE pc on huser.id = pc.user_id AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate())	"
			+ " group by clinic.id) as associated_hcp on associated_patient.pclinicid = associated_hcp.hclinicid ";
	
	public static String QUERY_PATIENT_SEARCH_FOR_ALL_DEVICETYPE_CLINIC_ADMIN_LOGIN 
			= "  select patient_id as id,pemail,pfirstName,plastName, isDeleted,pzipcode,paddress,pcity,pdob,pgender,ptitle,"
				+" phillrom_id,createdAt,isActivated, state , adherence,last_date,mrnid,hName,clinicName,isExpired,isHMRNonCompliant,"
				+" isSettingsDeviated, isMissedTherapy,adherencesetting,devType	from (select user.id as patient_id,user.email as pemail,"
				+" user.first_name as pfirstName,user.last_name as "
				+" plastName, user.is_deleted as isDeleted, user.zipcode as pzipcode,patInfo.address paddress,patInfo.city " 
				+" as pcity,user.dob as pdob, user.gender as pgender,user.title as ptitle,  user.hillrom_id as phillrom_id,"
				+" user.created_date as createdAt, user.activated as isActivated, patInfo.state as state , "
				+" user_clinic.mrn_id as mrnid, clinic.id as pclinicid,  GROUP_CONCAT(clinic.name) as clinicName, "
				+" user.expired as isExpired, pc.compliance_score as adherence,   pc.last_therapy_session_date as last_date, "
				+" pc.is_hmr_compliant as isHMRNonCompliant, pc.is_settings_deviated as isSettingsDeviated, "

				+" pc.missed_therapy_count as isMissedTherapy, clinic.adherence_setting as adherencesetting,patient_dev_assoc.ptype as devType " 

				+" from USER user "
				+" join " 
				+" USER_PATIENT_ASSOC  upa on user.id = upa.user_id  and upa.relation_label = 'Self' join "
				+" PATIENT_INFO patInfo on upa.patient_id = patInfo.id "  
				+" left outer join "
				+" CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id " 

				+ " left outer join " 
				+ " (SELECT pda.*, IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
				+ "	from "
				+ "	PATIENT_DEVICES_ASSOC pda "  
				+ "	where "
				+ " is_active=1 group by patient_id) patient_dev_assoc on patient_dev_assoc.patient_id = patInfo.id "

				+" join " 
				+" USER_AUTHORITY user_authority on user_authority.user_id = user.id "  
				+" and user_authority.authority_name = 'PATIENT'  and (lower(user.first_name) like lower(:queryString) or "
				+" lower(user.last_name) like lower(:queryString) or  lower(user.email) like lower(:queryString) or "  
				+" lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or " 
				+" lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or "
				+" (lower(IFNULL(user_clinic.mrn_id,0)) like lower(:queryString) ) ) "
				+" left outer join PATIENT_COMPLIANCE_MONARCH pc on user.id = pc.user_id " 
				+" AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate()) " 
				+" left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and  user_clinic.patient_id = patInfo.id"  

				+" where patient_dev_assoc.ptype in ('MONARCH','ALL')   group by user.id) as associated_patient " 

				+" left outer join " 
				+" (select  GROUP_CONCAT(huser.last_name ,' ',huser.first_name ) as hName,  clinic.id as hclinicid " 
				+" from USER huser " 
				+" join USER_AUTHORITY user_authorityh on user_authorityh.user_id = huser.id"   
				+" and user_authorityh.authority_name = 'HCP' "  
				+" left outer join CLINIC_USER_ASSOC user_clinic " 
				+" on user_clinic.users_id = huser.id "  
				+" left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id" 
				+" and user_clinic.users_id = huser.id"  
				+" left outer join PATIENT_COMPLIANCE_MONARCH pc on huser.id = pc.user_id " 
				+" AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate()) "  
				+" group by clinic.id) as associated_hcp "  
				+" on associated_patient.pclinicid = associated_hcp.hclinicid "
				+" UNION "
				+" select patient_id as id,pemail,pfirstName,plastName, isDeleted,pzipcode,paddress,pcity,pdob,pgender,ptitle, phillrom_id,createdAt,isActivated, " 
				+" state,adherence,last_date,mrnid,hName,clinicName,isExpired,isHMRNonCompliant,isSettingsDeviated, isMissedTherapy,adherencesetting,devType "				
				+" from " 
				+" (select " 
				+" user.id as patient_id,user.email as pemail,user.first_name as pfirstName, "
				+" user.last_name as plastName, user.is_deleted as isDeleted, user.zipcode as pzipcode, "
				+" patInfo.address paddress,patInfo.city as pcity,user.dob as pdob, user.gender as pgender, "
				+" user.title as ptitle,  user.hillrom_id as phillrom_id,user.created_date as createdAt, "
				+" user.activated as isActivated, patInfo.state as state ,  user_clinic.mrn_id as mrnid, "
				+" clinic.id as pclinicid,  GROUP_CONCAT(clinic.name) as clinicName, user.expired as isExpired, "
				+" pc.compliance_score as adherence,   pc.last_therapy_session_date "
				+" as last_date,pc.is_hmr_compliant as isHMRNonCompliant, pc.is_settings_deviated "
				+" as isSettingsDeviated, pc.missed_therapy_count as isMissedTherapy, " 

				+" clinic.adherence_setting as adherencesetting,patient_dev_assoc.ptype as devType " 

				+" from USER user " 
				+" join "
				+" USER_PATIENT_ASSOC  upa on user.id = upa.user_id  and upa.relation_label = 'Self' "
				+" join " 
				+" PATIENT_INFO patInfo on upa.patient_id = patInfo.id " 	
				+" left outer join " 
				+" CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id " 

				+ " left outer join " 
				+ " (SELECT pda.*, IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
				+ "	from "
				+ "	PATIENT_DEVICES_ASSOC pda "  
				+ "	where "
				+ " is_active=1 group by patient_id) patient_dev_assoc on patient_dev_assoc.patient_id = patInfo.id "

				+" join " 
				+" USER_AUTHORITY user_authority on user_authority.user_id = user.id "   
				+" and user_authority.authority_name = 'PATIENT'  and (lower(user.first_name) "
				+" like lower(:queryString) or  lower(user.last_name) like lower(:queryString) or  lower(user.email) " 
				+" like lower(:queryString) or  lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or " 
				+" lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or "
				+" (lower(IFNULL(user_clinic.mrn_id,0)) like lower(:queryString) ) ) "			
				+" left outer join " 
				+" PATIENT_COMPLIANCE pc on user.id = pc.user_id AND " 
				+" pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate())	"  
				+" left outer join " 
				+" CLINIC clinic on user_clinic.clinic_id = clinic.id and  user_clinic.patient_id = patInfo.id "		

				+" where patient_dev_assoc.ptype IN ('VEST') " 

				+" group by user.id) as associated_patient " 
				+" left outer join" 
				+" (select "  
				+" GROUP_CONCAT(huser.last_name ,' ',huser.first_name ) as hName, "  
				+" clinic.id as hclinicid " 
				+" from USER huser	" 
				+" join	" 
				+" USER_AUTHORITY user_authorityh on user_authorityh.user_id = huser.id  and user_authorityh.authority_name = 'HCP'	" 
				+" left outer join	" 
				+" CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = huser.id	" 
				+" left outer join	" 
				+" CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = huser.id	"  
				+" left outer join	" 
				+" PATIENT_COMPLIANCE pc on huser.id = pc.user_id AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate())	"  
				+" group by clinic.id) as associated_hcp	"  
				+" on associated_patient.pclinicid = associated_hcp.hclinicid ";
	

	public static String QUERY_PATIENT_SEARCH_PART1 = " select patient_id as id,pemail,pfirstName,plastName, isDeleted,pzipcode,paddress,pcity,pdob,pgender,ptitle,"
			+ " phillrom_id,createdAt,isActivated, state , adherence,last_date,mrnid,hName,clinicName,isExpired,isHMRNonCompliant,isSettingsDeviated,"
			+ " isMissedTherapy,adherencesetting,devType  from (select user.id as patient_id,user.email as pemail,user.first_name as pfirstName,user.last_name as plastName,"
			+ " user.is_deleted as isDeleted, user.zipcode as pzipcode,patInfo.address paddress,patInfo.city as pcity,user.dob as pdob,"
			+ " user.gender as pgender,user.title as ptitle,  user.hillrom_id as phillrom_id,user.created_date as createdAt,"
			+ " user.activated as isActivated, patInfo.state as state ,  user_clinic.mrn_id as mrnid, clinic.id as pclinicid, "
			+ " GROUP_CONCAT(clinic.name) as clinicName, user.expired as isExpired, pc.compliance_score as adherence,  "
			+ " pc.last_therapy_session_date as last_date,pc.is_hmr_compliant as isHMRNonCompliant,"
			+ " pc.is_settings_deviated as isSettingsDeviated,"

			+ " pc.missed_therapy_count as isMissedTherapy, clinic.adherence_setting as adherencesetting,patient_dev_assoc.ptype as devType from USER user join USER_PATIENT_ASSOC  upa on user.id = upa.user_id "
			+ " and upa.relation_label = '" + SELF + "' join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
			+ " left outer join CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id "
			+ " left outer join " 
			+ " (SELECT pda.*, IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
			+ "	from "
			+ "	PATIENT_DEVICES_ASSOC pda "  
			+ "	where "
			+ " is_active=1 group by patient_id) patient_dev_assoc on patient_dev_assoc.patient_id = patInfo.id "

			+ " join USER_AUTHORITY user_authority on user_authority.user_id = user.id  and user_authority.authority_name = '"
			+ PATIENT + "' "
			+ " and (lower(user.first_name) like lower(:queryString) or  lower(user.last_name) like lower(:queryString) or "
			+ " lower(user.email) like lower(:queryString) or "
			+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
			+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or ";

	public static String QUERY_PATIENT_SEARCH_PART2_VEST = " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate()) "
			+ " left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and  user_clinic.patient_id = patInfo.id "

			+ " where patient_dev_assoc.ptype IN ('VEST','ALL')  "

			+ " group by user.id) as associated_patient left outer join (select  GROUP_CONCAT(huser.last_name ,' ',huser.first_name ) as hName, "
			+ " clinic.id as hclinicid from USER huser join USER_AUTHORITY user_authorityh on user_authorityh.user_id = huser.id "
			+ " and user_authorityh.authority_name = '" + HCP + "' "
			+ " left outer join CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = huser.id "
			+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = huser.id "
			+ " left outer join PATIENT_COMPLIANCE pc on huser.id = pc.user_id AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate()) "
			+ " group by clinic.id) as associated_hcp  on associated_patient.pclinicid = associated_hcp.hclinicid ";

	public static String QUERY_PATIENT_SEARCH_PART2_MONARCH = " left outer join PATIENT_COMPLIANCE_MONARCH pc on user.id = pc.user_id AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate()) "
			+ " left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and  user_clinic.patient_id = patInfo.id "

			+ " where patient_dev_assoc.ptype IN ('MONARCH','ALL')   "

			+ " group by user.id) as associated_patient left outer join (select  GROUP_CONCAT(huser.last_name ,' ',huser.first_name ) as hName, "
			+ " clinic.id as hclinicid from USER huser join USER_AUTHORITY user_authorityh on user_authorityh.user_id = huser.id "
			+ " and user_authorityh.authority_name = '" + HCP + "' "
			+ " left outer join CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = huser.id "
			+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = huser.id "
			+ " left outer join PATIENT_COMPLIANCE_MONARCH pc on huser.id = pc.user_id AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate()) "
			+ " group by clinic.id) as associated_hcp  on associated_patient.pclinicid = associated_hcp.hclinicid ";
	
	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_PART1 = " select user.id,user.email,user.first_name as"
			+ " firstName,user.last_name as lastName, IF(user.is_deleted=true,1,IF(patient_clinic.is_active=true,0,IF(patient_clinic.is_active = NULL,user.is_deleted,1))) as isDeleted ,"
			+ " user.zipcode,patInfo.address,patInfo.city,user.dob,user.gender,"
			+ " user.title,user.hillrom_id,user.created_date as createdAt,"
			+ " user.activated as isActivated, patInfo.state , compliance_score, pc.last_therapy_session_date as last_date, patient_clinic.expired, patient_clinic.mrn_id as mrnId, "
			+ " pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated,pc.missed_therapy_count as isMissedTherapy,clinic.adherence_setting as adherencesetting,"

			+ " patient_dev_assoc.ptype as devType "

			+ " from USER user" + " join USER_AUTHORITY user_authority on user_authority.user_id = user.id  "
			+ " and user_authority.authority_name = '" + PATIENT + "' and "
			+ " (lower(user.first_name) like lower(:queryString) or  "
			+ " lower(user.last_name) like lower(:queryString) or  "
			+ " lower(user.email) like lower(:queryString) or "
			+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
			+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or "
			+ " lower(user.hillrom_id) like lower(:queryString)) "
			+ " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '" + SELF + "' "
			+ " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
			+ " join CLINIC_PATIENT_ASSOC patient_clinic on "
			+ " patient_clinic.patient_id = patInfo.id and patient_clinic.clinic_id = ':clinicId'"
			+ " join CLINIC clinic on clinic.id = patient_clinic.clinic_id "

			+ " left outer join "
			+ "(SELECT pda.*, IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
			+ " from " 
			+ " PATIENT_DEVICES_ASSOC pda "  
			+ " where " 
			+ " is_active=1 group by patient_id) patient_dev_assoc on patient_dev_assoc.patient_id = patInfo.id ";
	
	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_PART2_VEST_DEVTYPE = " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id "
			+ " AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate()) "
			+ " where patient_dev_assoc.ptype IN ('VEST','ALL') group by pc.user_id";

	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_PART2_MONARCH_DEVTYPE = " left outer join PATIENT_COMPLIANCE_MONARCH pc on user.id = pc.user_id "
			+ " AND pc.date=IF(pc.date <> curdate(),subdate(curdate(),1),curdate()) ";
			
	
	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_PART3_MONARCH_DEVTYPE = " where patient_dev_assoc.ptype IN ('MONARCH','ALL') group by pc.user_id";
	
	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_MONARCH_DEVTYPE = QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_PART2_MONARCH_DEVTYPE 
			+ QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_PART3_MONARCH_DEVTYPE;
	
	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_MONARCH_DEVTYPE_EX_ALL = QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_PART2_MONARCH_DEVTYPE 
			+ " where patient_dev_assoc.ptype IN ('MONARCH') group by pc.user_id";

	
	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_ADMIN_VEST_DEVTYPE = " select user.id,user.email,user.first_name as firstName,user.last_name as lastName,  "
			+ " IF(user.is_deleted=true,1,IF(patient_clinic.is_active=true,0,IF(patient_clinic.is_active = NULL,user.is_deleted,1))) as isDeleted ,user.zipcode,patInfo.address,patInfo.city,user.dob,"
			+ " user.gender,user.title,user.hillrom_id,user.created_date as createdAt, user.activated as isActivated, patInfo.state as state, pc.compliance_score adherence,"
			+ " pc.last_therapy_session_date as last_date, "
			+ " (select  GROUP_CONCAT(clinicc.name) from USER userc  "
			+ " left outer join USER_AUTHORITY user_authorityc on user_authorityc.user_id = userc.id  and user_authorityc.authority_name = '"
			+ PATIENT + "' "
			+ " join USER_PATIENT_ASSOC  upac on userc.id = upac.user_id and upac.relation_label = '" + SELF + "' "
			+ " left outer join PATIENT_INFO patInfoc on upac.patient_id = patInfoc.id  "
			+ " left outer join CLINIC_PATIENT_ASSOC user_clinicc on user_clinicc.patient_id = patInfoc.id   "
			+ " left outer join CLINIC clinicc on user_clinicc.clinic_id = clinicc.id and user_clinicc.patient_id = patInfoc.id "
			+ "  where upac.user_id = user.id  group by patInfoc.id) as clinicname, "
			+ " (select  GROUP_CONCAT(userh.last_name,' ',userh.first_name)  from USER userh  "
			+ " left outer join USER_AUTHORITY user_authorityh on user_authorityh.user_id = userh.id "
			+ " and user_authorityh.authority_name = '" + HCP
			+ "'  join USER_PATIENT_ASSOC  upah on userh.id = upah.user_id " + " and upah.relation_label = '" + HCP
			+ "'  left outer join PATIENT_INFO patInfoh on upah.patient_id = patInfoh.id "
			+ " where patInfo.id = patInfoh.id group by patInfoh.id) as hcpname,patient_clinic.mrn_id as mrnid,"
			+ " patient_clinic.expired as isExpired, pc.is_hmr_compliant as isHMRNonCompliant,"
			+ " pc.is_settings_deviated as isSettingsDeviated, pc.missed_therapy_count as isMissedTherapy, clinic.adherence_setting as adherencesetting,"

			+ " patient_dev_assoc.ptype as devType from USER user "

			+ " left outer join USER_AUTHORITY user_authority on user_authority.user_id = user.id and user_authority.authority_name = '"
			+ PATIENT + "'" + " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '"
			+ SELF + "' " + " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id"
			+ " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=IF(pc.date <> curdate(),IF(pc.date <> curdate(),subdate(curdate(),1),curdate()),curdate()) "
			+ " join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id"

			+ " left outer join " 
			+ " (SELECT pda.*, IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
			+ "	from "
			+ "	PATIENT_DEVICES_ASSOC pda "  
			+ "	where "
			+ " is_active=1 group by patient_id) patient_dev_assoc on patient_dev_assoc.patient_id = patInfo.id "

			+ " join CLINIC clinic on clinic.id = patient_clinic.clinic_id "
			+ " and (lower(user.first_name)  like lower(:queryString) or "
			+ " lower(user.last_name) like lower(:queryString)  or "
			+ " lower(user.email) like lower(:queryString) or "
			+ " lower(CONCAT(user.first_name,' ',user.last_name))  like lower(:queryString) or "
			+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString)  or "
			+ " lower(IFNULL(patient_clinic.mrn_id,0)) like lower(:queryString) or "

			+ " lower(user.hillrom_id) like lower(:queryString)) "			
			+ " where clinic.id= ':clinicId' and patient_dev_assoc.ptype in ('VEST','ALL')";
	
	
	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_ADMIN_MONARCH_DEVTYPE_PART1 = " select user.id,user.email,user.first_name as firstName,user.last_name as lastName,  "

			+ " IF(user.is_deleted=true,1,IF(patient_clinic.is_active=true,0,IF(patient_clinic.is_active = NULL,user.is_deleted,1))) as isDeleted ,user.zipcode,patInfo.address,patInfo.city,user.dob,"
			+ " user.gender,user.title,user.hillrom_id,user.created_date as createdAt, user.activated as isActivated, patInfo.state as state, pc.compliance_score adherence,"
			+ " pc.last_therapy_session_date as last_date, "
			+ " (select  GROUP_CONCAT(clinicc.name) from USER userc  "
			+ " left outer join USER_AUTHORITY user_authorityc on user_authorityc.user_id = userc.id  and user_authorityc.authority_name = '"
			+ PATIENT + "' "
			+ " join USER_PATIENT_ASSOC  upac on userc.id = upac.user_id and upac.relation_label = '" + SELF + "' "
			+ " left outer join PATIENT_INFO patInfoc on upac.patient_id = patInfoc.id  "
			+ " left outer join CLINIC_PATIENT_ASSOC user_clinicc on user_clinicc.patient_id = patInfoc.id   "
			+ " left outer join CLINIC clinicc on user_clinicc.clinic_id = clinicc.id and user_clinicc.patient_id = patInfoc.id "
			+ "  where upac.user_id = user.id  group by patInfoc.id) as clinicname, "
			+ " (select  GROUP_CONCAT(userh.last_name,' ',userh.first_name)  from USER userh  "
			+ " left outer join USER_AUTHORITY user_authorityh on user_authorityh.user_id = userh.id "
			+ " and user_authorityh.authority_name = '" + HCP
			+ "'  join USER_PATIENT_ASSOC  upah on userh.id = upah.user_id " + " and upah.relation_label = '" + HCP
			+ "'  left outer join PATIENT_INFO patInfoh on upah.patient_id = patInfoh.id "
			+ " where patInfo.id = patInfoh.id group by patInfoh.id) as hcpname,patient_clinic.mrn_id as mrnid,"
			+ " patient_clinic.expired as isExpired, pc.is_hmr_compliant as isHMRNonCompliant,"
			+ " pc.is_settings_deviated as isSettingsDeviated, pc.missed_therapy_count as isMissedTherapy, clinic.adherence_setting as adherencesetting,"

			+ " patient_dev_assoc.ptype as devType from USER user "

			+ " left outer join USER_AUTHORITY user_authority on user_authority.user_id = user.id and user_authority.authority_name = '"
			+ PATIENT + "'" + " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '"
			+ SELF + "' " + " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id"
			+ " left outer join PATIENT_COMPLIANCE_MONARCH pc on user.id = pc.user_id AND pc.date=IF(pc.date <> curdate(),IF(pc.date <> curdate(),subdate(curdate(),1),curdate()),curdate()) "
			+ " join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id"

			+ " left outer join " 
			+ " (SELECT pda.*, IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
			+ "	from "
			+ "	PATIENT_DEVICES_ASSOC pda "  
			+ "	where "
			+ " is_active=1 group by patient_id) patient_dev_assoc on patient_dev_assoc.patient_id = patInfo.id "

			+ " join CLINIC clinic on clinic.id = patient_clinic.clinic_id "
			+ " and (lower(user.first_name)  like lower(:queryString) or "
			+ " lower(user.last_name) like lower(:queryString)  or "
			+ " lower(user.email) like lower(:queryString) or "
			+ " lower(CONCAT(user.first_name,' ',user.last_name))  like lower(:queryString) or "
			+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString)  or "
			+ " lower(IFNULL(patient_clinic.mrn_id,0)) like lower(:queryString) or "

			+ " lower(user.hillrom_id) like lower(:queryString)) ";
	
	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_ADMIN_MONARCH_DEVTYPE_PART2 = " where clinic.id= ':clinicId' and patient_dev_assoc.ptype in ('MONARCH','ALL')";
	
	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_ADMIN_MONARCH_DEVTYPE = QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_ADMIN_MONARCH_DEVTYPE_PART1 + QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_ADMIN_MONARCH_DEVTYPE_PART2;
	
	public static String QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_ADMIN_MONARCH_DEVTYPE_EX_ALL = QUERY_ASSOCIATED_PATIENT_SEARCH_UNDER_CLINIC_ADMIN_MONARCH_DEVTYPE_PART1   
			+ " where clinic.id= ':clinicId' and patient_dev_assoc.ptype in ('MONARCH')";
	
	
	public static String QUERY_PATIENT_SEARCH_UNDER_HCP_USER_WHERE_CLAUSE_FOR_MONARCH_ALL = " and patient_dev_assoc.ptype in ('MONARCH','ALL')";
	public static String QUERY_PATIENT_SEARCH_UNDER_HCP_USER_WHERE_CLAUSE_FOR_MONARCH_DEVTYPE = " and patient_dev_assoc.ptype in ('MONARCH')";


}
