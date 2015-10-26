package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QClinicPatientAssoc is a Querydsl query type for QClinicPatientAssoc
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QClinicPatientAssoc extends com.mysema.query.sql.RelationalPathBase<QClinicPatientAssoc> {

    private static final long serialVersionUID = 1102626105;

    public static final QClinicPatientAssoc clinicPatientAssoc = new QClinicPatientAssoc("CLINIC_PATIENT_ASSOC");

    public final StringPath clinicId = createString("clinicId");

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final StringPath mrnId = createString("mrnId");

    public final StringPath notes = createString("notes");

    public final StringPath patientId = createString("patientId");

    public final com.mysema.query.sql.PrimaryKey<QClinicPatientAssoc> primary = createPrimaryKey(clinicId, patientId);

    public final com.mysema.query.sql.ForeignKey<QClinic> cpaClinicIdFk = createForeignKey(clinicId, "id");

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> cpaPatientsIdFk = createForeignKey(patientId, "id");

    public QClinicPatientAssoc(String variable) {
        super(QClinicPatientAssoc.class, forVariable(variable), "null", "CLINIC_PATIENT_ASSOC");
        addMetadata();
    }

    public QClinicPatientAssoc(String variable, String schema, String table) {
        super(QClinicPatientAssoc.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QClinicPatientAssoc(Path<? extends QClinicPatientAssoc> path) {
        super(path.getType(), path.getMetadata(), "null", "CLINIC_PATIENT_ASSOC");
        addMetadata();
    }

    public QClinicPatientAssoc(PathMetadata<?> metadata) {
        super(QClinicPatientAssoc.class, metadata, "null", "CLINIC_PATIENT_ASSOC");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(clinicId, ColumnMetadata.named("clinic_id").withIndex(1).ofType(Types.VARCHAR).withSize(45).notNull());
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(5).ofType(Types.VARCHAR).withSize(50));
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(6).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(7).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(8).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(mrnId, ColumnMetadata.named("mrn_id").withIndex(3).ofType(Types.VARCHAR).withSize(45));
        addMetadata(notes, ColumnMetadata.named("notes").withIndex(4).ofType(Types.VARCHAR).withSize(45));
        addMetadata(patientId, ColumnMetadata.named("patient_id").withIndex(2).ofType(Types.VARCHAR).withSize(45).notNull());
    }

}

