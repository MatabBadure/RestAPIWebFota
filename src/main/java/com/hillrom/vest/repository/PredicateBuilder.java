package com.hillrom.vest.repository;

import java.util.ArrayList;
import java.util.List;

import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.path.PathBuilder;

public class PredicateBuilder<T> {
    private List<SearchCriteria> params;
 
    private Predicate<T> predicate;
    
    private PathBuilder<T> entityPathBuilder;
    
    public PredicateBuilder(Class<T> type,String entityName) {        
    	entityPathBuilder = new PathBuilder<T>(type,entityName);
    	params = new ArrayList<SearchCriteria>();
    }
 
    public PredicateBuilder<T> with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }
 
    public BooleanExpression build() {
        if (params.size() == 0) {
            return null;
        }
        List<BooleanExpression> predicates = new ArrayList<BooleanExpression>();
        for (SearchCriteria param : params) {
            predicate = new Predicate<T>(param,entityPathBuilder);
            BooleanExpression exp = predicate.getPredicate();
            if (exp != null) {
                predicates.add(exp);
            }
        }
        BooleanExpression result = predicates.get(0);
        for (int i = 1; i < predicates.size(); i++) {
            result = result.and(predicates.get(i));
        }
        return result;
    }
}
