package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QAuthority is a Querydsl query type for QAuthority
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QAuthority extends com.mysema.query.sql.RelationalPathBase<QAuthority> {

    private static final long serialVersionUID = -148084222;

    public static final QAuthority authority = new QAuthority("AUTHORITY");

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QAuthority> primary = createPrimaryKey(name);

    public final com.mysema.query.sql.ForeignKey<QUserAuthority> __7hl8ovqlkkowqlxhfo4m48iojFK = createInvForeignKey(name, "authority_name");

    public QAuthority(String variable) {
        super(QAuthority.class, forVariable(variable), "null", "AUTHORITY");
        addMetadata();
    }

    public QAuthority(String variable, String schema, String table) {
        super(QAuthority.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QAuthority(Path<? extends QAuthority> path) {
        super(path.getType(), path.getMetadata(), "null", "AUTHORITY");
        addMetadata();
    }

    public QAuthority(PathMetadata<?> metadata) {
        super(QAuthority.class, metadata, "null", "AUTHORITY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("name").withIndex(1).ofType(Types.VARCHAR).withSize(50).notNull());
    }

}

