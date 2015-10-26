package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientVestTherapyData is a Querydsl query type for QPatientVestTherapyData
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientVestTherapyData extends com.mysema.query.sql.RelationalPathBase<QPatientVestTherapyData> {

    private static final long serialVersionUID = 1033800223;

    public static final QPatientVestTherapyData patientVestTherapyData = new QPatientVestTherapyData("PATIENT_VEST_THERAPY_DATA");

    public final NumberPath<Integer> caughPauseDuration = createNumber("caughPauseDuration", Integer.class);

    public final DatePath<java.sql.Date> date = createDate("date", java.sql.Date.class);

    public final NumberPath<Long> durationInMinutes = createNumber("durationInMinutes", Long.class);

    public final DateTimePath<java.sql.Timestamp> endTime = createDateTime("endTime", java.sql.Timestamp.class);

    public final NumberPath<Long> frequency = createNumber("frequency", Long.class);

    public final NumberPath<Double> hmr = createNumber("hmr", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> normalCaughPauses = createNumber("normalCaughPauses", Integer.class);

    public final StringPath patientId = createString("patientId");

    public final NumberPath<Long> pressure = createNumber("pressure", Long.class);

    public final NumberPath<Integer> programmedCaughPauses = createNumber("programmedCaughPauses", Integer.class);

    public final NumberPath<Integer> sessionNo = createNumber("sessionNo", Integer.class);

    public final StringPath sessionType = createString("sessionType");

    public final DateTimePath<java.sql.Timestamp> startTime = createDateTime("startTime", java.sql.Timestamp.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QPatientVestTherapyData> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> pvtdPatientIdFk = createForeignKey(patientId, "id");

    public final com.mysema.query.sql.ForeignKey<QUser> pvtdUserIdFk = createForeignKey(userId, "id");

    public QPatientVestTherapyData(String variable) {
        super(QPatientVestTherapyData.class, forVariable(variable), "null", "PATIENT_VEST_THERAPY_DATA");
        addMetadata();
    }

    public QPatientVestTherapyData(String variable, String schema, String table) {
        super(QPatientVestTherapyData.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientVestTherapyData(Path<? extends QPatientVestTherapyData> path) {
        super(path.getType(), path.getMetadata(), "null", "PATIENT_VEST_THERAPY_DATA");
        addMetadata();
    }

    public QPatientVestTherapyData(PathMetadata<?> metadata) {
        super(QPatientVestTherapyData.class, metadata, "null", "PATIENT_VEST_THERAPY_DATA");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(caughPauseDuration, ColumnMetadata.named("caugh_pause_duration").withIndex(14).ofType(Types.INTEGER).withSize(10));
        addMetadata(date, ColumnMetadata.named("date").withIndex(4).ofType(Types.DATE).withSize(10));
        addMetadata(durationInMinutes, ColumnMetadata.named("duration_in_minutes").withIndex(11).ofType(Types.BIGINT).withSize(19));
        addMetadata(endTime, ColumnMetadata.named("end_time").withIndex(8).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(frequency, ColumnMetadata.named("frequency").withIndex(9).ofType(Types.DECIMAL).withSize(10));
        addMetadata(hmr, ColumnMetadata.named("hmr").withIndex(15).ofType(Types.DOUBLE).withSize(22));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(normalCaughPauses, ColumnMetadata.named("normal_caugh_pauses").withIndex(13).ofType(Types.INTEGER).withSize(10));
        addMetadata(patientId, ColumnMetadata.named("patient_id").withIndex(2).ofType(Types.VARCHAR).withSize(45).notNull());
        addMetadata(pressure, ColumnMetadata.named("pressure").withIndex(10).ofType(Types.DECIMAL).withSize(10));
        addMetadata(programmedCaughPauses, ColumnMetadata.named("programmed_caugh_pauses").withIndex(12).ofType(Types.INTEGER).withSize(10));
        addMetadata(sessionNo, ColumnMetadata.named("session_no").withIndex(6).ofType(Types.INTEGER).withSize(10));
        addMetadata(sessionType, ColumnMetadata.named("session_type").withIndex(5).ofType(Types.VARCHAR).withSize(50));
        addMetadata(startTime, ColumnMetadata.named("start_time").withIndex(7).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

