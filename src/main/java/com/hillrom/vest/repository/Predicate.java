package com.hillrom.vest.repository;

import org.apache.commons.lang.StringUtils;

import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.StringPath;

public class Predicate<T> {
	 
    private SearchCriteria criteria;
    
    private PathBuilder<T> entityPath;
    
    public Predicate(SearchCriteria criteria, PathBuilder<T> entityPath) {
		super();
		this.criteria = criteria;
		this.entityPath = entityPath;
	}

	public BooleanExpression getPredicate() {
 
        if (StringUtils.isNumeric(criteria.getValue().toString())) {
            NumberPath<Integer> path = entityPath.getNumber(criteria.getKey(), Integer.class);
            int value = Integer.parseInt(criteria.getValue().toString());
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                return path.eq(value);
            } 
            else if (criteria.getOperation().equalsIgnoreCase(">")) {
                return path.goe(value);
            } 
            else if (criteria.getOperation().equalsIgnoreCase("<")) {
                return path.loe(value);
            }
        } 
        else {
            StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                return path.containsIgnoreCase(criteria.getValue().toString());
            }
        }
        return null;
    }
}