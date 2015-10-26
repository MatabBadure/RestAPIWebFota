package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QProtocolConstants is a Querydsl query type for QProtocolConstants
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QProtocolConstants extends com.mysema.query.sql.RelationalPathBase<QProtocolConstants> {

    private static final long serialVersionUID = 630997334;

    public static final QProtocolConstants protocolConstants = new QProtocolConstants("PROTOCOL_CONSTANTS");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> maxFrequency = createNumber("maxFrequency", Integer.class);

    public final NumberPath<Integer> maxMinutesPerTreatment = createNumber("maxMinutesPerTreatment", Integer.class);

    public final NumberPath<Integer> maxPressure = createNumber("maxPressure", Integer.class);

    public final NumberPath<Integer> minFrequency = createNumber("minFrequency", Integer.class);

    public final NumberPath<Integer> minMinutesPerTreatment = createNumber("minMinutesPerTreatment", Integer.class);

    public final NumberPath<Integer> minPressure = createNumber("minPressure", Integer.class);

    public final NumberPath<Integer> treatmentsPerDay = createNumber("treatmentsPerDay", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QProtocolConstants> primary = createPrimaryKey(id);

    public QProtocolConstants(String variable) {
        super(QProtocolConstants.class, forVariable(variable), "null", "PROTOCOL_CONSTANTS");
        addMetadata();
    }

    public QProtocolConstants(String variable, String schema, String table) {
        super(QProtocolConstants.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QProtocolConstants(Path<? extends QProtocolConstants> path) {
        super(path.getType(), path.getMetadata(), "null", "PROTOCOL_CONSTANTS");
        addMetadata();
    }

    public QProtocolConstants(PathMetadata<?> metadata) {
        super(QProtocolConstants.class, metadata, "null", "PROTOCOL_CONSTANTS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(maxFrequency, ColumnMetadata.named("max_frequency").withIndex(6).ofType(Types.INTEGER).withSize(10));
        addMetadata(maxMinutesPerTreatment, ColumnMetadata.named("max_minutes_per_treatment").withIndex(4).ofType(Types.INTEGER).withSize(10));
        addMetadata(maxPressure, ColumnMetadata.named("max_pressure").withIndex(8).ofType(Types.INTEGER).withSize(10));
        addMetadata(minFrequency, ColumnMetadata.named("min_frequency").withIndex(5).ofType(Types.INTEGER).withSize(10));
        addMetadata(minMinutesPerTreatment, ColumnMetadata.named("min_minutes_per_treatment").withIndex(3).ofType(Types.INTEGER).withSize(10));
        addMetadata(minPressure, ColumnMetadata.named("min_pressure").withIndex(7).ofType(Types.INTEGER).withSize(10));
        addMetadata(treatmentsPerDay, ColumnMetadata.named("treatments_per_day").withIndex(2).ofType(Types.INTEGER).withSize(10));
    }

}

