package com.payline.payment.ideal.bean.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.Merchant;
import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;

@JacksonXmlRootElement(localName = "DirectoryReq", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
public class IdealDirectoryRequest extends IdealBean {

    @JacksonXmlProperty(localName = "Merchant")
    private Merchant merchant;

    public IdealDirectoryRequest() {
    }

    public IdealDirectoryRequest(ContractParametersCheckRequest request) {
        super();
        this.merchant = new Merchant(request.getAccountInfo().get(ContractConfigurationKeys.MERCHANT_ID_KEY)
                , request.getAccountInfo().get(ContractConfigurationKeys.MERCHANT_SUBID_KEY));
    }


    public IdealDirectoryRequest(ContractConfiguration configuration) {
        super();
        this.merchant = new Merchant(configuration.getProperty(ContractConfigurationKeys.MERCHANT_ID_KEY).getValue()
                , configuration.getProperty(ContractConfigurationKeys.MERCHANT_SUBID_KEY).getValue());

    }

    public Merchant getMerchant() {
        return merchant;
    }
}
