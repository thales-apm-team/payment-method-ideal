package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class IdealError {

    @JacksonXmlProperty(localName = "errorCode")
    private String errorCode;

    @JacksonXmlProperty(localName = "errorMessage")
    private String errorMessage;

    @JacksonXmlProperty(localName = "errorDetail")
    private String errorDetail;

    @JacksonXmlProperty(localName = "suggestedAction")
    private String suggestedAction;

    @JacksonXmlProperty(localName = "consumerMessage")
    private String consumerMessage;

    /**
     * Empty public constructor needed by JacksonXML
     */
    public IdealError() {
    }

    public IdealError(String errorCode, String errorMessage, String errorDetail, String suggestedAction, String consumerMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
        this.suggestedAction = suggestedAction;
        this.consumerMessage = consumerMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public String getSuggestedAction() {
        return suggestedAction;
    }

    public String getConsumerMessage() {
        return consumerMessage;
    }
}