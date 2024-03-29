package com.nle.constant;

public class AppConstant {
    public static final String CREATED_DATE = "createdDate";
    public static final String SPLASH = "/";
    public static final String TAX_MINISTRY_HEADER_KEY = "Beacukai-Api-Key";

    public static final String[] MEMBER_FIELDS_TO_BIND_TO = new String[]{
        "tx_date",
        "process_type",
        "depot",
        "fleet_manager",
        "container_number",
        "iso_code",
        "condition",
        "date_manufacturer",
        "clean",
        "grade",
        "order_number",
        "customer",
        "vessel",
        "voyage",
        "discarge_port",
        "delivery_port",
        "carrier",
        "transport_number",
        "driver_name",
        "tare",
        "payload",
        "max_gross",
        "remark"};
    public static final String TIME_ZONE = "+07:00";
    public static final String DEPO_OWNER_IMPERSONATE_TOKEN = "DEPO_OWNER_IMPERSONATE_TOKEN";

    private AppConstant() {
    }

    public static class Status {
        public static final String WAITING = "WAITING";
        public static final String SUBMITTED = "SUBMITTED";

        public static final String ALREADY = "ALREADY";
    }

    public static class VerificationStatus {
        public static final String INACTIVE = "INACTIVE";
        public static final String ACTIVE = "ACTIVE";
        public static final String ALREADY_ACTIVE = "ALREADY_ACTIVE";
    }


    public static class Pattern {
        public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
        public static final String NAME_PATTERN = "^[a-zA-Z]{2,30}$";
        public static final String NUMERIC_PATTERN = "^\\d+$";
        public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,15}$";
        public static final String PHONE_NUMBER_PATTERN = "^\\d{5,15}$";

        private Pattern() {
        }
    }
}
