package com.payline.payment.ideal.bean.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.Merchant;
import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;

@JacksonXmlRootElement(localName = "AcquirerStatusReq", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
public class IdealStatusRequest extends IdealBean {

    @JacksonXmlProperty(localName = "Merchant")
    private Merchant merchant;

    @JacksonXmlProperty(localName = "Transaction")
    private Transaction transaction;

    public IdealStatusRequest(RedirectionPaymentRequest request) {
        this.merchant = new Merchant(
                request.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID_KEY).getValue()
                , request.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_SUBID_KEY).getValue());

        // in RedirectionPaymentRequests, field 'transactionId' is the partner transactionId
        this.transaction = Transaction.builder()
                .transactionId(request.getTransactionId())
                .build();
    }

    public IdealStatusRequest(TransactionStatusRequest request) {
        this.merchant = new Merchant(
                request.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID_KEY).getValue()
                , request.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_SUBID_KEY).getValue());

        // in TransactionStatusRequest, field 'transactionId' is the partner transactionId
        this.transaction = Transaction.builder()
                .transactionId(request.getTransactionId())
                .build();
    }
}
