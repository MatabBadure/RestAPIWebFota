package com.hillrom.vest.web.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

public class UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 100;

    @NotNull
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    @Size(max = 50)
    private String title;
    
    @Size(max = 50)
    private String firstName;
    
    @Size(max = 50)
    private String middleName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    private String email;
    
    @Size(max = 50)
    private String gender;
    
    @Size(max = 50)
    private Integer zipcode;
    
    @Size(min = 2, max = 5)
    private String langKey;

    private List<String> roles;
    
    private Boolean termsConditionAccepted;

    public UserDTO() {
    }

	public UserDTO(String password, String title, String firstName,
			String middleName, String lastName, String email, String gender,
			Integer zipcode, String langKey, List<String> roles) {
		super();
		this.password = password;
		this.title = title;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.gender = gender;
		this.zipcode = zipcode;
		this.langKey = langKey;
		this.roles = roles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getZipcode() {
		return zipcode;
	}

	public void setZipcode(Integer zipcode) {
		this.zipcode = zipcode;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Boolean getTermsConditionAccepted() {
		return termsConditionAccepted;
	}

	public void setTermsConditionAccepted(Boolean termsConditionAccepted) {
		this.termsConditionAccepted = termsConditionAccepted;
	}

	@Override
	public String toString() {
		return "UserDTO [password=" + password + ", title=" + title
				+ ", firstName=" + firstName + ", middleName=" + middleName
				+ ", lastName=" + lastName + ", email=" + email + ", gender="
				+ gender + ", zipcode=" + zipcode + ", langKey=" + langKey
				+ ", roles=" + roles + ", termsConditionAccepted="
				+ termsConditionAccepted + "]";
	}

}
