package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Directory {

    @JacksonXmlProperty(localName = "directoryDateTimestamp")
    private String directoryDateTimestamp;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Country")
    private List<Country> countries;

    /**
     * Empty public constructor needed by JacksonXML
     */
    public Directory() {
    }

    public String getDirectoryDateTimestamp() {
        return directoryDateTimestamp;
    }

    public List<Country> getCountries() {
        return countries;
    }
}
