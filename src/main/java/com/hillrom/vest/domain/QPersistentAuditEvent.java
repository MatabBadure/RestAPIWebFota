package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPersistentAuditEvent is a Querydsl query type for QPersistentAuditEvent
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPersistentAuditEvent extends com.mysema.query.sql.RelationalPathBase<QPersistentAuditEvent> {

    private static final long serialVersionUID = 566112599;

    public static final QPersistentAuditEvent persistentAuditEvent = new QPersistentAuditEvent("PERSISTENT_AUDIT_EVENT");

    public final DateTimePath<java.sql.Timestamp> eventDate = createDateTime("eventDate", java.sql.Timestamp.class);

    public final NumberPath<Long> eventId = createNumber("eventId", Long.class);

    public final StringPath eventType = createString("eventType");

    public final StringPath principal = createString("principal");

    public final com.mysema.query.sql.PrimaryKey<QPersistentAuditEvent> primary = createPrimaryKey(eventId);

    public final com.mysema.query.sql.ForeignKey<QPersistentAuditEvtData> _evtPersAuditEvtDataFk = createInvForeignKey(eventId, "event_id");

    public QPersistentAuditEvent(String variable) {
        super(QPersistentAuditEvent.class, forVariable(variable), "null", "PERSISTENT_AUDIT_EVENT");
        addMetadata();
    }

    public QPersistentAuditEvent(String variable, String schema, String table) {
        super(QPersistentAuditEvent.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPersistentAuditEvent(Path<? extends QPersistentAuditEvent> path) {
        super(path.getType(), path.getMetadata(), "null", "PERSISTENT_AUDIT_EVENT");
        addMetadata();
    }

    public QPersistentAuditEvent(PathMetadata<?> metadata) {
        super(QPersistentAuditEvent.class, metadata, "null", "PERSISTENT_AUDIT_EVENT");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(eventDate, ColumnMetadata.named("event_date").withIndex(3).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(eventId, ColumnMetadata.named("event_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(eventType, ColumnMetadata.named("event_type").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(principal, ColumnMetadata.named("principal").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

