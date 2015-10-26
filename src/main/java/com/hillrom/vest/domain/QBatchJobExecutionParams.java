package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBatchJobExecutionParams is a Querydsl query type for QBatchJobExecutionParams
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBatchJobExecutionParams extends com.mysema.query.sql.RelationalPathBase<QBatchJobExecutionParams> {

    private static final long serialVersionUID = 251980442;

    public static final QBatchJobExecutionParams batchJobExecutionParams = new QBatchJobExecutionParams("BATCH_JOB_EXECUTION_PARAMS");

    public final DateTimePath<java.sql.Timestamp> dateVal = createDateTime("dateVal", java.sql.Timestamp.class);

    public final NumberPath<Double> doubleVal = createNumber("doubleVal", Double.class);

    public final StringPath identifying = createString("identifying");

    public final NumberPath<Long> jobExecutionId = createNumber("jobExecutionId", Long.class);

    public final StringPath keyName = createString("keyName");

    public final NumberPath<Long> longVal = createNumber("longVal", Long.class);

    public final StringPath stringVal = createString("stringVal");

    public final StringPath typeCd = createString("typeCd");

    public final com.mysema.query.sql.ForeignKey<QBatchJobExecution> jobExecParamsFk = createForeignKey(jobExecutionId, "JOB_EXECUTION_ID");

    public QBatchJobExecutionParams(String variable) {
        super(QBatchJobExecutionParams.class, forVariable(variable), "null", "BATCH_JOB_EXECUTION_PARAMS");
        addMetadata();
    }

    public QBatchJobExecutionParams(String variable, String schema, String table) {
        super(QBatchJobExecutionParams.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBatchJobExecutionParams(Path<? extends QBatchJobExecutionParams> path) {
        super(path.getType(), path.getMetadata(), "null", "BATCH_JOB_EXECUTION_PARAMS");
        addMetadata();
    }

    public QBatchJobExecutionParams(PathMetadata<?> metadata) {
        super(QBatchJobExecutionParams.class, metadata, "null", "BATCH_JOB_EXECUTION_PARAMS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(dateVal, ColumnMetadata.named("DATE_VAL").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(doubleVal, ColumnMetadata.named("DOUBLE_VAL").withIndex(7).ofType(Types.DOUBLE).withSize(22));
        addMetadata(identifying, ColumnMetadata.named("IDENTIFYING").withIndex(8).ofType(Types.CHAR).withSize(1).notNull());
        addMetadata(jobExecutionId, ColumnMetadata.named("JOB_EXECUTION_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(keyName, ColumnMetadata.named("KEY_NAME").withIndex(3).ofType(Types.VARCHAR).withSize(100).notNull());
        addMetadata(longVal, ColumnMetadata.named("LONG_VAL").withIndex(6).ofType(Types.BIGINT).withSize(19));
        addMetadata(stringVal, ColumnMetadata.named("STRING_VAL").withIndex(4).ofType(Types.VARCHAR).withSize(250));
        addMetadata(typeCd, ColumnMetadata.named("TYPE_CD").withIndex(2).ofType(Types.VARCHAR).withSize(6).notNull());
    }

}

