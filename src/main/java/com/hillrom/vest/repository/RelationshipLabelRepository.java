package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.RelationshipLabel;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface RelationshipLabelRepository extends JpaRepository<RelationshipLabel, String> {
}
