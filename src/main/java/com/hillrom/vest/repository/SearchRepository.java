package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository<T> {
  List<T> findBy(SearchCriteria<T> criteria);
}
