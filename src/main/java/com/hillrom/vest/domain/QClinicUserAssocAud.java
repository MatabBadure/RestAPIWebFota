package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QClinicUserAssocAud is a Querydsl query type for QClinicUserAssocAud
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QClinicUserAssocAud extends com.mysema.query.sql.RelationalPathBase<QClinicUserAssocAud> {

    private static final long serialVersionUID = 872417391;

    public static final QClinicUserAssocAud clinicUserAssocAud = new QClinicUserAssocAud("CLINIC_USER_ASSOC_AUD");

    public final StringPath clinicsId = createString("clinicsId");

    public final NumberPath<Integer> rev = createNumber("rev", Integer.class);

    public final NumberPath<Byte> revtype = createNumber("revtype", Byte.class);

    public final NumberPath<Long> usersId = createNumber("usersId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QClinicUserAssocAud> primary = createPrimaryKey(rev, clinicsId, usersId);

    public final com.mysema.query.sql.ForeignKey<QAuditRevisionInfo> _82impe23ub6gsmwto7qnsw14tFK = createForeignKey(rev, "id");

    public QClinicUserAssocAud(String variable) {
        super(QClinicUserAssocAud.class, forVariable(variable), "null", "CLINIC_USER_ASSOC_AUD");
        addMetadata();
    }

    public QClinicUserAssocAud(String variable, String schema, String table) {
        super(QClinicUserAssocAud.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QClinicUserAssocAud(Path<? extends QClinicUserAssocAud> path) {
        super(path.getType(), path.getMetadata(), "null", "CLINIC_USER_ASSOC_AUD");
        addMetadata();
    }

    public QClinicUserAssocAud(PathMetadata<?> metadata) {
        super(QClinicUserAssocAud.class, metadata, "null", "CLINIC_USER_ASSOC_AUD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(clinicsId, ColumnMetadata.named("clinics_id").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(rev, ColumnMetadata.named("REV").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(revtype, ColumnMetadata.named("REVTYPE").withIndex(4).ofType(Types.TINYINT).withSize(3));
        addMetadata(usersId, ColumnMetadata.named("users_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

