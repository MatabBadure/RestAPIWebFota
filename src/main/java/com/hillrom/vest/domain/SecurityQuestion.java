package com.hillrom.vest.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * A SecurityQuestion.
 */
@Entity
@Table(name = "SECURITY_QUESTION")
public class SecurityQuestion implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "question")
    private String question;
    
    @OneToOne(mappedBy = "securityQuestion",fetch=FetchType.LAZY)
    @JsonIgnore
    private UserSecurityQuestion securityQuestion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SecurityQuestion securityQuestion = (SecurityQuestion) o;

        if ( ! Objects.equals(id, securityQuestion.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SecurityQuestion{" +
                "id=" + id +
                ", question='" + question + "'" +
                '}';
    }
}
