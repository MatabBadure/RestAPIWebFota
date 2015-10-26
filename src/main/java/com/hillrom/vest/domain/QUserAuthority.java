package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserAuthority is a Querydsl query type for QUserAuthority
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUserAuthority extends com.mysema.query.sql.RelationalPathBase<QUserAuthority> {

    private static final long serialVersionUID = 2143238903;

    public static final QUserAuthority userAuthority = new QUserAuthority("USER_AUTHORITY");

    public final StringPath authorityName = createString("authorityName");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QUserAuthority> primary = createPrimaryKey(authorityName, userId);

    public final com.mysema.query.sql.ForeignKey<QAuthority> _7hl8ovqlkkowqlxhfo4m48iojFK = createForeignKey(authorityName, "name");

    public final com.mysema.query.sql.ForeignKey<QUser> userIdFk = createForeignKey(userId, "id");

    public QUserAuthority(String variable) {
        super(QUserAuthority.class, forVariable(variable), "null", "USER_AUTHORITY");
        addMetadata();
    }

    public QUserAuthority(String variable, String schema, String table) {
        super(QUserAuthority.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserAuthority(Path<? extends QUserAuthority> path) {
        super(path.getType(), path.getMetadata(), "null", "USER_AUTHORITY");
        addMetadata();
    }

    public QUserAuthority(PathMetadata<?> metadata) {
        super(QUserAuthority.class, metadata, "null", "USER_AUTHORITY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(authorityName, ColumnMetadata.named("authority_name").withIndex(2).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

