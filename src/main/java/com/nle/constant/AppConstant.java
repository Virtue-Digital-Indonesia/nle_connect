package com.nle.constant;

public class AppConstant {
    private AppConstant() {
    }
    public static class Pattern {
        public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
        public static final String NAME_PATTERN = "^[a-zA-Z]{2,30}$";
        public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,15}$";
        public static final String PHONE_NUMBER_PATTERN = "^\\d{5,15}$";
        private Pattern() {
        }
    }
}
