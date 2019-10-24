package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Country {

    @JacksonXmlProperty(localName = "countryNames")
    private String countryNames;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Issuer")
    private List<Issuer> issuers;

    /**
     * Empty public constructor needed by JacksonXML
     */
    public Country() {
    }

    public String getCountryNames() {
        return countryNames;
    }

    public List<Issuer> getIssuers() {
        return issuers;
    }
}
