package com.hillrom.vest.domain.FOTA;

import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
@Entity
@Table(name = "FOTA_INFO")
public class FOTAInfo {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Size(max = 16)
	@Column(name="device_part_number", length = 16)
	private String devicePartNumber;
	
	@Size(max = 8)
	@Column(name="software_version", length = 8)
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
	
	
	@Size(max = 2)
	@Column(name="model_id", length = 2)
	private String modelId;
	
	@Size(max = 2)
	@Column(name="board_id", length = 2)
	private String boardId;
	
	@Size(max = 2)
	@Column(name="bed_id", length = 2)
	private String bedId;

	@Size(max = 2)
	@Column(name="boot_comp_ver", length = 2)
	private String bootCompVer;
	
	@Size(max = 8)
	@Column(name="fill_pattern", length = 8)
	private String fillPattern;
	
	@Size(max = 8)
	@Column(name="MCU_size", length = 8)
	private String MCUSize;
	
	@Size(max = 8)
	@Column(name="release_number", length = 8)
	private String releaseNumber;
	
	@Column(name="soft_delete_flag")
	private boolean softDeleteFlag;
	
	@Column(name="active_published_flag")
	private boolean activePublishedFlag;
	
	@Column(name="delete_request_flag")
	private boolean deleteRequestFlag;
	
	@Size(max = 8)
	@Column(name="Region1_Start_Address", length = 8)
	private String region1StartAddress = "";
	
	@Size(max = 8)
	@Column(name="Region1_End_Address", length = 8)
	private String region1EndAddress = "";
	
	@Size(max = 8)
	@Column(name="Region1_CRC_Location", length = 8)
	private String region1CRCLocation = "";
	
	@Column(name="Region2_Start_Address", length = 8)
	private String region2StartAddress = "";
	
	@Column(name="Region2_End_Address", length = 8)
	private String region2EndAddress = "";
	
	@Column(name="Region2_CRC_Location", length = 8)
	private String region2CRCLocation = "";

	@Transient
	@JsonSerialize
	@JsonDeserialize
	private String FOTAStatus;
	
	
	
	public static Comparator<FOTAInfo> idDesc = new Comparator<FOTAInfo>() {

		public int compare(FOTAInfo s1, FOTAInfo s2) {
			Long id1 = s1.getId();
			Long id2 = s2.getId();
			// desc order
			return id2.compareTo(id1);
		}
	};
	public static Comparator<FOTAInfo> devicePartAscComparator = new Comparator<FOTAInfo>() {

		public int compare(FOTAInfo s1, FOTAInfo s2) {
			String devicePartNumber1 = s1.getDevicePartNumber();
			String devicePartNumber2 = s2.getDevicePartNumber();
			// ascending order
			return devicePartNumber1.compareTo(devicePartNumber2);
		}
	};
	public static Comparator<FOTAInfo> devicePartDescComparator = new Comparator<FOTAInfo>() {
		public int compare(FOTAInfo s1, FOTAInfo s2) {
			String devicePartNumber1 = s1.getDevicePartNumber();
			String devicePartNumber2 = s2.getDevicePartNumber();
			// Descending order
			return devicePartNumber2.compareTo(devicePartNumber1);
		}
	};
	
	
	public static Comparator<FOTAInfo> productNameAscComparator = new Comparator<FOTAInfo>() {

		public int compare(FOTAInfo s1, FOTAInfo s2) {
			String productName1 = s1.getProductType();
			String productName2 = s2.getProductType();
			// ascending order
			return productName1.compareTo(productName2);
		}
	};
	public static Comparator<FOTAInfo> productNameDescComparator = new Comparator<FOTAInfo>() {
		public int compare(FOTAInfo s1, FOTAInfo s2) {
			String productName1 = s1.getProductType();
			String productName2 = s2.getProductType();
			// Descending order
			return productName2.compareTo(productName1);
		}
	};
	
	public static Comparator<FOTAInfo> softVerAscComparator = new Comparator<FOTAInfo>() {

		public int compare(FOTAInfo s1, FOTAInfo s2) {
			String softVer1 = s1.getSoftVersion();
			String softVer2 = s2.getSoftVersion();
			// ascending order
			return softVer1.compareTo(softVer2);
		}
	};
	public static Comparator<FOTAInfo> softVerDescComparator = new Comparator<FOTAInfo>() {
		public int compare(FOTAInfo s1, FOTAInfo s2) {
			String softVer1 = s1.getSoftVersion();
			String softVer2 = s2.getSoftVersion();
			// Descending order
			return softVer2.compareTo(softVer1);
		}
	}; 
	public static Comparator<FOTAInfo> softDateAscComparator = new Comparator<FOTAInfo>() {

		public int compare(FOTAInfo s1, FOTAInfo s2) {
			DateTime softDate1 = s1.getReleaseDate();
			DateTime softDate2 = s2.getReleaseDate();
			// ascending order
			return softDate1.compareTo(softDate2);
		}
	};
	public static Comparator<FOTAInfo> softDateDescComparator = new Comparator<FOTAInfo>() {
		public int compare(FOTAInfo s1, FOTAInfo s2) {
			DateTime softDate1 = s1.getReleaseDate();
			DateTime softDate2 = s2.getReleaseDate();
			// ascending order
			return softDate2.compareTo(softDate1);
		}
	};
	
	public static Comparator<FOTAInfo> uploadByAscComparator = new Comparator<FOTAInfo>() {

		public int compare(FOTAInfo s1, FOTAInfo s2) {
			String uploadBy1 = s1.getUploadUser();
			String uploadBy2 = s2.getUploadUser();
			// ascending order
			return uploadBy1.compareTo(uploadBy2);
		}
	};
	public static Comparator<FOTAInfo> uploadByDescComparator = new Comparator<FOTAInfo>() {
		public int compare(FOTAInfo s1, FOTAInfo s2) {
			String uploadBy1 = s1.getUploadUser();
			String uploadBy2 = s2.getUploadUser();
			// ascending order
			return uploadBy2.compareTo(uploadBy1);
		}
	};
	
	public static Comparator<FOTAInfo> uploadDateAscComparator = new Comparator<FOTAInfo>() {

		public int compare(FOTAInfo s1, FOTAInfo s2) {
			DateTime uploadDate1 = s1.getUploadDatetime();
			DateTime uploadDate2 = s2.getUploadDatetime();
			// ascending order
			return uploadDate1.compareTo(uploadDate2);
		}
	};
	public static Comparator<FOTAInfo> uploadDateDescComparator = new Comparator<FOTAInfo>() {
		public int compare(FOTAInfo s1, FOTAInfo s2) {
			DateTime uploadDate1 = s1.getUploadDatetime();
			DateTime uploadDate2 = s2.getUploadDatetime();
			// ascending order
			return uploadDate2.compareTo(uploadDate1);
		}
	};
	
	public static Comparator<FOTAInfo> publishedByAscComparator = new Comparator<FOTAInfo>() {

		public int compare(FOTAInfo s1, FOTAInfo s2) {
			if(s1.getPublishedUser() == null && s2.getPublishedUser()== null){
				return 0;
			}else if(s1.getPublishedUser() == null){
				return -1;
			}else if(s1.getPublishedUser() == null){
				return 1;
			}else if(s1.getPublishedUser() != null && s2.getPublishedUser() != null){
				return s1.getPublishedUser().compareTo(s2.getPublishedUser());
			}else{
				return 0;
			}
			
		}
	};
	public static Comparator<FOTAInfo> publishedByDescComparator = new Comparator<FOTAInfo>() {
		public int compare(FOTAInfo s1, FOTAInfo s2) {

			if(s1.getPublishedUser() == null && s2.getPublishedUser()== null){
				return 0;
			}else if(s1.getPublishedUser() == null){
				return 1;
			}else if(s1.getPublishedUser() == null){
				return -1;
			}else if(s1.getPublishedUser() != null && s2.getPublishedUser() != null){
				return s2.getPublishedUser().compareTo(s1.getPublishedUser());
			}else{
				return 0;
			}
			
		
			
		}
	};
	
	
	public static Comparator<FOTAInfo> publishedDateAscComparator = new Comparator<FOTAInfo>() {

		public int compare(FOTAInfo s1, FOTAInfo s2) {
			if(s1.getPublishedDateTime() == null && s2.getPublishedDateTime()== null){
				return 0;
			}else if(s1.getPublishedDateTime() == null){
				return -1;
			}else if(s1.getPublishedDateTime() == null){
				return 1;
			}else if(s1.getPublishedDateTime() != null && s2.getPublishedDateTime() != null){
				return s1.getPublishedDateTime().compareTo(s2.getPublishedDateTime());
			}else{
				return 0;
			}
			
		}
	};
	public static Comparator<FOTAInfo> publishedDateDescComparator = new Comparator<FOTAInfo>() {
		public int compare(FOTAInfo s1, FOTAInfo s2) {
			if(s1.getPublishedDateTime() == null && s2.getPublishedDateTime()== null){
				return 0;
			}else if(s1.getPublishedDateTime() != null && s2.getPublishedDateTime() != null){
				return s2.getPublishedDateTime().compareTo(s1.getPublishedDateTime());
			}else if(s1.getPublishedDateTime() == null){
				return 1;
			}else  if(s1.getPublishedDateTime() == null){
				return -1;
			}else{
				return 0;
			}
		}
	};
	
	
	public static Comparator<FOTAInfo> statusAscComparator = new Comparator<FOTAInfo>() {
		/*if(s1.getPublishedDateTime() != null && s2.getPublishedDateTime() != null){
		return s1.getPublishedDateTime().compareTo(s2.getPublishedDateTime());*/
		public int compare(FOTAInfo s1, FOTAInfo s2) {
			String status1 = s1.getFOTAStatus();
			String status2 = s2.getFOTAStatus();
			// ascending order
			return status1.compareTo(status2);
		}
	};
	public static Comparator<FOTAInfo> statusDescComparator = new Comparator<FOTAInfo>() {
		public int compare(FOTAInfo s1, FOTAInfo s2) {
			String status1 = s1.getFOTAStatus();
			String status2 = s2.getFOTAStatus();
			// ascending order
			return status2.compareTo(status1);
		}
	};
	
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
