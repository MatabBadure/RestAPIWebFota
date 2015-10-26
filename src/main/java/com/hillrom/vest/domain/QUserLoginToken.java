package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserLoginToken is a Querydsl query type for QUserLoginToken
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUserLoginToken extends com.mysema.query.sql.RelationalPathBase<QUserLoginToken> {

    private static final long serialVersionUID = 1758680796;

    public static final QUserLoginToken userLoginToken = new QUserLoginToken("USER_LOGIN_TOKEN");

    public final DateTimePath<java.sql.Timestamp> createdTime = createDateTime("createdTime", java.sql.Timestamp.class);

    public final StringPath id = createString("id");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QUserLoginToken> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QUser> userlogintokenUserIdFk = createForeignKey(userId, "id");

    public QUserLoginToken(String variable) {
        super(QUserLoginToken.class, forVariable(variable), "null", "USER_LOGIN_TOKEN");
        addMetadata();
    }

    public QUserLoginToken(String variable, String schema, String table) {
        super(QUserLoginToken.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserLoginToken(Path<? extends QUserLoginToken> path) {
        super(path.getType(), path.getMetadata(), "null", "USER_LOGIN_TOKEN");
        addMetadata();
    }

    public QUserLoginToken(PathMetadata<?> metadata) {
        super(QUserLoginToken.class, metadata, "null", "USER_LOGIN_TOKEN");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createdTime, ColumnMetadata.named("created_time").withIndex(2).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(3).ofType(Types.BIGINT).withSize(19));
    }

}

