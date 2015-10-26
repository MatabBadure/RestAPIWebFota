package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QSecurityQuestion is a Querydsl query type for QSecurityQuestion
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QSecurityQuestion extends com.mysema.query.sql.RelationalPathBase<QSecurityQuestion> {

    private static final long serialVersionUID = 731492263;

    public static final QSecurityQuestion securityQuestion = new QSecurityQuestion("SECURITY_QUESTION");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath question = createString("question");

    public final com.mysema.query.sql.PrimaryKey<QSecurityQuestion> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QUserSecurityQuestion> _usqQuestionIdFk = createInvForeignKey(id, "question_id");

    public QSecurityQuestion(String variable) {
        super(QSecurityQuestion.class, forVariable(variable), "null", "SECURITY_QUESTION");
        addMetadata();
    }

    public QSecurityQuestion(String variable, String schema, String table) {
        super(QSecurityQuestion.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QSecurityQuestion(Path<? extends QSecurityQuestion> path) {
        super(path.getType(), path.getMetadata(), "null", "SECURITY_QUESTION");
        addMetadata();
    }

    public QSecurityQuestion(PathMetadata<?> metadata) {
        super(QSecurityQuestion.class, metadata, "null", "SECURITY_QUESTION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(question, ColumnMetadata.named("question").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

