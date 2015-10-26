package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QDatabasechangeloglock is a Querydsl query type for QDatabasechangeloglock
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QDatabasechangeloglock extends com.mysema.query.sql.RelationalPathBase<QDatabasechangeloglock> {

    private static final long serialVersionUID = 1312241507;

    public static final QDatabasechangeloglock databasechangeloglock = new QDatabasechangeloglock("DATABASECHANGELOGLOCK");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final BooleanPath locked = createBoolean("locked");

    public final StringPath lockedby = createString("lockedby");

    public final DateTimePath<java.sql.Timestamp> lockgranted = createDateTime("lockgranted", java.sql.Timestamp.class);

    public final com.mysema.query.sql.PrimaryKey<QDatabasechangeloglock> primary = createPrimaryKey(id);

    public QDatabasechangeloglock(String variable) {
        super(QDatabasechangeloglock.class, forVariable(variable), "null", "DATABASECHANGELOGLOCK");
        addMetadata();
    }

    public QDatabasechangeloglock(String variable, String schema, String table) {
        super(QDatabasechangeloglock.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QDatabasechangeloglock(Path<? extends QDatabasechangeloglock> path) {
        super(path.getType(), path.getMetadata(), "null", "DATABASECHANGELOGLOCK");
        addMetadata();
    }

    public QDatabasechangeloglock(PathMetadata<?> metadata) {
        super(QDatabasechangeloglock.class, metadata, "null", "DATABASECHANGELOGLOCK");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(locked, ColumnMetadata.named("LOCKED").withIndex(2).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(lockedby, ColumnMetadata.named("LOCKEDBY").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(lockgranted, ColumnMetadata.named("LOCKGRANTED").withIndex(3).ofType(Types.TIMESTAMP).withSize(19));
    }

}

