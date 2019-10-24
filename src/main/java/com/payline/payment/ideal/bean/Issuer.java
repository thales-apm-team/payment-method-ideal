package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Issuer {

    @JacksonXmlProperty(localName = "issuerID")
    private String issuerId;

    @JacksonXmlProperty(localName = "issuerName")
    private String issuerName;

    @JacksonXmlProperty(localName = "issuerAuthenticationURL")
    private String issuerAuthenticationURL;


    /**
     * Empty public constructor needed by JacksonXML
     */
    public Issuer() {
    }

    private Issuer(IssuerBuilder builder) {
        this.issuerId = builder.issuerId;
        this.issuerName = builder.issuerName;
        this.issuerAuthenticationURL = builder.issuerAuthenticationURL;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getIssuerAuthenticationURL() {
        return issuerAuthenticationURL;
    }

    public static class IssuerBuilder {
        private String issuerId;
        private String issuerName;
        private String issuerAuthenticationURL;

        public static IssuerBuilder anIssuerBuilder() {
            return new IssuerBuilder();
        }

        public IssuerBuilder withId(String issuerId) {
            this.issuerId = issuerId;
            return this;
        }

        public IssuerBuilder withName(String issuerName) {
            this.issuerName = issuerName;
            return this;
        }

        public IssuerBuilder withUrl(String url) {
            this.issuerAuthenticationURL = url;
            return this;
        }

        public Issuer build() {
            return new Issuer(this);
        }

    }
}
