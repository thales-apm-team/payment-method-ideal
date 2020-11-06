package com.payline.payment.ideal.utils;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.bean.request.IdealDirectoryRequest;
import com.payline.payment.ideal.bean.request.IdealPaymentRequest;
import com.payline.payment.ideal.bean.request.IdealStatusRequest;
import com.payline.payment.ideal.bean.response.IdealDirectoryResponse;
import com.payline.payment.ideal.bean.response.IdealPaymentResponse;
import com.payline.payment.ideal.bean.response.IdealStatusResponse;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class XMLUtilsTest {

    @InjectMocks
    XMLUtils xmlUtils;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void toXmlDirectoryRequest() {
        ContractConfiguration configuration = Utils.createContractConfiguration();
        IdealDirectoryRequest request = new IdealDirectoryRequest(configuration);

        String s = xmlUtils.toXml(request);

        Assertions.assertNotNull(s);
        Assertions.assertTrue(s.contains("<Merchant"));
        Assertions.assertTrue(s.contains("<merchantID"));


    }

    @Test
    void toXmlPaymentRequest() {
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().build();
        IdealPaymentRequest request = new IdealPaymentRequest(paymentRequest);

        String s = xmlUtils.toXml(request);

        Assertions.assertNotNull(s);
        Assertions.assertTrue(s.contains("<Issuer"));
        Assertions.assertTrue(s.contains("<issuerID"));
        Assertions.assertTrue(s.contains("<Merchant"));
        Assertions.assertTrue(s.contains("<merchantID"));
        Assertions.assertTrue(s.contains("<merchantReturnURL"));
        Assertions.assertTrue(s.contains("<Transaction"));
        Assertions.assertTrue(s.contains("<purchaseID"));
        Assertions.assertTrue(s.contains("<amount"));
        Assertions.assertTrue(s.contains("<currency"));
        Assertions.assertTrue(s.contains("<expirationPeriod"));
    }

    @Test
    void toXmlStatusRequest() {

        RedirectionPaymentRequest redirectionPaymentRequest = Utils.createCompleteRedirectionPayment("123123");
        IdealStatusRequest request = new IdealStatusRequest(redirectionPaymentRequest);
        String s = xmlUtils.toXml(request);

        Assertions.assertNotNull(s);
        Assertions.assertTrue(s.contains("<Merchant"));
        Assertions.assertTrue(s.contains("<merchantID"));
        Assertions.assertTrue(s.contains("<Transaction"));
        Assertions.assertTrue(s.contains("<transactionID"));
    }

    @Test
    void fromXMLDirectoryResponse() {
        IdealDirectoryResponse response =  xmlUtils.fromXML(Utils.directoryResponseOK, IdealDirectoryResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getAcquirer());
        Assertions.assertNotNull(response.getDirectory());
        Assertions.assertNotNull(response.getDirectory().getCountries());
    }


    @Test
    void fromXMLPaymentResponse() {
        IdealPaymentResponse response = xmlUtils.fromXML(Utils.TransactionResponse, IdealPaymentResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getIssuer());
        Assertions.assertNotNull(response.getIssuer().getIssuerAuthenticationURL());
        Assertions.assertNotNull(response.getTransaction());
        Assertions.assertNotNull(response.getTransaction().getTransactionId());
        Assertions.assertNotNull(response.getAcquirer());
    }


    @Test
    void fromXMLStatusResponse() {
        IdealStatusResponse response = xmlUtils.fromXML(Utils.statusResponseOK, IdealStatusResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getAcquirer());
        Assertions.assertNotNull(response.getTransaction());
        Assertions.assertNotNull(response.getTransaction().getStatus());
        Assertions.assertNotNull(response.getTransaction().getConsumerIBAN());

    }
}