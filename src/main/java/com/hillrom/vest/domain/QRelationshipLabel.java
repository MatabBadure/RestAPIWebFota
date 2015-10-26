package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QRelationshipLabel is a Querydsl query type for QRelationshipLabel
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QRelationshipLabel extends com.mysema.query.sql.RelationalPathBase<QRelationshipLabel> {

    private static final long serialVersionUID = 648441691;

    public static final QRelationshipLabel relationshipLabel = new QRelationshipLabel("RELATIONSHIP_LABEL");

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QRelationshipLabel> primary = createPrimaryKey(name);

    public QRelationshipLabel(String variable) {
        super(QRelationshipLabel.class, forVariable(variable), "null", "RELATIONSHIP_LABEL");
        addMetadata();
    }

    public QRelationshipLabel(String variable, String schema, String table) {
        super(QRelationshipLabel.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QRelationshipLabel(Path<? extends QRelationshipLabel> path) {
        super(path.getType(), path.getMetadata(), "null", "RELATIONSHIP_LABEL");
        addMetadata();
    }

    public QRelationshipLabel(PathMetadata<?> metadata) {
        super(QRelationshipLabel.class, metadata, "null", "RELATIONSHIP_LABEL");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("name").withIndex(1).ofType(Types.VARCHAR).withSize(50).notNull());
    }

}

