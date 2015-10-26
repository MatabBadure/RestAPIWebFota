package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPersistentAuditEvtData is a Querydsl query type for QPersistentAuditEvtData
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPersistentAuditEvtData extends com.mysema.query.sql.RelationalPathBase<QPersistentAuditEvtData> {

    private static final long serialVersionUID = -1414051926;

    public static final QPersistentAuditEvtData persistentAuditEvtData = new QPersistentAuditEvtData("PERSISTENT_AUDIT_EVT_DATA");

    public final NumberPath<Long> eventId = createNumber("eventId", Long.class);

    public final StringPath name = createString("name");

    public final StringPath value = createString("value");

    public final com.mysema.query.sql.PrimaryKey<QPersistentAuditEvtData> primary = createPrimaryKey(eventId, name);

    public final com.mysema.query.sql.ForeignKey<QPersistentAuditEvent> evtPersAuditEvtDataFk = createForeignKey(eventId, "event_id");

    public QPersistentAuditEvtData(String variable) {
        super(QPersistentAuditEvtData.class, forVariable(variable), "null", "PERSISTENT_AUDIT_EVT_DATA");
        addMetadata();
    }

    public QPersistentAuditEvtData(String variable, String schema, String table) {
        super(QPersistentAuditEvtData.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPersistentAuditEvtData(Path<? extends QPersistentAuditEvtData> path) {
        super(path.getType(), path.getMetadata(), "null", "PERSISTENT_AUDIT_EVT_DATA");
        addMetadata();
    }

    public QPersistentAuditEvtData(PathMetadata<?> metadata) {
        super(QPersistentAuditEvtData.class, metadata, "null", "PERSISTENT_AUDIT_EVT_DATA");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(eventId, ColumnMetadata.named("event_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(value, ColumnMetadata.named("value").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    }

}

