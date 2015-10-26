package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientVestDeviceHistory is a Querydsl query type for QPatientVestDeviceHistory
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientVestDeviceHistory extends com.mysema.query.sql.RelationalPathBase<QPatientVestDeviceHistory> {

    private static final long serialVersionUID = 1868946634;

    public static final QPatientVestDeviceHistory patientVestDeviceHistory = new QPatientVestDeviceHistory("PATIENT_VEST_DEVICE_HISTORY");

    public final StringPath bluetoothId = createString("bluetoothId");

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final StringPath hubId = createString("hubId");

    public final BooleanPath isActive = createBoolean("isActive");

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final StringPath patientId = createString("patientId");

    public final StringPath serialNumber = createString("serialNumber");

    public final com.mysema.query.sql.PrimaryKey<QPatientVestDeviceHistory> primary = createPrimaryKey(patientId, serialNumber);

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> pvdPatientIdFk = createForeignKey(patientId, "id");

    public QPatientVestDeviceHistory(String variable) {
        super(QPatientVestDeviceHistory.class, forVariable(variable), "null", "PATIENT_VEST_DEVICE_HISTORY");
        addMetadata();
    }

    public QPatientVestDeviceHistory(String variable, String schema, String table) {
        super(QPatientVestDeviceHistory.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientVestDeviceHistory(Path<? extends QPatientVestDeviceHistory> path) {
        super(path.getType(), path.getMetadata(), "null", "PATIENT_VEST_DEVICE_HISTORY");
        addMetadata();
    }

    public QPatientVestDeviceHistory(PathMetadata<?> metadata) {
        super(QPatientVestDeviceHistory.class, metadata, "null", "PATIENT_VEST_DEVICE_HISTORY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(bluetoothId, ColumnMetadata.named("bluetooth_id").withIndex(3).ofType(Types.VARCHAR).withSize(255));
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(5).ofType(Types.VARCHAR).withSize(50));
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(6).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(hubId, ColumnMetadata.named("hub_id").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(isActive, ColumnMetadata.named("is_active").withIndex(9).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(7).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(8).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(patientId, ColumnMetadata.named("patient_id").withIndex(1).ofType(Types.VARCHAR).withSize(45).notNull());
        addMetadata(serialNumber, ColumnMetadata.named("serial_number").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

