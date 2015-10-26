package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserSecurityQuestion is a Querydsl query type for QUserSecurityQuestion
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUserSecurityQuestion extends com.mysema.query.sql.RelationalPathBase<QUserSecurityQuestion> {

    private static final long serialVersionUID = -1715678958;

    public static final QUserSecurityQuestion userSecurityQuestion = new QUserSecurityQuestion("USER_SECURITY_QUESTION");

    public final StringPath answer = createString("answer");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> questionId = createNumber("questionId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QUserSecurityQuestion> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QSecurityQuestion> usqQuestionIdFk = createForeignKey(questionId, "id");

    public final com.mysema.query.sql.ForeignKey<QUser> usqUserIdFk = createForeignKey(userId, "id");

    public QUserSecurityQuestion(String variable) {
        super(QUserSecurityQuestion.class, forVariable(variable), "null", "USER_SECURITY_QUESTION");
        addMetadata();
    }

    public QUserSecurityQuestion(String variable, String schema, String table) {
        super(QUserSecurityQuestion.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserSecurityQuestion(Path<? extends QUserSecurityQuestion> path) {
        super(path.getType(), path.getMetadata(), "null", "USER_SECURITY_QUESTION");
        addMetadata();
    }

    public QUserSecurityQuestion(PathMetadata<?> metadata) {
        super(QUserSecurityQuestion.class, metadata, "null", "USER_SECURITY_QUESTION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(answer, ColumnMetadata.named("answer").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(questionId, ColumnMetadata.named("question_id").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

