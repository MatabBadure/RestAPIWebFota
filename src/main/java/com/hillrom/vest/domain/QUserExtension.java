package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUserExtension is a Querydsl query type for QUserExtension
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUserExtension extends com.mysema.query.sql.RelationalPathBase<QUserExtension> {

    private static final long serialVersionUID = 55070707;

    public static final QUserExtension userExtension = new QUserExtension("USER_EXTENSION");

    public final StringPath address = createString("address");

    public final StringPath city = createString("city");

    public final StringPath credentials = createString("credentials");

    public final StringPath faxNumber = createString("faxNumber");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath mobilePhone = createString("mobilePhone");

    public final StringPath npiNumber = createString("npiNumber");

    public final StringPath primaryPhone = createString("primaryPhone");

    public final StringPath speciality = createString("speciality");

    public final StringPath state = createString("state");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QUserExtension> primary = createPrimaryKey(userId);

    public final com.mysema.query.sql.ForeignKey<QUser> userExtensionIdFk = createForeignKey(userId, "id");

    public final com.mysema.query.sql.ForeignKey<QClinicUserAssoc> __46roc6696st3dqo8e84dux3u3FK = createInvForeignKey(userId, "users_id");

    public QUserExtension(String variable) {
        super(QUserExtension.class, forVariable(variable), "null", "USER_EXTENSION");
        addMetadata();
    }

    public QUserExtension(String variable, String schema, String table) {
        super(QUserExtension.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserExtension(Path<? extends QUserExtension> path) {
        super(path.getType(), path.getMetadata(), "null", "USER_EXTENSION");
        addMetadata();
    }

    public QUserExtension(PathMetadata<?> metadata) {
        super(QUserExtension.class, metadata, "null", "USER_EXTENSION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(address, ColumnMetadata.named("address").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(city, ColumnMetadata.named("city").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(credentials, ColumnMetadata.named("credentials").withIndex(3).ofType(Types.VARCHAR).withSize(255));
        addMetadata(faxNumber, ColumnMetadata.named("fax_number").withIndex(6).ofType(Types.VARCHAR).withSize(20));
        addMetadata(isDeleted, ColumnMetadata.named("is_deleted").withIndex(10).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(mobilePhone, ColumnMetadata.named("mobile_phone").withIndex(5).ofType(Types.VARCHAR).withSize(20));
        addMetadata(npiNumber, ColumnMetadata.named("npi_number").withIndex(11).ofType(Types.VARCHAR).withSize(15));
        addMetadata(primaryPhone, ColumnMetadata.named("primary_phone").withIndex(4).ofType(Types.VARCHAR).withSize(20));
        addMetadata(speciality, ColumnMetadata.named("speciality").withIndex(2).ofType(Types.VARCHAR).withSize(255));
        addMetadata(state, ColumnMetadata.named("state").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

