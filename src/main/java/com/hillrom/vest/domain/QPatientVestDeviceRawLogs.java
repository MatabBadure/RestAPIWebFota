package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientVestDeviceRawLogs is a Querydsl query type for QPatientVestDeviceRawLogs
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientVestDeviceRawLogs extends com.mysema.query.sql.RelationalPathBase<QPatientVestDeviceRawLogs> {

    private static final long serialVersionUID = 1927517741;

    public static final QPatientVestDeviceRawLogs patientVestDeviceRawLogs = new QPatientVestDeviceRawLogs("PATIENT_VEST_DEVICE_RAW_LOGS");

    public final StringPath airInterfaceType = createString("airInterfaceType");

    public final StringPath cucVersion = createString("cucVersion");

    public final StringPath customerId = createString("customerId");

    public final StringPath customerName = createString("customerName");

    public final StringPath deviceAddress = createString("deviceAddress");

    public final StringPath deviceData = createString("deviceData");

    public final StringPath deviceModelType = createString("deviceModelType");

    public final StringPath deviceSerialNumber = createString("deviceSerialNumber");

    public final StringPath deviceType = createString("deviceType");

    public final StringPath hubId = createString("hubId");

    public final NumberPath<Long> hubReceiveTime = createNumber("hubReceiveTime", Long.class);

    public final NumberPath<Integer> hubReceiveTimeOffset = createNumber("hubReceiveTimeOffset", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath rawMessage = createString("rawMessage");

    public final NumberPath<Long> spReceiveTime = createNumber("spReceiveTime", Long.class);

    public final StringPath timezone = createString("timezone");

    public final com.mysema.query.sql.PrimaryKey<QPatientVestDeviceRawLogs> primary = createPrimaryKey(id);

    public QPatientVestDeviceRawLogs(String variable) {
        super(QPatientVestDeviceRawLogs.class, forVariable(variable), "null", "PATIENT_VEST_DEVICE_RAW_LOGS");
        addMetadata();
    }

    public QPatientVestDeviceRawLogs(String variable, String schema, String table) {
        super(QPatientVestDeviceRawLogs.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientVestDeviceRawLogs(Path<? extends QPatientVestDeviceRawLogs> path) {
        super(path.getType(), path.getMetadata(), "null", "PATIENT_VEST_DEVICE_RAW_LOGS");
        addMetadata();
    }

    public QPatientVestDeviceRawLogs(PathMetadata<?> metadata) {
        super(QPatientVestDeviceRawLogs.class, metadata, "null", "PATIENT_VEST_DEVICE_RAW_LOGS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(airInterfaceType, ColumnMetadata.named("air_interface_type").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(cucVersion, ColumnMetadata.named("cuc_version").withIndex(14).ofType(Types.VARCHAR).withSize(255));
        addMetadata(customerId, ColumnMetadata.named("customer_id").withIndex(15).ofType(Types.VARCHAR).withSize(255));
        addMetadata(customerName, ColumnMetadata.named("customer_name").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(deviceAddress, ColumnMetadata.named("device_address").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(deviceData, ColumnMetadata.named("device_data").withIndex(5).ofType(Types.LONGVARCHAR).withSize(16777215));
        addMetadata(deviceModelType, ColumnMetadata.named("device_model_type").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(deviceSerialNumber, ColumnMetadata.named("device_serial_number").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(deviceType, ColumnMetadata.named("device_type").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(hubId, ColumnMetadata.named("hub_id").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(hubReceiveTime, ColumnMetadata.named("hub_receive_time").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(hubReceiveTimeOffset, ColumnMetadata.named("hub_receive_time_offset").withIndex(13).ofType(Types.INTEGER).withSize(10));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(rawMessage, ColumnMetadata.named("raw_message").withIndex(16).ofType(Types.LONGVARCHAR).withSize(2147483647));
        addMetadata(spReceiveTime, ColumnMetadata.named("sp_receive_time").withIndex(12).ofType(Types.BIGINT).withSize(19));
        addMetadata(timezone, ColumnMetadata.named("timezone").withIndex(11).ofType(Types.VARCHAR).withSize(255));
    }

}

