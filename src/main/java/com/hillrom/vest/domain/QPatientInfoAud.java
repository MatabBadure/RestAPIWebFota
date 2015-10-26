package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientInfoAud is a Querydsl query type for QPatientInfoAud
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientInfoAud extends com.mysema.query.sql.RelationalPathBase<QPatientInfoAud> {

    private static final long serialVersionUID = 1664278782;

    public static final QPatientInfoAud patientInfoAud = new QPatientInfoAud("PATIENT_INFO_AUD");

    public final StringPath address = createString("address");

    public final StringPath bluetoothId = createString("bluetoothId");

    public final StringPath city = createString("city");

    public final DateTimePath<java.sql.Timestamp> deviceAssocDate = createDateTime("deviceAssocDate", java.sql.Timestamp.class);

    public final DatePath<java.sql.Date> dob = createDate("dob", java.sql.Date.class);

    public final StringPath email = createString("email");

    public final BooleanPath expired = createBoolean("expired");

    public final DateTimePath<java.sql.Timestamp> expiredDate = createDateTime("expiredDate", java.sql.Timestamp.class);

    public final StringPath firstName = createString("firstName");

    public final StringPath gender = createString("gender");

    public final StringPath hillromId = createString("hillromId");

    public final StringPath hubId = createString("hubId");

    public final StringPath id = createString("id");

    public final StringPath langKey = createString("langKey");

    public final StringPath lastName = createString("lastName");

    public final StringPath middleName = createString("middleName");

    public final StringPath mobilePhone = createString("mobilePhone");

    public final StringPath primaryPhone = createString("primaryPhone");

    public final NumberPath<Integer> rev = createNumber("rev", Integer.class);

    public final NumberPath<Byte> revtype = createNumber("revtype", Byte.class);

    public final StringPath serialNumber = createString("serialNumber");

    public final StringPath state = createString("state");

    public final StringPath title = createString("title");

    public final BooleanPath webLoginCreated = createBoolean("webLoginCreated");

    public final NumberPath<Integer> zipcode = createNumber("zipcode", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QPatientInfoAud> primary = createPrimaryKey(rev, id);

    public final com.mysema.query.sql.ForeignKey<QAuditRevisionInfo> k7rbakwkymf7gavfqlaoar9u4FK = createForeignKey(rev, "id");

    public QPatientInfoAud(String variable) {
        super(QPatientInfoAud.class, forVariable(variable), "null", "PATIENT_INFO_AUD");
        addMetadata();
    }

    public QPatientInfoAud(String variable, String schema, String table) {
        super(QPatientInfoAud.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientInfoAud(Path<? extends QPatientInfoAud> path) {
        super(path.getType(), path.getMetadata(), "null", "PATIENT_INFO_AUD");
        addMetadata();
    }

    public QPatientInfoAud(PathMetadata<?> metadata) {
        super(QPatientInfoAud.class, metadata, "null", "PATIENT_INFO_AUD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(address, ColumnMetadata.named("address").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(bluetoothId, ColumnMetadata.named("bluetooth_id").withIndex(5).ofType(Types.VARCHAR).withSize(255));
        addMetadata(city, ColumnMetadata.named("city").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(deviceAssocDate, ColumnMetadata.named("device_assoc_date").withIndex(7).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(dob, ColumnMetadata.named("dob").withIndex(8).ofType(Types.DATE).withSize(10));
        addMetadata(email, ColumnMetadata.named("email").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(expired, ColumnMetadata.named("expired").withIndex(10).ofType(Types.BIT).withSize(1));
        addMetadata(expiredDate, ColumnMetadata.named("expired_date").withIndex(11).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(firstName, ColumnMetadata.named("first_name").withIndex(12).ofType(Types.VARCHAR).withSize(255));
        addMetadata(gender, ColumnMetadata.named("gender").withIndex(13).ofType(Types.VARCHAR).withSize(255));
        addMetadata(hillromId, ColumnMetadata.named("hillrom_id").withIndex(14).ofType(Types.VARCHAR).withSize(255));
        addMetadata(hubId, ColumnMetadata.named("hub_id").withIndex(15).ofType(Types.VARCHAR).withSize(255));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(langKey, ColumnMetadata.named("lang_key").withIndex(16).ofType(Types.VARCHAR).withSize(255));
        addMetadata(lastName, ColumnMetadata.named("last_name").withIndex(17).ofType(Types.VARCHAR).withSize(255));
        addMetadata(middleName, ColumnMetadata.named("middle_name").withIndex(18).ofType(Types.VARCHAR).withSize(255));
        addMetadata(mobilePhone, ColumnMetadata.named("mobile_phone").withIndex(19).ofType(Types.VARCHAR).withSize(255));
        addMetadata(primaryPhone, ColumnMetadata.named("primary_phone").withIndex(20).ofType(Types.VARCHAR).withSize(255));
        addMetadata(rev, ColumnMetadata.named("REV").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(revtype, ColumnMetadata.named("REVTYPE").withIndex(3).ofType(Types.TINYINT).withSize(3));
        addMetadata(serialNumber, ColumnMetadata.named("serial_number").withIndex(21).ofType(Types.VARCHAR).withSize(255));
        addMetadata(state, ColumnMetadata.named("state").withIndex(22).ofType(Types.VARCHAR).withSize(255));
        addMetadata(title, ColumnMetadata.named("title").withIndex(23).ofType(Types.VARCHAR).withSize(255));
        addMetadata(webLoginCreated, ColumnMetadata.named("web_login_created").withIndex(24).ofType(Types.BIT).withSize(1));
        addMetadata(zipcode, ColumnMetadata.named("zipcode").withIndex(25).ofType(Types.INTEGER).withSize(10));
    }

}

