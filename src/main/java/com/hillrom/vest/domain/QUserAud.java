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
 * QUserAud is a Querydsl query type for QUserAud
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUserAud extends com.mysema.query.sql.RelationalPathBase<QUserAud> {

    private static final long serialVersionUID = -564492956;

    public static final QUserAud userAud = new QUserAud("USER_AUD");

    public final BooleanPath activated = createBoolean("activated");

    public final StringPath activationKey = createString("activationKey");

    public final DateTimePath<java.sql.Timestamp> activationLinkSentDate = createDateTime("activationLinkSentDate", java.sql.Timestamp.class);

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.sql.Timestamp> createdDate = createDateTime("createdDate", java.sql.Timestamp.class);

    public final DatePath<java.sql.Date> dob = createDate("dob", java.sql.Date.class);

    public final StringPath email = createString("email");

    public final StringPath firstName = createString("firstName");

    public final StringPath gender = createString("gender");

    public final StringPath hillromId = createString("hillromId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath langKey = createString("langKey");

    public final DateTimePath<java.sql.Timestamp> lastLoggedinAt = createDateTime("lastLoggedinAt", java.sql.Timestamp.class);

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<java.sql.Timestamp> lastModifiedDate = createDateTime("lastModifiedDate", java.sql.Timestamp.class);

    public final StringPath lastName = createString("lastName");

    public final StringPath middleName = createString("middleName");

    public final BooleanPath missedTherapyNotification = createBoolean("missedTherapyNotification");

    public final BooleanPath nonHmrNotification = createBoolean("nonHmrNotification");

    public final StringPath password = createString("password");

    public final DateTimePath<java.sql.Timestamp> resetDate = createDateTime("resetDate", java.sql.Timestamp.class);

    public final StringPath resetKey = createString("resetKey");

    public final NumberPath<Integer> rev = createNumber("rev", Integer.class);

    public final NumberPath<Byte> revtype = createNumber("revtype", Byte.class);

    public final BooleanPath settingDeviationNotification = createBoolean("settingDeviationNotification");

    public final BooleanPath termsConditionAccepted = createBoolean("termsConditionAccepted");

    public final DateTimePath<java.sql.Timestamp> termsConditionAcceptedDate = createDateTime("termsConditionAcceptedDate", java.sql.Timestamp.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> zipcode = createNumber("zipcode", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QUserAud> primary = createPrimaryKey(rev, id);

    public final com.mysema.query.sql.ForeignKey<QAuditRevisionInfo> jboq3w0aies9n06aqmd81c8pbFK = createForeignKey(rev, "id");

    public final com.mysema.query.sql.ForeignKey<QUserExtensionAud> _drgtw20862kydh7wy8jyaw220FK = createInvForeignKey(Arrays.asList(id, rev), Arrays.asList("USER_ID", "REV"));

    public QUserAud(String variable) {
        super(QUserAud.class, forVariable(variable), "null", "USER_AUD");
        addMetadata();
    }

    public QUserAud(String variable, String schema, String table) {
        super(QUserAud.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserAud(Path<? extends QUserAud> path) {
        super(path.getType(), path.getMetadata(), "null", "USER_AUD");
        addMetadata();
    }

    public QUserAud(PathMetadata<?> metadata) {
        super(QUserAud.class, metadata, "null", "USER_AUD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(activated, ColumnMetadata.named("activated").withIndex(8).ofType(Types.BIT).withSize(1));
        addMetadata(activationKey, ColumnMetadata.named("activation_key").withIndex(9).ofType(Types.VARCHAR).withSize(20));
        addMetadata(activationLinkSentDate, ColumnMetadata.named("activation_link_sent_date").withIndex(10).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(4).ofType(Types.VARCHAR).withSize(50));
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(dob, ColumnMetadata.named("dob").withIndex(12).ofType(Types.DATE).withSize(10));
        addMetadata(email, ColumnMetadata.named("email").withIndex(13).ofType(Types.VARCHAR).withSize(100));
        addMetadata(firstName, ColumnMetadata.named("first_name").withIndex(14).ofType(Types.VARCHAR).withSize(50));
        addMetadata(gender, ColumnMetadata.named("gender").withIndex(15).ofType(Types.VARCHAR).withSize(10));
        addMetadata(hillromId, ColumnMetadata.named("hillrom_id").withIndex(16).ofType(Types.VARCHAR).withSize(255));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(isDeleted, ColumnMetadata.named("is_deleted").withIndex(11).ofType(Types.BIT).withSize(1));
        addMetadata(langKey, ColumnMetadata.named("lang_key").withIndex(17).ofType(Types.VARCHAR).withSize(5));
        addMetadata(lastLoggedinAt, ColumnMetadata.named("last_loggedin_at").withIndex(18).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(6).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(7).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastName, ColumnMetadata.named("last_name").withIndex(19).ofType(Types.VARCHAR).withSize(50));
        addMetadata(middleName, ColumnMetadata.named("middle_name").withIndex(20).ofType(Types.VARCHAR).withSize(50));
        addMetadata(missedTherapyNotification, ColumnMetadata.named("missed_therapy_notification").withIndex(21).ofType(Types.BIT).withSize(1));
        addMetadata(nonHmrNotification, ColumnMetadata.named("non_hmr_notification").withIndex(22).ofType(Types.BIT).withSize(1));
        addMetadata(password, ColumnMetadata.named("password").withIndex(23).ofType(Types.VARCHAR).withSize(60));
        addMetadata(resetDate, ColumnMetadata.named("reset_date").withIndex(24).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(resetKey, ColumnMetadata.named("reset_key").withIndex(25).ofType(Types.VARCHAR).withSize(20));
        addMetadata(rev, ColumnMetadata.named("REV").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(revtype, ColumnMetadata.named("REVTYPE").withIndex(3).ofType(Types.TINYINT).withSize(3));
        addMetadata(settingDeviationNotification, ColumnMetadata.named("setting_deviation_notification").withIndex(26).ofType(Types.BIT).withSize(1));
        addMetadata(termsConditionAccepted, ColumnMetadata.named("terms_condition_accepted").withIndex(27).ofType(Types.BIT).withSize(1));
        addMetadata(termsConditionAcceptedDate, ColumnMetadata.named("terms_condition_accepted_date").withIndex(28).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(title, ColumnMetadata.named("title").withIndex(29).ofType(Types.VARCHAR).withSize(50));
        addMetadata(zipcode, ColumnMetadata.named("zipcode").withIndex(30).ofType(Types.INTEGER).withSize(10));
    }

}

