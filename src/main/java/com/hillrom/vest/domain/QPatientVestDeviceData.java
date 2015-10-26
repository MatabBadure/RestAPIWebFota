package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientVestDeviceData is a Querydsl query type for QPatientVestDeviceData
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientVestDeviceData extends com.mysema.query.sql.RelationalPathBase<QPatientVestDeviceData> {

    private static final long serialVersionUID = -1025977548;

    public static final QPatientVestDeviceData patientVestDeviceData = new QPatientVestDeviceData("PATIENT_VEST_DEVICE_DATA");

    public final StringPath bluetoothId = createString("bluetoothId");

    public final NumberPath<Integer> checksum = createNumber("checksum", Integer.class);

    public final NumberPath<Integer> duration = createNumber("duration", Integer.class);

    public final StringPath eventId = createString("eventId");

    public final NumberPath<Integer> frequency = createNumber("frequency", Integer.class);

    public final NumberPath<Long> hmr = createNumber("hmr", Long.class);

    public final StringPath hubId = createString("hubId");

    public final StringPath patientId = createString("patientId");

    public final NumberPath<Integer> pressure = createNumber("pressure", Integer.class);

    public final NumberPath<Integer> sequenceNumber = createNumber("sequenceNumber", Integer.class);

    public final StringPath serialNumber = createString("serialNumber");

    public final NumberPath<Long> timestamp = createNumber("timestamp", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QPatientVestDeviceData> primary = createPrimaryKey(bluetoothId, eventId, timestamp);

    public final com.mysema.query.sql.ForeignKey<QUser> pvddUserIdFk = createForeignKey(userId, "id");

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> patientVestDeviceDataPatientIdFk = createForeignKey(patientId, "id");

    public QPatientVestDeviceData(String variable) {
        super(QPatientVestDeviceData.class, forVariable(variable), "null", "PATIENT_VEST_DEVICE_DATA");
        addMetadata();
    }

    public QPatientVestDeviceData(String variable, String schema, String table) {
        super(QPatientVestDeviceData.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientVestDeviceData(Path<? extends QPatientVestDeviceData> path) {
        super(path.getType(), path.getMetadata(), "null", "PATIENT_VEST_DEVICE_DATA");
        addMetadata();
    }

    public QPatientVestDeviceData(PathMetadata<?> metadata) {
        super(QPatientVestDeviceData.class, metadata, "null", "PATIENT_VEST_DEVICE_DATA");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(bluetoothId, ColumnMetadata.named("bluetooth_id").withIndex(6).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(checksum, ColumnMetadata.named("checksum").withIndex(12).ofType(Types.INTEGER).withSize(10));
        addMetadata(duration, ColumnMetadata.named("duration").withIndex(11).ofType(Types.INTEGER).withSize(10));
        addMetadata(eventId, ColumnMetadata.named("event_id").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(frequency, ColumnMetadata.named("frequency").withIndex(9).ofType(Types.INTEGER).withSize(10));
        addMetadata(hmr, ColumnMetadata.named("hmr").withIndex(8).ofType(Types.DECIMAL).withSize(10));
        addMetadata(hubId, ColumnMetadata.named("hub_id").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(patientId, ColumnMetadata.named("patient_id").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(pressure, ColumnMetadata.named("pressure").withIndex(10).ofType(Types.INTEGER).withSize(10));
        addMetadata(sequenceNumber, ColumnMetadata.named("sequence_number").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(serialNumber, ColumnMetadata.named("serial_number").withIndex(5).ofType(Types.VARCHAR).withSize(255));
        addMetadata(timestamp, ColumnMetadata.named("timestamp").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(13).ofType(Types.BIGINT).withSize(19));
    }

}

