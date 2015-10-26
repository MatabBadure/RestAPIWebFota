package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBatchStepExecutionContext is a Querydsl query type for QBatchStepExecutionContext
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBatchStepExecutionContext extends com.mysema.query.sql.RelationalPathBase<QBatchStepExecutionContext> {

    private static final long serialVersionUID = 1535834492;

    public static final QBatchStepExecutionContext batchStepExecutionContext = new QBatchStepExecutionContext("BATCH_STEP_EXECUTION_CONTEXT");

    public final StringPath serializedContext = createString("serializedContext");

    public final StringPath shortContext = createString("shortContext");

    public final NumberPath<Long> stepExecutionId = createNumber("stepExecutionId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QBatchStepExecutionContext> primary = createPrimaryKey(stepExecutionId);

    public final com.mysema.query.sql.ForeignKey<QBatchStepExecution> stepExecCtxFk = createForeignKey(stepExecutionId, "STEP_EXECUTION_ID");

    public QBatchStepExecutionContext(String variable) {
        super(QBatchStepExecutionContext.class, forVariable(variable), "null", "BATCH_STEP_EXECUTION_CONTEXT");
        addMetadata();
    }

    public QBatchStepExecutionContext(String variable, String schema, String table) {
        super(QBatchStepExecutionContext.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBatchStepExecutionContext(Path<? extends QBatchStepExecutionContext> path) {
        super(path.getType(), path.getMetadata(), "null", "BATCH_STEP_EXECUTION_CONTEXT");
        addMetadata();
    }

    public QBatchStepExecutionContext(PathMetadata<?> metadata) {
        super(QBatchStepExecutionContext.class, metadata, "null", "BATCH_STEP_EXECUTION_CONTEXT");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(serializedContext, ColumnMetadata.named("SERIALIZED_CONTEXT").withIndex(3).ofType(Types.LONGVARCHAR).withSize(65535));
        addMetadata(shortContext, ColumnMetadata.named("SHORT_CONTEXT").withIndex(2).ofType(Types.VARCHAR).withSize(2500).notNull());
        addMetadata(stepExecutionId, ColumnMetadata.named("STEP_EXECUTION_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

