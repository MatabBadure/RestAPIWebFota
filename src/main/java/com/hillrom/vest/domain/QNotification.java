package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QNotification is a Querydsl query type for QNotification
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QNotification extends com.mysema.query.sql.RelationalPathBase<QNotification> {

    private static final long serialVersionUID = -970606132;

    public static final QNotification notification = new QNotification("NOTIFICATION");

    public final DatePath<java.sql.Date> date = createDate("date", java.sql.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAcknowledged = createBoolean("isAcknowledged");

    public final StringPath notificationType = createString("notificationType");

    public final StringPath patientId = createString("patientId");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QNotification> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> notificationPatientIdFk = createForeignKey(patientId, "id");

    public final com.mysema.query.sql.ForeignKey<QUser> notificationUserIdFk = createForeignKey(userId, "id");

    public QNotification(String variable) {
        super(QNotification.class, forVariable(variable), "null", "NOTIFICATION");
        addMetadata();
    }

    public QNotification(String variable, String schema, String table) {
        super(QNotification.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QNotification(Path<? extends QNotification> path) {
        super(path.getType(), path.getMetadata(), "null", "NOTIFICATION");
        addMetadata();
    }

    public QNotification(PathMetadata<?> metadata) {
        super(QNotification.class, metadata, "null", "NOTIFICATION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(date, ColumnMetadata.named("date").withIndex(4).ofType(Types.DATE).withSize(10));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(isAcknowledged, ColumnMetadata.named("is_acknowledged").withIndex(6).ofType(Types.BIT).withSize(1));
        addMetadata(notificationType, ColumnMetadata.named("notification_type").withIndex(5).ofType(Types.VARCHAR).withSize(50));
        addMetadata(patientId, ColumnMetadata.named("patient_id").withIndex(2).ofType(Types.VARCHAR).withSize(45).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

