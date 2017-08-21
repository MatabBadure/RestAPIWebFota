package com.hillrom.vest.domain.FOTA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
@Entity
@Audited
@Table(name = "FOTA_INFO")
public class FOTAInfo {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="software_version")
	private String softVersion;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    /*@JsonSerialize(using = MMDDYYYYLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)*/
	@Column(name="release_date")
	private DateTime releaseDate;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name="upload_datetime")
	private DateTime uploadDatetime;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name="effective_datetime")
	private DateTime effectiveDatetime;
	
	
	@Column(name="device_part_number")
	private String devicePartNumber;
	
	@Column(name="file_path")
	private String filePath;
	
	@Column(name="upload_user")
	private String uploadUser;
	
	@Column(name="product_Type")
	private String productType;
	
	@Column(name="model_id")
	private String modelId;
	
	@Column(name="board_id")
	private String boardId;
	
	@Column(name="bed_id")
	private String bedId;

	@Column(name="boot_comp_ver")
	private String bootCompVer;
	
	@Column(name="file_pattern")
	private String filePattern;
	
	@Column(name="MCU_size")
	private String MCUSize;
	
	@Column(name="release_number")
	private String releaseNumber;
	
	@Column(name="checksum")
	private String checksum;
	
	@Column(name="old_soft_flag")
	private boolean oldSoftFlag;
	
	public boolean getOldSoftFlag() {
		return oldSoftFlag;
	}

	public void setOldSoftFlag(boolean oldSoftFlag) {
		this.oldSoftFlag = oldSoftFlag;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public DateTime getUploadDatetime() {
		return uploadDatetime;
	}

	public void setUploadDatetime(DateTime uploadDatetime) {
		this.uploadDatetime = uploadDatetime;
	}

	public DateTime getEffectiveDatetime() {
		return effectiveDatetime;
	}

	public void setEffectiveDatetime(DateTime effectiveDatetime) {
		this.effectiveDatetime = effectiveDatetime;
	}

	public String getDevicePartNumber() {
		return devicePartNumber;
	}

	public void setDevicePartNumber(String devicePartNumber) {
		this.devicePartNumber = devicePartNumber;
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

	public String getFilePattern() {
		return filePattern;
	}

	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
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

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	
}
