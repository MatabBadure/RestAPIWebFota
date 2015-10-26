package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QClinic is a Querydsl query type for QClinic
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QClinic extends com.mysema.query.sql.RelationalPathBase<QClinic> {

    private static final long serialVersionUID = 984138377;

    public static final QClinic clinic = new QClinic("CLINIC");

    public final StringPath address = createString("address");

    public final StringPath city = createString("city");

    public final NumberPath<Long> clinicAdminId = createNumber("clinicAdminId", Long.class);

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final StringPath faxNumber = createString("faxNumber");

    public final StringPath hillromId = createString("hillromId");

    public final StringPath id = createString("id");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final BooleanPath isParent = createBoolean("isParent");

    public final StringPath name = createString("name");

    public final StringPath parentClinicId = createString("parentClinicId");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath state = createString("state");

    public final NumberPath<Integer> zipcode = createNumber("zipcode", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QClinic> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QClinic> qm1hoeyivvhgfjnr85545sybqFK = createForeignKey(parentClinicId, "id");

    public final com.mysema.query.sql.ForeignKey<QUser> clinicAdminUserIdFk = createForeignKey(clinicAdminId, "id");

    public final com.mysema.query.sql.ForeignKey<QClinicPatientAssoc> _cpaClinicIdFk = createInvForeignKey(id, "clinic_id");

    public final com.mysema.query.sql.ForeignKey<QClinic> _qm1hoeyivvhgfjnr85545sybqFK = createInvForeignKey(id, "parent_clinic_id");

    public final com.mysema.query.sql.ForeignKey<QClinicPatientAssocAud> _mgegmv7wca724bnphk4wel741FK = createInvForeignKey(id, "CLINIC_ID");

    public final com.mysema.query.sql.ForeignKey<QClinicUserAssoc> _cuaClinicIdFk = createInvForeignKey(id, "clinics_id");

    public QClinic(String variable) {
        super(QClinic.class, forVariable(variable), "null", "CLINIC");
        addMetadata();
    }

    public QClinic(String variable, String schema, String table) {
        super(QClinic.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QClinic(Path<? extends QClinic> path) {
        super(path.getType(), path.getMetadata(), "null", "CLINIC");
        addMetadata();
    }

    public QClinic(PathMetadata<?> metadata) {
        super(QClinic.class, metadata, "null", "CLINIC");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(address, ColumnMetadata.named("address").withIndex(3).ofType(Types.VARCHAR).withSize(255));
        addMetadata(city, ColumnMetadata.named("city").withIndex(5).ofType(Types.VARCHAR).withSize(255));
        addMetadata(clinicAdminId, ColumnMetadata.named("clinic_admin_id").withIndex(13).ofType(Types.BIGINT).withSize(19));
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(14).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(faxNumber, ColumnMetadata.named("fax_number").withIndex(8).ofType(Types.VARCHAR).withSize(20));
        addMetadata(hillromId, ColumnMetadata.named("hillrom_id").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(isDeleted, ColumnMetadata.named("is_deleted").withIndex(11).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(isParent, ColumnMetadata.named("is_parent").withIndex(12).ofType(Types.BIT).withSize(1));
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(255));
        addMetadata(parentClinicId, ColumnMetadata.named("parent_clinic_id").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(phoneNumber, ColumnMetadata.named("phone_number").withIndex(7).ofType(Types.VARCHAR).withSize(20));
        addMetadata(state, ColumnMetadata.named("state").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(zipcode, ColumnMetadata.named("zipcode").withIndex(4).ofType(Types.INTEGER).withSize(10));
    }

}

