package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientVestDeviceDataTemp is a Querydsl query type for QPatientVestDeviceDataTemp
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientVestDeviceDataTemp extends com.mysema.query.sql.RelationalPathBase<QPatientVestDeviceDataTemp> {

    private static final long serialVersionUID = 926667048;

    public static final QPatientVestDeviceDataTemp patientVestDeviceDataTemp = new QPatientVestDeviceDataTemp("patient_vest_device_data_temp");

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

    public final com.mysema.query.sql.PrimaryKey<QPatientVestDeviceDataTemp> primary = createPrimaryKey(eventId, sequenceNumber, timestamp);

    public QPatientVestDeviceDataTemp(String variable) {
        super(QPatientVestDeviceDataTemp.class, forVariable(variable), "null", "patient_vest_device_data_temp");
        addMetadata();
    }

    public QPatientVestDeviceDataTemp(String variable, String schema, String table) {
        super(QPatientVestDeviceDataTemp.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientVestDeviceDataTemp(Path<? extends QPatientVestDeviceDataTemp> path) {
        super(path.getType(), path.getMetadata(), "null", "patient_vest_device_data_temp");
        addMetadata();
    }

    public QPatientVestDeviceDataTemp(PathMetadata<?> metadata) {
        super(QPatientVestDeviceDataTemp.class, metadata, "null", "patient_vest_device_data_temp");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(bluetoothId, ColumnMetadata.named("bluetooth_id").withIndex(6).ofType(Types.VARCHAR).withSize(255));
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

