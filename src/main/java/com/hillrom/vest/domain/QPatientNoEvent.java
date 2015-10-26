package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientNoEvent is a Querydsl query type for QPatientNoEvent
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientNoEvent extends com.mysema.query.sql.RelationalPathBase<QPatientNoEvent> {

    private static final long serialVersionUID = 1805225781;

    public static final QPatientNoEvent patientNoEvent = new QPatientNoEvent("PATIENT_NO_EVENT");

    public final DatePath<java.sql.Date> firstTransmissionDate = createDate("firstTransmissionDate", java.sql.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath patientId = createString("patientId");

    public final DatePath<java.sql.Date> userCreatedDate = createDate("userCreatedDate", java.sql.Date.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QPatientNoEvent> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QUser> sd5m0ftnm3kscdq0uj6wnkndbFK = createForeignKey(userId, "id");

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> org2ed4ir5ef4c125gvatvqu4FK = createForeignKey(patientId, "id");

    public QPatientNoEvent(String variable) {
        super(QPatientNoEvent.class, forVariable(variable), "null", "PATIENT_NO_EVENT");
        addMetadata();
    }

    public QPatientNoEvent(String variable, String schema, String table) {
        super(QPatientNoEvent.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientNoEvent(Path<? extends QPatientNoEvent> path) {
        super(path.getType(), path.getMetadata(), "null", "PATIENT_NO_EVENT");
        addMetadata();
    }

    public QPatientNoEvent(PathMetadata<?> metadata) {
        super(QPatientNoEvent.class, metadata, "null", "PATIENT_NO_EVENT");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(firstTransmissionDate, ColumnMetadata.named("first_transmission_date").withIndex(2).ofType(Types.DATE).withSize(10));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(patientId, ColumnMetadata.named("patient_id").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(userCreatedDate, ColumnMetadata.named("user_created_date").withIndex(3).ofType(Types.DATE).withSize(10));
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(5).ofType(Types.BIGINT).withSize(19));
    }

}

