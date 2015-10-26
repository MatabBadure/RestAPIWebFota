package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QClinicPatientAssocAud is a Querydsl query type for QClinicPatientAssocAud
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QClinicPatientAssocAud extends com.mysema.query.sql.RelationalPathBase<QClinicPatientAssocAud> {

    private static final long serialVersionUID = 424480439;

    public static final QClinicPatientAssocAud clinicPatientAssocAud = new QClinicPatientAssocAud("CLINIC_PATIENT_ASSOC_AUD");

    public final StringPath clinicId = createString("clinicId");

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final StringPath mrnId = createString("mrnId");

    public final StringPath notes = createString("notes");

    public final StringPath patientId = createString("patientId");

    public final NumberPath<Integer> rev = createNumber("rev", Integer.class);

    public final NumberPath<Byte> revtype = createNumber("revtype", Byte.class);

    public final com.mysema.query.sql.PrimaryKey<QClinicPatientAssocAud> primary = createPrimaryKey(clinicId, patientId, rev);

    public final com.mysema.query.sql.ForeignKey<QClinic> mgegmv7wca724bnphk4wel741FK = createForeignKey(clinicId, "id");

    public final com.mysema.query.sql.ForeignKey<QAuditRevisionInfo> qbyvmgplui716dy405bbswxgcFK = createForeignKey(rev, "id");

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> _2tdtgkivexj56eh0pb84fb7m0FK = createForeignKey(patientId, "id");

    public QClinicPatientAssocAud(String variable) {
        super(QClinicPatientAssocAud.class, forVariable(variable), "null", "CLINIC_PATIENT_ASSOC_AUD");
        addMetadata();
    }

    public QClinicPatientAssocAud(String variable, String schema, String table) {
        super(QClinicPatientAssocAud.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QClinicPatientAssocAud(Path<? extends QClinicPatientAssocAud> path) {
        super(path.getType(), path.getMetadata(), "null", "CLINIC_PATIENT_ASSOC_AUD");
        addMetadata();
    }

    public QClinicPatientAssocAud(PathMetadata<?> metadata) {
        super(QClinicPatientAssocAud.class, metadata, "null", "CLINIC_PATIENT_ASSOC_AUD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(clinicId, ColumnMetadata.named("CLINIC_ID").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(5).ofType(Types.VARCHAR).withSize(50));
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(6).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(7).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(8).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(mrnId, ColumnMetadata.named("mrn_id").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(notes, ColumnMetadata.named("notes").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(patientId, ColumnMetadata.named("PATIENT_ID").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(rev, ColumnMetadata.named("REV").withIndex(3).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(revtype, ColumnMetadata.named("REVTYPE").withIndex(4).ofType(Types.TINYINT).withSize(3));
    }

}

