package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserPatientAssoc is a Querydsl query type for QUserPatientAssoc
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUserPatientAssoc extends com.mysema.query.sql.RelationalPathBase<QUserPatientAssoc> {

    private static final long serialVersionUID = -1576080420;

    public static final QUserPatientAssoc userPatientAssoc = new QUserPatientAssoc("USER_PATIENT_ASSOC");

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final StringPath patientId = createString("patientId");

    public final StringPath relationLabel = createString("relationLabel");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath userRole = createString("userRole");

    public final com.mysema.query.sql.PrimaryKey<QUserPatientAssoc> primary = createPrimaryKey(patientId, userId);

    public final com.mysema.query.sql.ForeignKey<QUser> upaUserIdFk = createForeignKey(userId, "id");

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> upaPatientIdFk = createForeignKey(patientId, "id");

    public QUserPatientAssoc(String variable) {
        super(QUserPatientAssoc.class, forVariable(variable), "null", "USER_PATIENT_ASSOC");
        addMetadata();
    }

    public QUserPatientAssoc(String variable, String schema, String table) {
        super(QUserPatientAssoc.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserPatientAssoc(Path<? extends QUserPatientAssoc> path) {
        super(path.getType(), path.getMetadata(), "null", "USER_PATIENT_ASSOC");
        addMetadata();
    }

    public QUserPatientAssoc(PathMetadata<?> metadata) {
        super(QUserPatientAssoc.class, metadata, "null", "USER_PATIENT_ASSOC");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(5).ofType(Types.VARCHAR).withSize(50));
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(6).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(7).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(8).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(patientId, ColumnMetadata.named("patient_id").withIndex(2).ofType(Types.VARCHAR).withSize(45).notNull());
        addMetadata(relationLabel, ColumnMetadata.named("relation_label").withIndex(4).ofType(Types.VARCHAR).withSize(45));
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(userRole, ColumnMetadata.named("user_role").withIndex(3).ofType(Types.VARCHAR).withSize(45));
    }

}

