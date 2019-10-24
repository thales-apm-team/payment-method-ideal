package com.payline.payment.ideal.integration;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.service.impl.ConfigurationServiceImpl;
import com.payline.payment.ideal.service.impl.PaymentServiceImpl;
import com.payline.payment.ideal.service.impl.PaymentWithRedirectionServiceImpl;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TestIT extends AbstractPaymentIntegration {
    static final Logger LOGGER = LogManager.getLogger(TestIT.class);

    private ConfigurationServiceImpl configurationService = new ConfigurationServiceImpl();
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();
    private PaymentWithRedirectionServiceImpl redirectionService = new PaymentWithRedirectionServiceImpl();


    @Test
    public void fullPaymentTest() throws Exception {


        // connection to Swish backend test
        LOGGER.info("Testing Check request");
        ContractParametersCheckRequest checkRequest = Utils.createContractParametersCheckRequest();
        Map errors = configurationService.check(checkRequest);
        Assertions.assertTrue(errors.isEmpty());

        // payment test
        LOGGER.info("Testing payment request");
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().build();
        PaymentResponse paymentResponse = paymentService.paymentRequest(paymentRequest);
        Assertions.assertEquals(PaymentResponseSuccess.class, paymentResponse.getClass());


    }

    @Override
    protected ContractConfiguration generateContractConfiguration() {
        return Utils.createContractConfiguration();
    }

    @Override
    protected PartnerConfiguration generatePartnerConfiguration() {
        return Utils.createDefaultPartnerConfiguration();
    }

    @Override
    protected PaymentFormContext generatePaymentFormContext() {
        return Utils.createPaymentFormContext();
    }

    @Override
    protected String payOnPartnerWebsite(String url) {
        return null;
    }

    @Override
    protected String cancelOnPartnerWebsite(String url) {
        return null;
    }
}
