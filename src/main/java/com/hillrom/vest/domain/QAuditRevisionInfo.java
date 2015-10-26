package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QAuditRevisionInfo is a Querydsl query type for QAuditRevisionInfo
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QAuditRevisionInfo extends com.mysema.query.sql.RelationalPathBase<QAuditRevisionInfo> {

    private static final long serialVersionUID = -250192285;

    public static final QAuditRevisionInfo auditRevisionInfo = new QAuditRevisionInfo("AUDIT_REVISION_INFO");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Long> timestamp = createNumber("timestamp", Long.class);

    public final StringPath userId = createString("userId");

    public final com.mysema.query.sql.PrimaryKey<QAuditRevisionInfo> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QUserAuthorityAud> _ghsw331opr6gcwma6849v76u6FK = createInvForeignKey(id, "REV");

    public final com.mysema.query.sql.ForeignKey<QClinicUserAssocAud> __82impe23ub6gsmwto7qnsw14tFK = createInvForeignKey(id, "REV");

    public final com.mysema.query.sql.ForeignKey<QAuthorityAud> _udgslkqww23nd9jj4fvghmrrFK = createInvForeignKey(id, "REV");

    public final com.mysema.query.sql.ForeignKey<QClinicAud> __6uqloh2984lk1e2q7gtvdpnugFK = createInvForeignKey(id, "REV");

    public final com.mysema.query.sql.ForeignKey<QUserAud> _jboq3w0aies9n06aqmd81c8pbFK = createInvForeignKey(id, "REV");

    public final com.mysema.query.sql.ForeignKey<QUserPatientAssocAud> _n8wsefgvxj90hk4s7ig5nimlvFK = createInvForeignKey(id, "REV");

    public final com.mysema.query.sql.ForeignKey<QClinicPatientAssocAud> _qbyvmgplui716dy405bbswxgcFK = createInvForeignKey(id, "REV");

    public final com.mysema.query.sql.ForeignKey<QPatientInfoAud> _k7rbakwkymf7gavfqlaoar9u4FK = createInvForeignKey(id, "REV");

    public QAuditRevisionInfo(String variable) {
        super(QAuditRevisionInfo.class, forVariable(variable), "null", "AUDIT_REVISION_INFO");
        addMetadata();
    }

    public QAuditRevisionInfo(String variable, String schema, String table) {
        super(QAuditRevisionInfo.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QAuditRevisionInfo(Path<? extends QAuditRevisionInfo> path) {
        super(path.getType(), path.getMetadata(), "null", "AUDIT_REVISION_INFO");
        addMetadata();
    }

    public QAuditRevisionInfo(PathMetadata<?> metadata) {
        super(QAuditRevisionInfo.class, metadata, "null", "AUDIT_REVISION_INFO");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(timestamp, ColumnMetadata.named("timestamp").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    }

}

