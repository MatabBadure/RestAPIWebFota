package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QProtocolIdSequence is a Querydsl query type for QProtocolIdSequence
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QProtocolIdSequence extends com.mysema.query.sql.RelationalPathBase<QProtocolIdSequence> {

    private static final long serialVersionUID = 190050005;

    public static final QProtocolIdSequence protocolIdSequence = new QProtocolIdSequence("protocol_id_sequence");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QProtocolIdSequence(String variable) {
        super(QProtocolIdSequence.class, forVariable(variable), "null", "protocol_id_sequence");
        addMetadata();
    }

    public QProtocolIdSequence(String variable, String schema, String table) {
        super(QProtocolIdSequence.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QProtocolIdSequence(Path<? extends QProtocolIdSequence> path) {
        super(path.getType(), path.getMetadata(), "null", "protocol_id_sequence");
        addMetadata();
    }

    public QProtocolIdSequence(PathMetadata<?> metadata) {
        super(QProtocolIdSequence.class, metadata, "null", "protocol_id_sequence");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19));
    }

}

