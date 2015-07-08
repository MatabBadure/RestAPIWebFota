package com.hillrom.vest.web.rest.dto;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

public class HillromTeamUserDTO {
	
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

    private String role;

	public HillromTeamUserDTO() {
		super();
	}

	public HillromTeamUserDTO(String title, String firstName,
			String middleName, String lastName, String email, String role) {
		this.title = title;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.role = role;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
    
}
