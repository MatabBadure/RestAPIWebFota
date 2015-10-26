package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientProtocolData is a Querydsl query type for QPatientProtocolData
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientProtocolData extends com.mysema.query.sql.RelationalPathBase<QPatientProtocolData> {

    private static final long serialVersionUID = -880867834;

    public static final QPatientProtocolData patientProtocolData = new QPatientProtocolData("PATIENT_PROTOCOL_DATA");

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final StringPath id = createString("id");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final NumberPath<Long> maxFrequency = createNumber("maxFrequency", Long.class);

    public final NumberPath<Long> maxMinutesPerTreatment = createNumber("maxMinutesPerTreatment", Long.class);

    public final NumberPath<Long> maxPressure = createNumber("maxPressure", Long.class);

    public final NumberPath<Long> minFrequency = createNumber("minFrequency", Long.class);

    public final NumberPath<Long> minMinutesPerTreatment = createNumber("minMinutesPerTreatment", Long.class);

    public final NumberPath<Long> minPressure = createNumber("minPressure", Long.class);

    public final StringPath patientId = createString("patientId");

    public final StringPath protocolKey = createString("protocolKey");

    public final StringPath treatmentLabel = createString("treatmentLabel");

    public final NumberPath<Long> treatmentsPerDay = createNumber("treatmentsPerDay", Long.class);

    public final StringPath type = createString("type");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QPatientProtocolData> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> ppdPatientIdFk = createForeignKey(patientId, "id");

    public final com.mysema.query.sql.ForeignKey<QUser> ppdUserIdFk = createForeignKey(userId, "id");

    public QPatientProtocolData(String variable) {
        super(QPatientProtocolData.class, forVariable(variable), "null", "PATIENT_PROTOCOL_DATA");
        addMetadata();
    }

    public QPatientProtocolData(String variable, String schema, String table) {
        super(QPatientProtocolData.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientProtocolData(Path<? extends QPatientProtocolData> path) {
        super(path.getType(), path.getMetadata(), "null", "PATIENT_PROTOCOL_DATA");
        addMetadata();
    }

    public QPatientProtocolData(PathMetadata<?> metadata) {
        super(QPatientProtocolData.class, metadata, "null", "PATIENT_PROTOCOL_DATA");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(13).ofType(Types.VARCHAR).withSize(50));
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(14).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.VARCHAR).withSize(45).notNull());
        addMetadata(isDeleted, ColumnMetadata.named("is_deleted").withIndex(17).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(15).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(16).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(maxFrequency, ColumnMetadata.named("max_frequency").withIndex(10).ofType(Types.BIGINT).withSize(19));
        addMetadata(maxMinutesPerTreatment, ColumnMetadata.named("max_minutes_per_treatment").withIndex(8).ofType(Types.BIGINT).withSize(19));
        addMetadata(maxPressure, ColumnMetadata.named("max_pressure").withIndex(12).ofType(Types.BIGINT).withSize(19));
        addMetadata(minFrequency, ColumnMetadata.named("min_frequency").withIndex(9).ofType(Types.BIGINT).withSize(19));
        addMetadata(minMinutesPerTreatment, ColumnMetadata.named("min_minutes_per_treatment").withIndex(7).ofType(Types.BIGINT).withSize(19));
        addMetadata(minPressure, ColumnMetadata.named("min_pressure").withIndex(11).ofType(Types.BIGINT).withSize(19));
        addMetadata(patientId, ColumnMetadata.named("patient_id").withIndex(2).ofType(Types.VARCHAR).withSize(45).notNull());
        addMetadata(protocolKey, ColumnMetadata.named("protocol_key").withIndex(18).ofType(Types.VARCHAR).withSize(45).notNull());
        addMetadata(treatmentLabel, ColumnMetadata.named("treatment_label").withIndex(6).ofType(Types.VARCHAR).withSize(45));
        addMetadata(treatmentsPerDay, ColumnMetadata.named("treatments_per_day").withIndex(5).ofType(Types.BIGINT).withSize(19));
        addMetadata(type, ColumnMetadata.named("type").withIndex(4).ofType(Types.VARCHAR).withSize(10));
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

