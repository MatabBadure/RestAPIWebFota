package com.hillrom.vest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.CustomDateTimeDeserializer;
import com.hillrom.vest.domain.util.CustomDateTimeSerializer;
import com.hillrom.vest.web.rest.dto.LoginAnalyticsVO;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A UserLoginToken.
 */
@Entity
@Table(name = "USER_LOGIN_TOKEN")
@SQLDelete(sql="UPDATE USER_LOGIN_TOKEN SET is_expired = 1,last_modified_time = current_timestamp() WHERE id = ?")
@NamedNativeQueries(value = {
	@NamedNativeQuery(resultSetMapping="AnalyticsMappingForWeekOrDay", name = "getAnalyticsForWeekOrDay", 
			  query = "SELECT DATE(ult.created_time) date,count(distinct(ult.user_id)) loginCount,ua.authority_name authority "
			  		+ "FROM USER_LOGIN_TOKEN ult join USER_AUTHORITY ua "
			  		+ "ON DATE(ult.created_time) between (:from) and (:to) "
			  		+ "AND ua.authority_name in  (:authorities) "
			  		+ "AND ult.user_id = ua.user_id "
			  		+ "GROUP BY DATE(ult.created_time),ua.authority_name"),
	@NamedNativeQuery(resultSetMapping="AnalyticsMappingForMonth", name = "getAnalyticsForMonth", 
	  query = "SELECT DATE(ult.created_time) date,count(distinct(ult.user_id)) loginCount,ua.authority_name authority,ult.user_id as userId "
	  		+ "FROM USER_LOGIN_TOKEN ult join USER_AUTHORITY ua "
	  		+ "ON DATE(ult.created_time) between (:from) and (:to) "
	  		+ "AND ua.authority_name in  (:authorities) "
	  		+ "AND ult.user_id = ua.user_id "
	  		+ "GROUP BY DATE(ult.created_time),ult.user_id,ua.authority_name"),
	@NamedNativeQuery(resultSetMapping="AnalyticsMappingForYear", name = "getAnalyticsForYear", 
	  query = "SELECT CONCAT(DATE_FORMAT(ult.created_time,'%b'),'"+"''"+"',DATE_FORMAT(ult.created_time,'%y')) AS month,"
	  		+ "count(distinct(ult.user_id)) loginCount,ua.authority_name authority "
	  		+ "FROM USER_LOGIN_TOKEN ult join USER_AUTHORITY ua "
	  		+ "ON DATE(ult.created_time) between (:from) and (:to) "
	  		+ "AND ua.authority_name in  (:authorities) "
	  		+ "AND ult.user_id = ua.user_id "
	  		+ "GROUP BY month,ua.authority_name")
})
@SqlResultSetMappings(value={
	@SqlResultSetMapping(
		    name = "AnalyticsMappingForWeekOrDay",
		    classes = {
		        @ConstructorResult(targetClass = LoginAnalyticsVO.class,
		            columns = {
		                @ColumnResult(name = "date", type = org.jadira.usertype.dateandtime.joda.PersistentLocalDate.class),
		                @ColumnResult(name = "loginCount", type = Integer.class),
		                @ColumnResult(name = "authority", type = String.class)
		            }
		        )
		    }
		),
	@SqlResultSetMapping(
		    name = "AnalyticsMappingForYear",
		    classes = {
		        @ConstructorResult(targetClass = LoginAnalyticsVO.class,
		            columns = {
		                @ColumnResult(name = "month", type = String.class),
		                @ColumnResult(name = "loginCount", type = Integer.class),
		                @ColumnResult(name = "authority", type = String.class)
		            }
		        )
		    }
		),
	@SqlResultSetMapping(
		    name = "AnalyticsMappingForMonth",
		    classes = {
		        @ConstructorResult(targetClass = LoginAnalyticsVO.class,
		            columns = {
		                @ColumnResult(name = "date", type = org.jadira.usertype.dateandtime.joda.PersistentLocalDate.class),
		                @ColumnResult(name = "loginCount", type = Integer.class),
		                @ColumnResult(name = "authority", type = String.class),
		                @ColumnResult(name = "userId", type = Long.class)
		            }
		        )
		    }
		)
})
public class UserLoginToken implements Serializable {

    @Id
    private String id;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "created_time")
    private DateTime createdTime = DateTime.now();

    @ManyToOne
    private User user;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "last_modified_time")
    private DateTime lastModifiedTime = DateTime.now();

    @Column(name="ip_address")
    private String ipAddress;
    
    @Column(name="is_expired")
    private boolean isExpired;
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(DateTime createdTime) {
        this.createdTime = createdTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
	public DateTime getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(DateTime lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public boolean isExpired() {
		return isExpired;
	}

	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}

    public int getDuration() {
		return Math.round((lastModifiedTime.getMillis()- createdTime.getMillis())/(1000*60));
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserLoginToken userLoginToken = (UserLoginToken) o;

        if ( ! Objects.equals(id, userLoginToken.id)) return false;

        return true;
    }

	@Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserLoginToken{" +
                "id=" + id +
                ", createdTime='" + createdTime + "'" +
                '}';
    }
}
