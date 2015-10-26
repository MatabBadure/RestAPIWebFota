package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientInfo is a Querydsl query type for QPatientInfo
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientInfo extends com.mysema.query.sql.RelationalPathBase<QPatientInfo> {

    private static final long serialVersionUID = -772118446;

    public static final QPatientInfo patientInfo = new QPatientInfo("PATIENT_INFO");

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

    public final StringPath serialNumber = createString("serialNumber");

    public final StringPath state = createString("state");

    public final StringPath title = createString("title");

    public final BooleanPath webLoginCreated = createBoolean("webLoginCreated");

    public final NumberPath<Integer> zipcode = createNumber("zipcode", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QPatientInfo> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QClinicPatientAssoc> _cpaPatientsIdFk = createInvForeignKey(id, "patient_id");

    public final com.mysema.query.sql.ForeignKey<QPatientCompliance> _pATIENTCOMPLIANCEPatientIdFk = createInvForeignKey(id, "patient_id");

    public final com.mysema.query.sql.ForeignKey<QPatientVestTherapyData> _pvtdPatientIdFk = createInvForeignKey(id, "patient_id");

    public final com.mysema.query.sql.ForeignKey<QPatientNote> _patNotePatientIdFk = createInvForeignKey(id, "patient_id");

    public final com.mysema.query.sql.ForeignKey<QPatientVestDeviceHistory> _pvdPatientIdFk = createInvForeignKey(id, "patient_id");

    public final com.mysema.query.sql.ForeignKey<QPatientVestDeviceData> _patientVestDeviceDataPatientIdFk = createInvForeignKey(id, "patient_id");

    public final com.mysema.query.sql.ForeignKey<QUserPatientAssocAud> _iahpahj2r4hixson0nb7d2pi2FK = createInvForeignKey(id, "PATIENT_ID");

    public final com.mysema.query.sql.ForeignKey<QUserPatientAssoc> _upaPatientIdFk = createInvForeignKey(id, "patient_id");

    public final com.mysema.query.sql.ForeignKey<QPatientNoEvent> _org2ed4ir5ef4c125gvatvqu4FK = createInvForeignKey(id, "patient_id");

    public final com.mysema.query.sql.ForeignKey<QPatientProtocolData> _ppdPatientIdFk = createInvForeignKey(id, "patient_id");

    public final com.mysema.query.sql.ForeignKey<QClinicPatientAssocAud> __2tdtgkivexj56eh0pb84fb7m0FK = createInvForeignKey(id, "PATIENT_ID");

    public final com.mysema.query.sql.ForeignKey<QNotification> _notificationPatientIdFk = createInvForeignKey(id, "patient_id");

    public QPatientInfo(String variable) {
        super(QPatientInfo.class, forVariable(variable), "null", "PATIENT_INFO");
        addMetadata();
    }

    public QPatientInfo(String variable, String schema, String table) {
        super(QPatientInfo.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientInfo(Path<? extends QPatientInfo> path) {
        super(path.getType(), path.getMetadata(), "null", "PATIENT_INFO");
        addMetadata();
    }

    public QPatientInfo(PathMetadata<?> metadata) {
        super(QPatientInfo.class, metadata, "null", "PATIENT_INFO");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(address, ColumnMetadata.named("address").withIndex(20).ofType(Types.VARCHAR).withSize(255));
        addMetadata(bluetoothId, ColumnMetadata.named("bluetooth_id").withIndex(5).ofType(Types.VARCHAR).withSize(255));
        addMetadata(city, ColumnMetadata.named("city").withIndex(21).ofType(Types.VARCHAR).withSize(255));
        addMetadata(deviceAssocDate, ColumnMetadata.named("device_assoc_date").withIndex(23).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(dob, ColumnMetadata.named("dob").withIndex(10).ofType(Types.DATE).withSize(10));
        addMetadata(email, ColumnMetadata.named("email").withIndex(11).ofType(Types.VARCHAR).withSize(255));
        addMetadata(expired, ColumnMetadata.named("expired").withIndex(18).ofType(Types.BIT).withSize(1));
        addMetadata(expiredDate, ColumnMetadata.named("expired_date").withIndex(19).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(firstName, ColumnMetadata.named("first_name").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(gender, ColumnMetadata.named("gender").withIndex(16).ofType(Types.VARCHAR).withSize(10));
        addMetadata(hillromId, ColumnMetadata.named("hillrom_id").withIndex(2).ofType(Types.VARCHAR).withSize(255));
        addMetadata(hubId, ColumnMetadata.named("hub_id").withIndex(3).ofType(Types.VARCHAR).withSize(255));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(langKey, ColumnMetadata.named("lang_key").withIndex(17).ofType(Types.VARCHAR).withSize(10));
        addMetadata(lastName, ColumnMetadata.named("last_name").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(middleName, ColumnMetadata.named("middle_name").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(mobilePhone, ColumnMetadata.named("mobile_phone").withIndex(15).ofType(Types.VARCHAR).withSize(20));
        addMetadata(primaryPhone, ColumnMetadata.named("primary_phone").withIndex(14).ofType(Types.VARCHAR).withSize(20));
        addMetadata(serialNumber, ColumnMetadata.named("serial_number").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(state, ColumnMetadata.named("state").withIndex(22).ofType(Types.VARCHAR).withSize(255));
        addMetadata(title, ColumnMetadata.named("title").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(webLoginCreated, ColumnMetadata.named("web_login_created").withIndex(13).ofType(Types.BIT).withSize(1));
        addMetadata(zipcode, ColumnMetadata.named("zipcode").withIndex(12).ofType(Types.INTEGER).withSize(10));
    }

}

