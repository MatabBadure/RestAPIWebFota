package com.hillrom.vest.domain.FOTA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
@Entity
@Audited
@Table(name = "FOTA_INFO")
public class FOTAInfo {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="device_part_number")
	private String devicePartNumber;
	
	@Column(name="software_version")
	private String softVersion;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name="release_date")
	private DateTime releaseDate;
	
	@Column(name="product_Type")
	private String productType;
	
	@Column(name="file_path")
	private String filePath;
	
	@Column(name="upload_user")
	private String uploadUser;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name="upload_datetime")
	private DateTime uploadDatetime;
	
	@Column(name="published_user")
	private String publishedUser;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name="published_datetime")
	private DateTime publishedDateTime;
	
	@Column(name="model_id")
	private String modelId;
	
	@Column(name="board_id")
	private String boardId;
	
	@Column(name="bed_id")
	private String bedId;

	@Column(name="boot_comp_ver")
	private String bootCompVer;
	
	@Column(name="fill_pattern")
	private String fillPattern;
	
	@Column(name="MCU_size")
	private String MCUSize;
	
	@Column(name="release_number")
	private String releaseNumber;
	
	@Column(name="soft_delete_flag")
	private boolean softDeleteFlag;
	
	@Column(name="active_published_flag")
	private boolean activePublishedFlag;
	
	@Column(name="delete_request_flag")
	private boolean deleteRequestFlag;
	
	@Column(name="Region1_Start_Address")
	private String region1StartAddress = "";
	
	@Column(name="Region1_End_Address")
	private String region1EndAddress = "";
	
	@Column(name="Region1_CRC_Location")
	private String region1CRCLocation = "";
	
	@Column(name="Region2_Start_Address")
	private String region2StartAddress = "";
	
	@Column(name="Region2_End_Address")
	private String region2EndAddress = "";
	
	@Column(name="Region2_CRC_Location")
	private String region2CRCLocation = "";

	@Transient
	@JsonSerialize
	@JsonDeserialize
	private String FOTAStatus;
	
	
	

	

	public boolean getDeleteRequestFlag() {
		return deleteRequestFlag;
	}

	public void setDeleteRequestFlag(boolean deleteRequestFlag) {
		this.deleteRequestFlag = deleteRequestFlag;
	}

	public String getFOTAStatus() {
		return FOTAStatus;
	}

	public void setFOTAStatus(String fOTAStatus) {
		FOTAStatus = fOTAStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDevicePartNumber() {
		return devicePartNumber;
	}

	public void setDevicePartNumber(String devicePartNumber) {
		this.devicePartNumber = devicePartNumber;
	}

	public String getSoftVersion() {
		return softVersion;
	}

	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}

	public DateTime getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(DateTime releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}

	public DateTime getUploadDatetime() {
		return uploadDatetime;
	}

	public void setUploadDatetime(DateTime uploadDatetime) {
		this.uploadDatetime = uploadDatetime;
	}

	public String getPublishedUser() {
		return publishedUser;
	}

	public void setPublishedUser(String publishedUser) {
		this.publishedUser = publishedUser;
	}

	public DateTime getPublishedDateTime() {
		return publishedDateTime;
	}

	public void setPublishedDateTime(DateTime publishedDateTime) {
		this.publishedDateTime = publishedDateTime;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public String getBedId() {
		return bedId;
	}

	public void setBedId(String bedId) {
		this.bedId = bedId;
	}

	public String getBootCompVer() {
		return bootCompVer;
	}

	public void setBootCompVer(String bootCompVer) {
		this.bootCompVer = bootCompVer;
	}

	public String getFillPattern() {
		return fillPattern;
	}

	public void setFillPattern(String fillPattern) {
		this.fillPattern = fillPattern;
	}

	public String getMCUSize() {
		return MCUSize;
	}

	public void setMCUSize(String mCUSize) {
		MCUSize = mCUSize;
	}

	public String getReleaseNumber() {
		return releaseNumber;
	}

	public void setReleaseNumber(String releaseNumber) {
		this.releaseNumber = releaseNumber;
	}

	public boolean getSoftDeleteFlag() {
		return softDeleteFlag;
	}

	public void setSoftDeleteFlag(boolean softDeleteFlag) {
		this.softDeleteFlag = softDeleteFlag;
	}

	public boolean getActivePublishedFlag() {
		return activePublishedFlag;
	}

	public void setActivePublishedFlag(boolean activePublishedFlag) {
		this.activePublishedFlag = activePublishedFlag;
	}

	public String getRegion1StartAddress() {
		return region1StartAddress;
	}

	public void setRegion1StartAddress(String region1StartAddress) {
		this.region1StartAddress = region1StartAddress;
	}

	public String getRegion1EndAddress() {
		return region1EndAddress;
	}

	public void setRegion1EndAddress(String region1EndAddress) {
		this.region1EndAddress = region1EndAddress;
	}

	public String getRegion1CRCLocation() {
		return region1CRCLocation;
	}

	public void setRegion1CRCLocation(String region1crcLocation) {
		region1CRCLocation = region1crcLocation;
	}

	public String getRegion2StartAddress() {
		return region2StartAddress;
	}

	public void setRegion2StartAddress(String region2StartAddress) {
		this.region2StartAddress = region2StartAddress;
	}

	public String getRegion2EndAddress() {
		return region2EndAddress;
	}

	public void setRegion2EndAddress(String region2EndAddress) {
		this.region2EndAddress = region2EndAddress;
	}

	public String getRegion2CRCLocation() {
		return region2CRCLocation;
	}

	public void setRegion2CRCLocation(String region2crcLocation) {
		region2CRCLocation = region2crcLocation;
	}
	
	

}
