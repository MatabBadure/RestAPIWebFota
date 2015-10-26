package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBatchJobExecutionContext is a Querydsl query type for QBatchJobExecutionContext
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBatchJobExecutionContext extends com.mysema.query.sql.RelationalPathBase<QBatchJobExecutionContext> {

    private static final long serialVersionUID = 966485787;

    public static final QBatchJobExecutionContext batchJobExecutionContext = new QBatchJobExecutionContext("BATCH_JOB_EXECUTION_CONTEXT");

    public final NumberPath<Long> jobExecutionId = createNumber("jobExecutionId", Long.class);

    public final StringPath serializedContext = createString("serializedContext");

    public final StringPath shortContext = createString("shortContext");

    public final com.mysema.query.sql.PrimaryKey<QBatchJobExecutionContext> primary = createPrimaryKey(jobExecutionId);

    public final com.mysema.query.sql.ForeignKey<QBatchJobExecution> jobExecCtxFk = createForeignKey(jobExecutionId, "JOB_EXECUTION_ID");

    public QBatchJobExecutionContext(String variable) {
        super(QBatchJobExecutionContext.class, forVariable(variable), "null", "BATCH_JOB_EXECUTION_CONTEXT");
        addMetadata();
    }

    public QBatchJobExecutionContext(String variable, String schema, String table) {
        super(QBatchJobExecutionContext.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBatchJobExecutionContext(Path<? extends QBatchJobExecutionContext> path) {
        super(path.getType(), path.getMetadata(), "null", "BATCH_JOB_EXECUTION_CONTEXT");
        addMetadata();
    }

    public QBatchJobExecutionContext(PathMetadata<?> metadata) {
        super(QBatchJobExecutionContext.class, metadata, "null", "BATCH_JOB_EXECUTION_CONTEXT");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(jobExecutionId, ColumnMetadata.named("JOB_EXECUTION_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(serializedContext, ColumnMetadata.named("SERIALIZED_CONTEXT").withIndex(3).ofType(Types.LONGVARCHAR).withSize(65535));
        addMetadata(shortContext, ColumnMetadata.named("SHORT_CONTEXT").withIndex(2).ofType(Types.VARCHAR).withSize(2500).notNull());
    }

}

