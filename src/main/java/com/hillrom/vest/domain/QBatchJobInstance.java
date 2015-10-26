package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBatchJobInstance is a Querydsl query type for QBatchJobInstance
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBatchJobInstance extends com.mysema.query.sql.RelationalPathBase<QBatchJobInstance> {

    private static final long serialVersionUID = -27441095;

    public static final QBatchJobInstance batchJobInstance = new QBatchJobInstance("BATCH_JOB_INSTANCE");

    public final NumberPath<Long> jobInstanceId = createNumber("jobInstanceId", Long.class);

    public final StringPath jobKey = createString("jobKey");

    public final StringPath jobName = createString("jobName");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QBatchJobInstance> primary = createPrimaryKey(jobInstanceId);

    public final com.mysema.query.sql.ForeignKey<QBatchJobExecution> _jobInstExecFk = createInvForeignKey(jobInstanceId, "JOB_INSTANCE_ID");

    public QBatchJobInstance(String variable) {
        super(QBatchJobInstance.class, forVariable(variable), "null", "BATCH_JOB_INSTANCE");
        addMetadata();
    }

    public QBatchJobInstance(String variable, String schema, String table) {
        super(QBatchJobInstance.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBatchJobInstance(Path<? extends QBatchJobInstance> path) {
        super(path.getType(), path.getMetadata(), "null", "BATCH_JOB_INSTANCE");
        addMetadata();
    }

    public QBatchJobInstance(PathMetadata<?> metadata) {
        super(QBatchJobInstance.class, metadata, "null", "BATCH_JOB_INSTANCE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(jobInstanceId, ColumnMetadata.named("JOB_INSTANCE_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(jobKey, ColumnMetadata.named("JOB_KEY").withIndex(4).ofType(Types.VARCHAR).withSize(32).notNull());
        addMetadata(jobName, ColumnMetadata.named("JOB_NAME").withIndex(3).ofType(Types.VARCHAR).withSize(100).notNull());
        addMetadata(version, ColumnMetadata.named("VERSION").withIndex(2).ofType(Types.BIGINT).withSize(19));
    }

}

