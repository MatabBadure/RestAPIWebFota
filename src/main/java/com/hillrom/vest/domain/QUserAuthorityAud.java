package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserAuthorityAud is a Querydsl query type for QUserAuthorityAud
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUserAuthorityAud extends com.mysema.query.sql.RelationalPathBase<QUserAuthorityAud> {

    private static final long serialVersionUID = 246403129;

    public static final QUserAuthorityAud userAuthorityAud = new QUserAuthorityAud("USER_AUTHORITY_AUD");

    public final StringPath authorityName = createString("authorityName");

    public final NumberPath<Integer> rev = createNumber("rev", Integer.class);

    public final NumberPath<Byte> revtype = createNumber("revtype", Byte.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QUserAuthorityAud> primary = createPrimaryKey(rev, authorityName, userId);

    public final com.mysema.query.sql.ForeignKey<QAuditRevisionInfo> ghsw331opr6gcwma6849v76u6FK = createForeignKey(rev, "id");

    public QUserAuthorityAud(String variable) {
        super(QUserAuthorityAud.class, forVariable(variable), "null", "USER_AUTHORITY_AUD");
        addMetadata();
    }

    public QUserAuthorityAud(String variable, String schema, String table) {
        super(QUserAuthorityAud.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserAuthorityAud(Path<? extends QUserAuthorityAud> path) {
        super(path.getType(), path.getMetadata(), "null", "USER_AUTHORITY_AUD");
        addMetadata();
    }

    public QUserAuthorityAud(PathMetadata<?> metadata) {
        super(QUserAuthorityAud.class, metadata, "null", "USER_AUTHORITY_AUD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(authorityName, ColumnMetadata.named("authority_name").withIndex(3).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(rev, ColumnMetadata.named("REV").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(revtype, ColumnMetadata.named("REVTYPE").withIndex(4).ofType(Types.TINYINT).withSize(3));
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

