package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBatchStepExecution is a Querydsl query type for QBatchStepExecution
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBatchStepExecution extends com.mysema.query.sql.RelationalPathBase<QBatchStepExecution> {

    private static final long serialVersionUID = -1914391373;

    public static final QBatchStepExecution batchStepExecution = new QBatchStepExecution("BATCH_STEP_EXECUTION");

    public final NumberPath<Long> commitCount = createNumber("commitCount", Long.class);

    public final DateTimePath<java.sql.Timestamp> endTime = createDateTime("endTime", java.sql.Timestamp.class);

    public final StringPath exitCode = createString("exitCode");

    public final StringPath exitMessage = createString("exitMessage");

    public final NumberPath<Long> filterCount = createNumber("filterCount", Long.class);

    public final NumberPath<Long> jobExecutionId = createNumber("jobExecutionId", Long.class);

    public final DateTimePath<java.sql.Timestamp> lastUpdated = createDateTime("lastUpdated", java.sql.Timestamp.class);

    public final NumberPath<Long> processSkipCount = createNumber("processSkipCount", Long.class);

    public final NumberPath<Long> readCount = createNumber("readCount", Long.class);

    public final NumberPath<Long> readSkipCount = createNumber("readSkipCount", Long.class);

    public final NumberPath<Long> rollbackCount = createNumber("rollbackCount", Long.class);

    public final DateTimePath<java.sql.Timestamp> startTime = createDateTime("startTime", java.sql.Timestamp.class);

    public final StringPath status = createString("status");

    public final NumberPath<Long> stepExecutionId = createNumber("stepExecutionId", Long.class);

    public final StringPath stepName = createString("stepName");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public final NumberPath<Long> writeCount = createNumber("writeCount", Long.class);

    public final NumberPath<Long> writeSkipCount = createNumber("writeSkipCount", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QBatchStepExecution> primary = createPrimaryKey(stepExecutionId);

    public final com.mysema.query.sql.ForeignKey<QBatchJobExecution> jobExecStepFk = createForeignKey(jobExecutionId, "JOB_EXECUTION_ID");

    public final com.mysema.query.sql.ForeignKey<QBatchStepExecutionContext> _stepExecCtxFk = createInvForeignKey(stepExecutionId, "STEP_EXECUTION_ID");

    public QBatchStepExecution(String variable) {
        super(QBatchStepExecution.class, forVariable(variable), "null", "BATCH_STEP_EXECUTION");
        addMetadata();
    }

    public QBatchStepExecution(String variable, String schema, String table) {
        super(QBatchStepExecution.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBatchStepExecution(Path<? extends QBatchStepExecution> path) {
        super(path.getType(), path.getMetadata(), "null", "BATCH_STEP_EXECUTION");
        addMetadata();
    }

    public QBatchStepExecution(PathMetadata<?> metadata) {
        super(QBatchStepExecution.class, metadata, "null", "BATCH_STEP_EXECUTION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(commitCount, ColumnMetadata.named("COMMIT_COUNT").withIndex(8).ofType(Types.BIGINT).withSize(19));
        addMetadata(endTime, ColumnMetadata.named("END_TIME").withIndex(6).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(exitCode, ColumnMetadata.named("EXIT_CODE").withIndex(16).ofType(Types.VARCHAR).withSize(100));
        addMetadata(exitMessage, ColumnMetadata.named("EXIT_MESSAGE").withIndex(17).ofType(Types.VARCHAR).withSize(2500));
        addMetadata(filterCount, ColumnMetadata.named("FILTER_COUNT").withIndex(10).ofType(Types.BIGINT).withSize(19));
        addMetadata(jobExecutionId, ColumnMetadata.named("JOB_EXECUTION_ID").withIndex(4).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(lastUpdated, ColumnMetadata.named("LAST_UPDATED").withIndex(18).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(processSkipCount, ColumnMetadata.named("PROCESS_SKIP_COUNT").withIndex(14).ofType(Types.BIGINT).withSize(19));
        addMetadata(readCount, ColumnMetadata.named("READ_COUNT").withIndex(9).ofType(Types.BIGINT).withSize(19));
        addMetadata(readSkipCount, ColumnMetadata.named("READ_SKIP_COUNT").withIndex(12).ofType(Types.BIGINT).withSize(19));
        addMetadata(rollbackCount, ColumnMetadata.named("ROLLBACK_COUNT").withIndex(15).ofType(Types.BIGINT).withSize(19));
        addMetadata(startTime, ColumnMetadata.named("START_TIME").withIndex(5).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(status, ColumnMetadata.named("STATUS").withIndex(7).ofType(Types.VARCHAR).withSize(10));
        addMetadata(stepExecutionId, ColumnMetadata.named("STEP_EXECUTION_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(stepName, ColumnMetadata.named("STEP_NAME").withIndex(3).ofType(Types.VARCHAR).withSize(100).notNull());
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(writeCount, ColumnMetadata.named("WRITE_COUNT").withIndex(11).ofType(Types.BIGINT).withSize(19));
        addMetadata(writeSkipCount, ColumnMetadata.named("WRITE_SKIP_COUNT").withIndex(13).ofType(Types.BIGINT).withSize(19));
    }

}

