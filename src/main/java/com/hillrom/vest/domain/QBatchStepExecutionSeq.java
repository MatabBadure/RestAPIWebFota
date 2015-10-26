package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBatchStepExecutionSeq is a Querydsl query type for QBatchStepExecutionSeq
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBatchStepExecutionSeq extends com.mysema.query.sql.RelationalPathBase<QBatchStepExecutionSeq> {

    private static final long serialVersionUID = 1237413548;

    public static final QBatchStepExecutionSeq batchStepExecutionSeq = new QBatchStepExecutionSeq("BATCH_STEP_EXECUTION_SEQ");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBatchStepExecutionSeq(String variable) {
        super(QBatchStepExecutionSeq.class, forVariable(variable), "null", "BATCH_STEP_EXECUTION_SEQ");
        addMetadata();
    }

    public QBatchStepExecutionSeq(String variable, String schema, String table) {
        super(QBatchStepExecutionSeq.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBatchStepExecutionSeq(Path<? extends QBatchStepExecutionSeq> path) {
        super(path.getType(), path.getMetadata(), "null", "BATCH_STEP_EXECUTION_SEQ");
        addMetadata();
    }

    public QBatchStepExecutionSeq(PathMetadata<?> metadata) {
        super(QBatchStepExecutionSeq.class, metadata, "null", "BATCH_STEP_EXECUTION_SEQ");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

