package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QClinicIdSequence is a Querydsl query type for QClinicIdSequence
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QClinicIdSequence extends com.mysema.query.sql.RelationalPathBase<QClinicIdSequence> {

    private static final long serialVersionUID = -294034075;

    public static final QClinicIdSequence clinicIdSequence = new QClinicIdSequence("clinic_id_sequence");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QClinicIdSequence(String variable) {
        super(QClinicIdSequence.class, forVariable(variable), "null", "clinic_id_sequence");
        addMetadata();
    }

    public QClinicIdSequence(String variable, String schema, String table) {
        super(QClinicIdSequence.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QClinicIdSequence(Path<? extends QClinicIdSequence> path) {
        super(path.getType(), path.getMetadata(), "null", "clinic_id_sequence");
        addMetadata();
    }

    public QClinicIdSequence(PathMetadata<?> metadata) {
        super(QClinicIdSequence.class, metadata, "null", "clinic_id_sequence");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19));
    }

}

