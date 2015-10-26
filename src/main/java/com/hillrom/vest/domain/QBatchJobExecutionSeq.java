package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBatchJobExecutionSeq is a Querydsl query type for QBatchJobExecutionSeq
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBatchJobExecutionSeq extends com.mysema.query.sql.RelationalPathBase<QBatchJobExecutionSeq> {

    private static final long serialVersionUID = 1697612747;

    public static final QBatchJobExecutionSeq batchJobExecutionSeq = new QBatchJobExecutionSeq("BATCH_JOB_EXECUTION_SEQ");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBatchJobExecutionSeq(String variable) {
        super(QBatchJobExecutionSeq.class, forVariable(variable), "null", "BATCH_JOB_EXECUTION_SEQ");
        addMetadata();
    }

    public QBatchJobExecutionSeq(String variable, String schema, String table) {
        super(QBatchJobExecutionSeq.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBatchJobExecutionSeq(Path<? extends QBatchJobExecutionSeq> path) {
        super(path.getType(), path.getMetadata(), "null", "BATCH_JOB_EXECUTION_SEQ");
        addMetadata();
    }

    public QBatchJobExecutionSeq(PathMetadata<?> metadata) {
        super(QBatchJobExecutionSeq.class, metadata, "null", "BATCH_JOB_EXECUTION_SEQ");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

