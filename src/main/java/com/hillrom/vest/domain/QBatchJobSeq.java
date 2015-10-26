package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBatchJobSeq is a Querydsl query type for QBatchJobSeq
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBatchJobSeq extends com.mysema.query.sql.RelationalPathBase<QBatchJobSeq> {

    private static final long serialVersionUID = -1623567813;

    public static final QBatchJobSeq batchJobSeq = new QBatchJobSeq("BATCH_JOB_SEQ");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBatchJobSeq(String variable) {
        super(QBatchJobSeq.class, forVariable(variable), "null", "BATCH_JOB_SEQ");
        addMetadata();
    }

    public QBatchJobSeq(String variable, String schema, String table) {
        super(QBatchJobSeq.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBatchJobSeq(Path<? extends QBatchJobSeq> path) {
        super(path.getType(), path.getMetadata(), "null", "BATCH_JOB_SEQ");
        addMetadata();
    }

    public QBatchJobSeq(PathMetadata<?> metadata) {
        super(QBatchJobSeq.class, metadata, "null", "BATCH_JOB_SEQ");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

