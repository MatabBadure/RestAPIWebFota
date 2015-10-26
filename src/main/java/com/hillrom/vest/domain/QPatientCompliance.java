package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientCompliance is a Querydsl query type for QPatientCompliance
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientCompliance extends com.mysema.query.sql.RelationalPathBase<QPatientCompliance> {

    private static final long serialVersionUID = 220620991;

    public static final QPatientCompliance patientCompliance = new QPatientCompliance("PATIENT_COMPLIANCE");

    public final NumberPath<Integer> complianceScore = createNumber("complianceScore", Integer.class);

    public final DatePath<java.sql.Date> date = createDate("date", java.sql.Date.class);

    public final NumberPath<Long> hmr = createNumber("hmr", Long.class);

    public final NumberPath<Integer> hmrRunRate = createNumber("hmrRunRate", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isHmrCompliant = createBoolean("isHmrCompliant");

    public final BooleanPath isSettingsDeviated = createBoolean("isSettingsDeviated");

    public final DatePath<java.sql.Date> lastTherapySessionDate = createDate("lastTherapySessionDate", java.sql.Date.class);

    public final NumberPath<Integer> missedTherapyCount = createNumber("missedTherapyCount", Integer.class);

    public final StringPath patientId = createString("patientId");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QPatientCompliance> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> pATIENTCOMPLIANCEPatientIdFk = createForeignKey(patientId, "id");

    public final com.mysema.query.sql.ForeignKey<QUser> pATIENTCOMPLIANCEUserIdFk = createForeignKey(userId, "id");

    public QPatientCompliance(String variable) {
        super(QPatientCompliance.class, forVariable(variable), "null", "PATIENT_COMPLIANCE");
        addMetadata();
    }

    public QPatientCompliance(String variable, String schema, String table) {
        super(QPatientCompliance.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientCompliance(Path<? extends QPatientCompliance> path) {
        super(path.getType(), path.getMetadata(), "null", "PATIENT_COMPLIANCE");
        addMetadata();
    }

    public QPatientCompliance(PathMetadata<?> metadata) {
        super(QPatientCompliance.class, metadata, "null", "PATIENT_COMPLIANCE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(complianceScore, ColumnMetadata.named("compliance_score").withIndex(5).ofType(Types.INTEGER).withSize(10));
        addMetadata(date, ColumnMetadata.named("date").withIndex(4).ofType(Types.DATE).withSize(10));
        addMetadata(hmr, ColumnMetadata.named("hmr").withIndex(7).ofType(Types.DECIMAL).withSize(10));
        addMetadata(hmrRunRate, ColumnMetadata.named("hmr_run_rate").withIndex(6).ofType(Types.INTEGER).withSize(10));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(isHmrCompliant, ColumnMetadata.named("is_hmr_compliant").withIndex(8).ofType(Types.BIT).withSize(1));
        addMetadata(isSettingsDeviated, ColumnMetadata.named("is_settings_deviated").withIndex(9).ofType(Types.BIT).withSize(1));
        addMetadata(lastTherapySessionDate, ColumnMetadata.named("last_therapy_session_date").withIndex(11).ofType(Types.DATE).withSize(10));
        addMetadata(missedTherapyCount, ColumnMetadata.named("missed_therapy_count").withIndex(10).ofType(Types.INTEGER).withSize(10));
        addMetadata(patientId, ColumnMetadata.named("patient_id").withIndex(2).ofType(Types.VARCHAR).withSize(45).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

