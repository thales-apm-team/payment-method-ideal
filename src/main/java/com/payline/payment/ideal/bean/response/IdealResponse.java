package com.payline.payment.ideal.bean.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.IdealError;

public class IdealResponse extends IdealBean {
    @JacksonXmlProperty(localName = "Error")
    private IdealError error;

    /**
     * Empty public constructor needed by JacksonXML
     */
    public IdealResponse() {
    }

    public IdealResponse(IdealError error) {
        this.error = error;
    }


    public IdealError getError() {
        return error;
    }
}
