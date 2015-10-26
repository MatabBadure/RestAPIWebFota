package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import java.util.*;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserExtensionAud is a Querydsl query type for QUserExtensionAud
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUserExtensionAud extends com.mysema.query.sql.RelationalPathBase<QUserExtensionAud> {

    private static final long serialVersionUID = -66008643;

    public static final QUserExtensionAud userExtensionAud = new QUserExtensionAud("USER_EXTENSION_AUD");

    public final StringPath address = createString("address");

    public final StringPath city = createString("city");

    public final StringPath credentials = createString("credentials");

    public final StringPath faxNumber = createString("faxNumber");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath mobilePhone = createString("mobilePhone");

    public final StringPath npiNumber = createString("npiNumber");

    public final StringPath primaryPhone = createString("primaryPhone");

    public final NumberPath<Integer> rev = createNumber("rev", Integer.class);

    public final StringPath speciality = createString("speciality");

    public final StringPath state = createString("state");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QUserExtensionAud> primary = createPrimaryKey(rev, userId);

    public final com.mysema.query.sql.ForeignKey<QUserAud> drgtw20862kydh7wy8jyaw220FK = createForeignKey(Arrays.asList(userId, rev), Arrays.asList("id", "REV"));

    public QUserExtensionAud(String variable) {
        super(QUserExtensionAud.class, forVariable(variable), "null", "USER_EXTENSION_AUD");
        addMetadata();
    }

    public QUserExtensionAud(String variable, String schema, String table) {
        super(QUserExtensionAud.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserExtensionAud(Path<? extends QUserExtensionAud> path) {
        super(path.getType(), path.getMetadata(), "null", "USER_EXTENSION_AUD");
        addMetadata();
    }

    public QUserExtensionAud(PathMetadata<?> metadata) {
        super(QUserExtensionAud.class, metadata, "null", "USER_EXTENSION_AUD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(address, ColumnMetadata.named("address").withIndex(3).ofType(Types.VARCHAR).withSize(255));
        addMetadata(city, ColumnMetadata.named("city").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(credentials, ColumnMetadata.named("credentials").withIndex(5).ofType(Types.VARCHAR).withSize(255));
        addMetadata(faxNumber, ColumnMetadata.named("fax_number").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(isDeleted, ColumnMetadata.named("is_deleted").withIndex(6).ofType(Types.BIT).withSize(1));
        addMetadata(mobilePhone, ColumnMetadata.named("mobile_phone").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(npiNumber, ColumnMetadata.named("npi_number").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(primaryPhone, ColumnMetadata.named("primary_phone").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(rev, ColumnMetadata.named("REV").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(speciality, ColumnMetadata.named("speciality").withIndex(11).ofType(Types.VARCHAR).withSize(255));
        addMetadata(state, ColumnMetadata.named("state").withIndex(12).ofType(Types.VARCHAR).withSize(255));
        addMetadata(userId, ColumnMetadata.named("USER_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

