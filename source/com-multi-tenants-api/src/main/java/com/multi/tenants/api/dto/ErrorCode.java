package com.multi.tenants.api.dto;

public class ErrorCode {
    /**
     * General error code
     * */
    public static final String GENERAL_ERROR_REQUIRE_PARAMS = "ERROR-GENERAL-0000";
    public static final String GENERAL_ERROR_STORE_LOCKED = "ERROR-GENERAL-0001";
    public static final String GENERAL_ERROR_ACCOUNT_LOCKED = "ERROR-GENERAL-0002";
    public static final String GENERAL_ERROR_SHOP_LOCKED = "ERROR-GENERAL-0003";
    public static final String GENERAL_ERROR_STORE_NOT_FOUND = "ERROR-GENERAL-0004";
    public static final String GENERAL_ERROR_ACCOUNT_NOT_FOUND = "ERROR-GENERAL-0005";

    /**
     * Starting error code ACCOUNT
     * */
    public static final String ACCOUNT_ERROR_UNKNOWN = "ERROR-ACCOUNT-0000";
    public static final String ACCOUNT_ERROR_USERNAME_EXIST = "ERROR-ACCOUNT-0001";
    public static final String ACCOUNT_ERROR_NOT_FOUND = "ERROR-ACCOUNT-0002";
    public static final String ACCOUNT_ERROR_WRONG_PASSWORD = "ERROR-ACCOUNT-0003";
    public static final String ACCOUNT_ERROR_WRONG_HASH_RESET_PASS = "ERROR-ACCOUNT-0004";
    public static final String ACCOUNT_ERROR_LOCKED = "ERROR-ACCOUNT-0005";
    public static final String ACCOUNT_ERROR_OPT_INVALID = "ERROR-ACCOUNT-0006";
    public static final String ACCOUNT_ERROR_NOT_ALLOW_DELETE_SUPPER_ADMIN = "ERROR-ACCOUNT-007";

    /**
     * Starting error code CATEGORY
     * */
    public static final String CATEGORY_ERROR_NOT_FOUND = "ERROR-CATEGORY-0000";
    public static final String CATEGORY_ERROR_EXIST = "ERROR-CATEGORY-0001";

    /**
     * Starting error code GROUP
     * */
    public static final String GROUP_ERROR_NOT_FOUND = "ERROR-GROUP-0000";
    public static final String GROUP_ERROR_CANT_DELETE = "ERROR-GROUP-0001";

    /**
     * Starting error code SETTING
     */
    public static final String SETTING_ERROR_NOT_FOUND = "ERROR-SETTING-0000";
    public static final String SETTING_ERROR_EXISTED_GROUP_NAME_AND_KEY_NAME = "ERROR-SETTING-0001";

    /**
     * Starting error code TENANT
     * */
    public static final String TENANT_ERROR_NOT_FOUND = "ERROR-TENANT-0000";

    /**
     * Starting error code DATABASE_ERROR
     *
     */
    public static final String  ERROR_DB_QUERY = "ERROR-DB-QUERY-0000";

    /**
     * Starting error code FILE
     */
    public static final String FILE_ERROR_UPLOAD_TYPE_INVALID = "ERROR-FILE_0000";
}
