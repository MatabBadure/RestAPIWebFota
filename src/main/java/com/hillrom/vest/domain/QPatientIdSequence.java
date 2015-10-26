package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPatientIdSequence is a Querydsl query type for QPatientIdSequence
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPatientIdSequence extends com.mysema.query.sql.RelationalPathBase<QPatientIdSequence> {

    private static final long serialVersionUID = -1637984608;

    public static final QPatientIdSequence patientIdSequence = new QPatientIdSequence("patient_id_sequence");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QPatientIdSequence(String variable) {
        super(QPatientIdSequence.class, forVariable(variable), "null", "patient_id_sequence");
        addMetadata();
    }

    public QPatientIdSequence(String variable, String schema, String table) {
        super(QPatientIdSequence.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPatientIdSequence(Path<? extends QPatientIdSequence> path) {
        super(path.getType(), path.getMetadata(), "null", "patient_id_sequence");
        addMetadata();
    }

    public QPatientIdSequence(PathMetadata<?> metadata) {
        super(QPatientIdSequence.class, metadata, "null", "patient_id_sequence");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19));
    }

}

