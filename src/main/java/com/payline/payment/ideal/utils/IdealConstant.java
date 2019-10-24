package com.payline.payment.ideal.utils;

public class IdealConstant {

    public static final String UNKNOWN = "UNKNOWN";
    public static final String STATUS_OK = "200";

    // data used in ConfigurationService
    public static final String MERCHANT_ID_KEY = "merchantId";
    public static final String MERCHANT_ID_LABEL = "merchantId.label";
    public static final String MERCHANT_ID_DESCRIPTION = "merchantId.description";

    public static final String MERCHANT_SUBID_KEY = "merchantSubId";
    public static final String MERCHANT_SUBID_LABEL = "merchantSubId.label";
    public static final String MERCHANT_SUBID_DESCRIPTION = "merchantSubId.description";


    // PartnerConfiguration constants
    public static final String URL_ABNAMRO = "URL_ABNAMRO";
    public static final String PUBLIC_KEY = "PUBLIC_KEY";
    public static final String IDEAL_PUBLIC = "IDEAL_PUBLIC";
    public static final String PRIVATE_KEY = "PRIVATE_KEY";
    public static final String PUBLIC_KEY_ID = "PUBLIC_KEY_ID";


    // Data used in PaymentFormConfigurationServiceImpl
    public static final boolean FORM_DISPLAY_PAYMENT_BUTTON = true;
    public static final String ISSUER_ID = "IssuerId";

    public static final String FORM_BUTTON_IDEAL_TEXT = "form.button.ideal.text";
    public static final String FORM_BUTTON_IDEAL_DESCRIPTION = "form.button.ideal.description";


}
