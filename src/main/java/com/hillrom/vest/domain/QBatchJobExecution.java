package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBatchJobExecution is a Querydsl query type for QBatchJobExecution
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBatchJobExecution extends com.mysema.query.sql.RelationalPathBase<QBatchJobExecution> {

    private static final long serialVersionUID = -1970746380;

    public static final QBatchJobExecution batchJobExecution = new QBatchJobExecution("BATCH_JOB_EXECUTION");

    public final DateTimePath<java.sql.Timestamp> createTime = createDateTime("createTime", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> endTime = createDateTime("endTime", java.sql.Timestamp.class);

    public final StringPath exitCode = createString("exitCode");

    public final StringPath exitMessage = createString("exitMessage");

    public final NumberPath<Long> jobExecutionId = createNumber("jobExecutionId", Long.class);

    public final NumberPath<Long> jobInstanceId = createNumber("jobInstanceId", Long.class);

    public final DateTimePath<java.sql.Timestamp> lastUpdated = createDateTime("lastUpdated", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> startTime = createDateTime("startTime", java.sql.Timestamp.class);

    public final StringPath status = createString("status");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QBatchJobExecution> primary = createPrimaryKey(jobExecutionId);

    public final com.mysema.query.sql.ForeignKey<QBatchJobInstance> jobInstExecFk = createForeignKey(jobInstanceId, "JOB_INSTANCE_ID");

    public final com.mysema.query.sql.ForeignKey<QBatchStepExecution> _jobExecStepFk = createInvForeignKey(jobExecutionId, "JOB_EXECUTION_ID");

    public final com.mysema.query.sql.ForeignKey<QBatchJobExecutionParams> _jobExecParamsFk = createInvForeignKey(jobExecutionId, "JOB_EXECUTION_ID");

    public final com.mysema.query.sql.ForeignKey<QBatchJobExecutionContext> _jobExecCtxFk = createInvForeignKey(jobExecutionId, "JOB_EXECUTION_ID");

    public QBatchJobExecution(String variable) {
        super(QBatchJobExecution.class, forVariable(variable), "null", "BATCH_JOB_EXECUTION");
        addMetadata();
    }

    public QBatchJobExecution(String variable, String schema, String table) {
        super(QBatchJobExecution.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBatchJobExecution(Path<? extends QBatchJobExecution> path) {
        super(path.getType(), path.getMetadata(), "null", "BATCH_JOB_EXECUTION");
        addMetadata();
    }

    public QBatchJobExecution(PathMetadata<?> metadata) {
        super(QBatchJobExecution.class, metadata, "null", "BATCH_JOB_EXECUTION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createTime, ColumnMetadata.named("CREATE_TIME").withIndex(4).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(endTime, ColumnMetadata.named("END_TIME").withIndex(6).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(exitCode, ColumnMetadata.named("EXIT_CODE").withIndex(8).ofType(Types.VARCHAR).withSize(100));
        addMetadata(exitMessage, ColumnMetadata.named("EXIT_MESSAGE").withIndex(9).ofType(Types.VARCHAR).withSize(2500));
        addMetadata(jobExecutionId, ColumnMetadata.named("JOB_EXECUTION_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(jobInstanceId, ColumnMetadata.named("JOB_INSTANCE_ID").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(lastUpdated, ColumnMetadata.named("LAST_UPDATED").withIndex(10).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(startTime, ColumnMetadata.named("START_TIME").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(status, ColumnMetadata.named("STATUS").withIndex(7).ofType(Types.VARCHAR).withSize(10));
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(2).ofType(Types.BIGINT).withSize(19));
    }

}

