package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QClinicAud is a Querydsl query type for QClinicAud
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QClinicAud extends com.mysema.query.sql.RelationalPathBase<QClinicAud> {

    private static final long serialVersionUID = 1019692903;

    public static final QClinicAud clinicAud = new QClinicAud("CLINIC_AUD");

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

    public final StringPath phoneNumber = createString("phoneNumber");

    public final NumberPath<Integer> rev = createNumber("rev", Integer.class);

    public final NumberPath<Byte> revtype = createNumber("revtype", Byte.class);

    public final StringPath state = createString("state");

    public final NumberPath<Integer> zipcode = createNumber("zipcode", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QClinicAud> primary = createPrimaryKey(rev, id);

    public final com.mysema.query.sql.ForeignKey<QAuditRevisionInfo> _6uqloh2984lk1e2q7gtvdpnugFK = createForeignKey(rev, "id");

    public QClinicAud(String variable) {
        super(QClinicAud.class, forVariable(variable), "null", "CLINIC_AUD");
        addMetadata();
    }

    public QClinicAud(String variable, String schema, String table) {
        super(QClinicAud.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QClinicAud(Path<? extends QClinicAud> path) {
        super(path.getType(), path.getMetadata(), "null", "CLINIC_AUD");
        addMetadata();
    }

    public QClinicAud(PathMetadata<?> metadata) {
        super(QClinicAud.class, metadata, "null", "CLINIC_AUD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(address, ColumnMetadata.named("address").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(city, ColumnMetadata.named("city").withIndex(5).ofType(Types.VARCHAR).withSize(255));
        addMetadata(clinicAdminId, ColumnMetadata.named("clinic_admin_id").withIndex(6).ofType(Types.BIGINT).withSize(19));
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(7).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(faxNumber, ColumnMetadata.named("fax_number").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(hillromId, ColumnMetadata.named("hillrom_id").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(isDeleted, ColumnMetadata.named("is_deleted").withIndex(8).ofType(Types.BIT).withSize(1));
        addMetadata(isParent, ColumnMetadata.named("is_parent").withIndex(12).ofType(Types.BIT).withSize(1));
        addMetadata(name, ColumnMetadata.named("name").withIndex(11).ofType(Types.VARCHAR).withSize(255));
        addMetadata(phoneNumber, ColumnMetadata.named("phone_number").withIndex(13).ofType(Types.VARCHAR).withSize(255));
        addMetadata(rev, ColumnMetadata.named("REV").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(revtype, ColumnMetadata.named("REVTYPE").withIndex(3).ofType(Types.TINYINT).withSize(3));
        addMetadata(state, ColumnMetadata.named("state").withIndex(14).ofType(Types.VARCHAR).withSize(255));
        addMetadata(zipcode, ColumnMetadata.named("zipcode").withIndex(15).ofType(Types.INTEGER).withSize(10));
    }

}

