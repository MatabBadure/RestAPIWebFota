package com.hillrom.vest.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUser is a Querydsl query type for QUser
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUser extends com.mysema.query.sql.RelationalPathBase<QUser> {

    private static final long serialVersionUID = 1592626604;

    public static final QUser user = new QUser("USER");

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

    public final BooleanPath settingDeviationNotification = createBoolean("settingDeviationNotification");

    public final BooleanPath termsConditionAccepted = createBoolean("termsConditionAccepted");

    public final DateTimePath<java.sql.Timestamp> termsConditionAcceptedDate = createDateTime("termsConditionAcceptedDate", java.sql.Timestamp.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> zipcode = createNumber("zipcode", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QUser> primary = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QPatientVestDeviceData> _pvddUserIdFk = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QUserPatientAssocAud> _n5e5hf7jqpkbu608v426l3wkkFK = createInvForeignKey(id, "USER_ID");

    public final com.mysema.query.sql.ForeignKey<QPatientCompliance> _pATIENTCOMPLIANCEUserIdFk = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QClinicUserAssoc> _cuaUserIdFk = createInvForeignKey(id, "users_id");

    public final com.mysema.query.sql.ForeignKey<QPatientVestTherapyData> _pvtdUserIdFk = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QClinic> _clinicAdminUserIdFk = createInvForeignKey(id, "clinic_admin_id");

    public final com.mysema.query.sql.ForeignKey<QNotification> _notificationUserIdFk = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QPatientNote> _patNoteUserIdFk = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QUserPatientAssoc> _upaUserIdFk = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QUserLoginToken> _userlogintokenUserIdFk = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QUserExtension> _userExtensionIdFk = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QUserSecurityQuestion> _usqUserIdFk = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QPatientNoEvent> _sd5m0ftnm3kscdq0uj6wnkndbFK = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QUserAuthority> _userIdFk = createInvForeignKey(id, "user_id");

    public final com.mysema.query.sql.ForeignKey<QPatientProtocolData> _ppdUserIdFk = createInvForeignKey(id, "user_id");

    public QUser(String variable) {
        super(QUser.class, forVariable(variable), "null", "USER");
        addMetadata();
    }

    public QUser(String variable, String schema, String table) {
        super(QUser.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUser(Path<? extends QUser> path) {
        super(path.getType(), path.getMetadata(), "null", "USER");
        addMetadata();
    }

    public QUser(PathMetadata<?> metadata) {
        super(QUser.class, metadata, "null", "USER");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(activated, ColumnMetadata.named("activated").withIndex(8).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(activationKey, ColumnMetadata.named("activation_key").withIndex(10).ofType(Types.VARCHAR).withSize(20));
        addMetadata(activationLinkSentDate, ColumnMetadata.named("activation_link_sent_date").withIndex(28).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(createdBy, ColumnMetadata.named("created_by").withIndex(12).ofType(Types.VARCHAR).withSize(50));
        addMetadata(createdDate, ColumnMetadata.named("created_date").withIndex(13).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(dob, ColumnMetadata.named("dob").withIndex(23).ofType(Types.DATE).withSize(10));
        addMetadata(email, ColumnMetadata.named("email").withIndex(2).ofType(Types.VARCHAR).withSize(100));
        addMetadata(firstName, ColumnMetadata.named("first_name").withIndex(5).ofType(Types.VARCHAR).withSize(50));
        addMetadata(gender, ColumnMetadata.named("gender").withIndex(19).ofType(Types.VARCHAR).withSize(10));
        addMetadata(hillromId, ColumnMetadata.named("hillrom_id").withIndex(24).ofType(Types.VARCHAR).withSize(255));
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(isDeleted, ColumnMetadata.named("is_deleted").withIndex(18).ofType(Types.BIT).withSize(1).notNull());
        addMetadata(langKey, ColumnMetadata.named("lang_key").withIndex(9).ofType(Types.VARCHAR).withSize(5));
        addMetadata(lastLoggedinAt, ColumnMetadata.named("last_loggedin_at").withIndex(15).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastModifiedBy, ColumnMetadata.named("last_modified_by").withIndex(16).ofType(Types.VARCHAR).withSize(50));
        addMetadata(lastModifiedDate, ColumnMetadata.named("last_modified_date").withIndex(17).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(lastName, ColumnMetadata.named("last_name").withIndex(7).ofType(Types.VARCHAR).withSize(50));
        addMetadata(middleName, ColumnMetadata.named("middle_name").withIndex(6).ofType(Types.VARCHAR).withSize(50));
        addMetadata(missedTherapyNotification, ColumnMetadata.named("missed_therapy_notification").withIndex(25).ofType(Types.BIT).withSize(1));
        addMetadata(nonHmrNotification, ColumnMetadata.named("non_hmr_notification").withIndex(26).ofType(Types.BIT).withSize(1));
        addMetadata(password, ColumnMetadata.named("PASSWORD").withIndex(3).ofType(Types.VARCHAR).withSize(60));
        addMetadata(resetDate, ColumnMetadata.named("reset_date").withIndex(14).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(resetKey, ColumnMetadata.named("reset_key").withIndex(11).ofType(Types.VARCHAR).withSize(20));
        addMetadata(settingDeviationNotification, ColumnMetadata.named("setting_deviation_notification").withIndex(27).ofType(Types.BIT).withSize(1));
        addMetadata(termsConditionAccepted, ColumnMetadata.named("terms_condition_accepted").withIndex(21).ofType(Types.BIT).withSize(1));
        addMetadata(termsConditionAcceptedDate, ColumnMetadata.named("terms_condition_accepted_date").withIndex(22).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(title, ColumnMetadata.named("title").withIndex(4).ofType(Types.VARCHAR).withSize(50));
        addMetadata(zipcode, ColumnMetadata.named("zipcode").withIndex(20).ofType(Types.INTEGER).withSize(10));
    }

}

