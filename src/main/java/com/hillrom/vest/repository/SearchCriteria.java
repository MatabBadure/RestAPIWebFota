package com.hillrom.vest.repository;

public class SearchCriteria<T> {

	private Class<T> entityClass;
	private String searchString;
	private int pageNo = 0;
	private int maxResults = 10;
	private String query;
	
	public SearchCriteria(Class<T> entityClass, String searchString,
			int pageNo, int maxResults) {
		super();
		this.entityClass = entityClass;
		this.searchString = searchString;
		this.pageNo = pageNo;
		this.maxResults = maxResults;
	}
	
	public Class<T> getEntityClass() {
		return entityClass;
	}

	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	
	
	
}
