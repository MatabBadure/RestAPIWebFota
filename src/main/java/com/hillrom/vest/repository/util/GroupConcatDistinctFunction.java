package com.hillrom.vest.repository.util;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class GroupConcatDistinctFunction implements SQLFunction {

@Override
public boolean hasArguments() {
    return true;
}

@Override
public boolean hasParenthesesIfNoArguments() {
    return true;
}

@Override
public Type getReturnType(Type firstArgumentType, Mapping mapping)
        throws QueryException {
    return StandardBasicTypes.STRING;
}


@Override
public String render(Type firstArgumentType, List arguments,
        SessionFactoryImplementor factory) throws QueryException {
    //if (arguments.size() != 1) {
    //    throw new QueryException(new IllegalArgumentException(
    //            "group_concat shoudl have one arg"));
    //}
    return "group_concat(distinct(" + arguments.get(0) + ") SEPARATOR ', ' )";
}

}
