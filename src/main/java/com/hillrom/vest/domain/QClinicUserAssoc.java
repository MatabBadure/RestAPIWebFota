package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QClinicUserAssoc is a Querydsl query type for QClinicUserAssoc
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QClinicUserAssoc extends com.mysema.query.sql.RelationalPathBase<QClinicUserAssoc> {

    private static final long serialVersionUID = 795270785;

    public static final QClinicUserAssoc clinicUserAssoc = new QClinicUserAssoc("CLINIC_USER_ASSOC");

    public final StringPath clinicsId = createString("clinicsId");

    public final NumberPath<Long> usersId = createNumber("usersId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QClinicUserAssoc> primary = createPrimaryKey(clinicsId, usersId);

    public final com.mysema.query.sql.ForeignKey<QClinic> cuaClinicIdFk = createForeignKey(clinicsId, "id");

    public final com.mysema.query.sql.ForeignKey<QUser> cuaUserIdFk = createForeignKey(usersId, "id");

    public final com.mysema.query.sql.ForeignKey<QUserExtension> _46roc6696st3dqo8e84dux3u3FK = createForeignKey(usersId, "user_id");

    public QClinicUserAssoc(String variable) {
        super(QClinicUserAssoc.class, forVariable(variable), "null", "CLINIC_USER_ASSOC");
        addMetadata();
    }

    public QClinicUserAssoc(String variable, String schema, String table) {
        super(QClinicUserAssoc.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QClinicUserAssoc(Path<? extends QClinicUserAssoc> path) {
        super(path.getType(), path.getMetadata(), "null", "CLINIC_USER_ASSOC");
        addMetadata();
    }

    public QClinicUserAssoc(PathMetadata<?> metadata) {
        super(QClinicUserAssoc.class, metadata, "null", "CLINIC_USER_ASSOC");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(clinicsId, ColumnMetadata.named("clinics_id").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(usersId, ColumnMetadata.named("users_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

