package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserPatientAssocAud is a Querydsl query type for QUserPatientAssocAud
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUserPatientAssocAud extends com.mysema.query.sql.RelationalPathBase<QUserPatientAssocAud> {

    private static final long serialVersionUID = -429246156;

    public static final QUserPatientAssocAud userPatientAssocAud = new QUserPatientAssocAud("USER_PATIENT_ASSOC_AUD");

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final StringPath patientId = createString("patientId");

    public final StringPath relationLabel = createString("relationLabel");

    public final NumberPath<Integer> rev = createNumber("rev", Integer.class);

    public final NumberPath<Byte> revtype = createNumber("revtype", Byte.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath userRole = createString("userRole");

    public final com.mysema.query.sql.PrimaryKey<QUserPatientAssocAud> primary = createPrimaryKey(patientId, rev, userId);

    public final com.mysema.query.sql.ForeignKey<QUser> n5e5hf7jqpkbu608v426l3wkkFK = createForeignKey(userId, "id");

    public final com.mysema.query.sql.ForeignKey<QAuditRevisionInfo> n8wsefgvxj90hk4s7ig5nimlvFK = createForeignKey(rev, "id");

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> iahpahj2r4hixson0nb7d2pi2FK = createForeignKey(patientId, "id");

    public QUserPatientAssocAud(String variable) {
        super(QUserPatientAssocAud.class, forVariable(variable), "null", "USER_PATIENT_ASSOC_AUD");
        addMetadata();
    }

    public QUserPatientAssocAud(String variable, String schema, String table) {
        super(QUserPatientAssocAud.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserPatientAssocAud(Path<? extends QUserPatientAssocAud> path) {
        super(path.getType(), path.getMetadata(), "null", "USER_PATIENT_ASSOC_AUD");
        addMetadata();
    }

    public QUserPatientAssocAud(PathMetadata<?> metadata) {
        super(QUserPatientAssocAud.class, metadata, "null", "USER_PATIENT_ASSOC_AUD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(5).ofType(Types.VARCHAR).withSize(50));
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(6).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(7).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(8).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(patientId, ColumnMetadata.named("PATIENT_ID").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(relationLabel, ColumnMetadata.named("relation_label").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(rev, ColumnMetadata.named("REV").withIndex(3).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(revtype, ColumnMetadata.named("REVTYPE").withIndex(4).ofType(Types.TINYINT).withSize(3));
        addMetadata(userId, ColumnMetadata.named("USER_ID").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(userRole, ColumnMetadata.named("user_role").withIndex(10).ofType(Types.VARCHAR).withSize(255));
    }

}

