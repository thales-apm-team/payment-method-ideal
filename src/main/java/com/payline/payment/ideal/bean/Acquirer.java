package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Acquirer {

    @JacksonXmlProperty(localName = "acquirerID")
    private String acquirerId;

    /**
     * Empty public constructor needed by JacksonXML
     */
    public Acquirer() {
    }

    public Acquirer(String acquirerId) {
        this.acquirerId = acquirerId;
    }
}
