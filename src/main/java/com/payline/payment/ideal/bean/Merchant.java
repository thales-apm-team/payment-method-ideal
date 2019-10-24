package com.payline.payment.ideal.bean;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.utils.PluginUtils;

@JacksonXmlRootElement(localName = "Merchant")
public class Merchant {

    @JacksonXmlProperty(localName = "merchantID")
    private String merchantId;

    @JacksonXmlProperty(localName = "subID")
    private String subId;

    @JacksonXmlProperty(localName = "merchantReturnURL")
    private String merchantReturnURL;

    /**
     * Empty public constructor needed by JacksonXML
     */
    public Merchant() {
    }

    public Merchant(String merchantId, String subId) {
        this.merchantId = merchantId;
        this.subId = subId;

        if (PluginUtils.isEmpty(this.subId)) this.subId = "0";
    }

    public Merchant(String merchantId, String subId, String redirectionURL) {
        this.merchantId = merchantId;
        this.subId = subId;
        this.merchantReturnURL = redirectionURL;

        if (PluginUtils.isEmpty(this.subId)) this.subId = "0";
    }
}
