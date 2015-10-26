package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientNote is a Querydsl query type for QPatientNote
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientNote extends com.mysema.query.sql.RelationalPathBase<QPatientNote> {

    private static final long serialVersionUID = -771968106;

    public static final QPatientNote patientNote = new QPatientNote("PATIENT_NOTE");

    public final DatePath<java.sql.Date> createdOn = createDate("createdOn", java.sql.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final DateTimePath<java.sql.Timestamp> modifiedAt = createDateTime("modifiedAt", java.sql.Timestamp.class);

    public final StringPath note = createString("note");

    public final StringPath patientId = createString("patientId");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QPatientNote> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QUser> patNoteUserIdFk = createForeignKey(userId, "id");

    public final com.mysema.query.sql.ForeignKey<QPatientInfo> patNotePatientIdFk = createForeignKey(patientId, "id");

    public QPatientNote(String variable) {
        super(QPatientNote.class, forVariable(variable), "null", "PATIENT_NOTE");
        addMetadata();
    }

    public QPatientNote(String variable, String schema, String table) {
        super(QPatientNote.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientNote(Path<? extends QPatientNote> path) {
        super(path.getType(), path.getMetadata(), "null", "PATIENT_NOTE");
        addMetadata();
    }

    public QPatientNote(PathMetadata<?> metadata) {
        super(QPatientNote.class, metadata, "null", "PATIENT_NOTE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createdOn, ColumnMetadata.named("created_on").withIndex(2).ofType(Types.DATE).withSize(10).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(isDeleted, ColumnMetadata.named("is_deleted").withIndex(7).ofType(Types.BIT).withSize(1));
        addMetadata(modifiedAt, ColumnMetadata.named("modified_at").withIndex(6).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(note, ColumnMetadata.named("note").withIndex(5).ofType(Types.LONGVARCHAR).withSize(16777215).notNull());
        addMetadata(patientId, ColumnMetadata.named("patient_id").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

