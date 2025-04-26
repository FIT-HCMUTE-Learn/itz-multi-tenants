package com.multi.tenants.api.constant;

public class ITzBaseConstant {
    public static final String ENTITY_STRATEGY_ID = "com.multi.tenants.api.service.id.IdGenerator";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static final Integer USER_KIND_ADMIN = 1;
    public static final Integer USER_KIND_MANAGER = 2;
    public static final Integer USER_KIND_USER = 3;
    public static final Integer USER_KIND_EMPLOYEE = 4;

    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_PENDING = 0;
    public static final Integer STATUS_LOCK = -1;
    public static final Integer STATUS_DELETE = -2;

    public static final String SETTING_DATA_TYPE_STRING = "string";
    public static final String SETTING_DATA_TYPE_INTEGER = "integer";
    public static final String SETTING_DATA_TYPE_LONG = "long";
    public static final String SETTING_DATA_TYPE_DOUBLE = "double";

    public static final Integer MAX_ATTEMPT_FORGET_PWD = 5;
    public static final Integer MAX_ATTEMPT_LOGIN = 5;
    public static final int MAX_TIME_FORGET_PWD = 5 * 60 * 1000; //5 minutes

    public static final String FILE_PUBLIC_FOLDER = "public";

    public static final Integer CATEGORY_KIND_NEWS = 1;
    public static final Integer CATEGORY_KIND_BRAND = 2;
    public static final Integer CATEGORY_KIND_DEVICE = 3;
    public static final Integer CATEGORY_KIND_DEVICE_COST_HISTORY = 4;

    private ITzBaseConstant(){
        throw new IllegalStateException("Utility class");
    }
}
