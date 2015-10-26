package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QVestDeviceBadData is a Querydsl query type for QVestDeviceBadData
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QVestDeviceBadData extends com.mysema.query.sql.RelationalPathBase<QVestDeviceBadData> {

    private static final long serialVersionUID = 1944877672;

    public static final QVestDeviceBadData vestDeviceBadData = new QVestDeviceBadData("VEST_DEVICE_BAD_DATA");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.sql.Timestamp> receivedAt = createDateTime("receivedAt", java.sql.Timestamp.class);

    public final StringPath requestData = createString("requestData");

    public final com.mysema.query.sql.PrimaryKey<QVestDeviceBadData> primary = createPrimaryKey(id);

    public QVestDeviceBadData(String variable) {
        super(QVestDeviceBadData.class, forVariable(variable), "null", "VEST_DEVICE_BAD_DATA");
        addMetadata();
    }

    public QVestDeviceBadData(String variable, String schema, String table) {
        super(QVestDeviceBadData.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QVestDeviceBadData(Path<? extends QVestDeviceBadData> path) {
        super(path.getType(), path.getMetadata(), "null", "VEST_DEVICE_BAD_DATA");
        addMetadata();
    }

    public QVestDeviceBadData(PathMetadata<?> metadata) {
        super(QVestDeviceBadData.class, metadata, "null", "VEST_DEVICE_BAD_DATA");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(receivedAt, ColumnMetadata.named("received_at").withIndex(2).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(requestData, ColumnMetadata.named("request_data").withIndex(3).ofType(Types.LONGVARCHAR).withSize(16777215));
    }

}

