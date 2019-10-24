package com.payline.payment.ideal.bean.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.Acquirer;
import com.payline.payment.ideal.bean.IdealError;
import com.payline.payment.ideal.bean.Transaction;

@JacksonXmlRootElement(localName = "AcquirerStatusRes", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
public class IdealStatusResponse extends IdealResponse {

    @JacksonXmlProperty(localName = "Acquirer")
    private Acquirer acquirer;

    @JacksonXmlProperty(localName = "Transaction")
    private Transaction transaction;

    public IdealStatusResponse() {
    }

    public IdealStatusResponse(Acquirer acquirer, Transaction transaction) {
        this.acquirer = acquirer;
        this.transaction = transaction;
    }

    public IdealStatusResponse(IdealError error, Acquirer acquirer, Transaction transaction) {
        super(error);
        this.acquirer = acquirer;
        this.transaction = transaction;
    }

    public Acquirer getAcquirer() {
        return acquirer;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
