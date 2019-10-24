package com.payline.payment.ideal.integration;

import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.service.PaymentService;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.junit.jupiter.api.Assertions;

/**
 * This is an Abstract test class to implement integration test.
 * It must be extended and its abstract method implemented.
 */
public abstract class AbstractPaymentIntegration {
    public static final String SUCCESS_URL = "https://succesurl.com/";
    public static final String CANCEL_URL = "http://cancelurl.com/";
    public static final String NOTIFICATION_URL = "http://google.com/";

    AbstractPaymentIntegration() {
    }

    protected abstract ContractConfiguration generateContractConfiguration();

    protected Environment generateEnvironment() {
        return new Environment(
                NOTIFICATION_URL,
                SUCCESS_URL,
                CANCEL_URL,
                true
        );
    }

    protected abstract PartnerConfiguration generatePartnerConfiguration();

    protected abstract PaymentFormContext generatePaymentFormContext();


    protected abstract String payOnPartnerWebsite(String url);

    protected abstract String cancelOnPartnerWebsite(String url);

    public void fullRedirectionPayment(PaymentRequest paymentRequest, PaymentService paymentService, PaymentWithRedirectionService paymentWithRedirectionService) {
        // Payment initialization
        PaymentResponse paymentResponseFromPaymentRequest = paymentService.paymentRequest(paymentRequest);
        this.checkPaymentResponseIsNotFailure(paymentResponseFromPaymentRequest);
        this.checkPaymentResponseIsRightClass("paymentRequest", paymentResponseFromPaymentRequest, PaymentResponseRedirect.class);

        // Realize or confirm payment on the partner UI
        PaymentResponseRedirect paymentResponseRedirect = (PaymentResponseRedirect) paymentResponseFromPaymentRequest;
        String partnerUrl = paymentResponseRedirect.getRedirectionRequest().getUrl().toString();
        String redirectionUrl = this.payOnPartnerWebsite(partnerUrl);
        Assertions.assertEquals(SUCCESS_URL, redirectionUrl);

        // Finalize payment (following the redirection to the merchant site)
        String partnerTransactionId = paymentResponseRedirect.getPartnerTransactionId();
        PaymentResponse paymentResponseFromFinalize = this.handlePartnerResponse(paymentWithRedirectionService, paymentRequest, paymentResponseRedirect);
        this.checkPaymentResponseIsNotFailure(paymentResponseFromFinalize);
        this.checkPaymentResponseIsRightClass("redirectionPaymentRequest", paymentResponseFromFinalize, PaymentResponseSuccess.class);
        PaymentResponseSuccess paymentResponseSuccess = (PaymentResponseSuccess) paymentResponseFromFinalize;
        Assertions.assertNotNull(paymentResponseSuccess.getTransactionDetails());
        Assertions.assertEquals(partnerTransactionId, paymentResponseSuccess.getPartnerTransactionId());
    }

    private PaymentResponse handlePartnerResponse(PaymentWithRedirectionService paymentWithRedirectionService, PaymentRequest paymentRequest, PaymentResponseRedirect paymentResponseRedirect) {
        RedirectionPaymentRequest redirectionPaymentRequest = (RedirectionPaymentRequest) RedirectionPaymentRequest.builder()
                .withAmount(paymentRequest.getAmount())
                .withBrowser(paymentRequest.getBrowser())
                .withBuyer(paymentRequest.getBuyer())
                .withContractConfiguration(this.generateContractConfiguration())
                .withEnvironment(this.generateEnvironment())
                .withOrder(paymentRequest.getOrder())
                .withPartnerConfiguration(this.generatePartnerConfiguration())
                .withPaymentFormContext(this.generatePaymentFormContext())
                .withRequestContext(paymentResponseRedirect.getRequestContext())
                //.withTransactionId( paymentRequest.getTransactionId() )
                .withTransactionId(paymentResponseRedirect.getPartnerTransactionId())
                .build();
        return paymentWithRedirectionService.finalizeRedirectionPayment(redirectionPaymentRequest);
    }

    private void checkPaymentResponseIsNotFailure(PaymentResponse paymentResponse) {
        Assertions.assertFalse(paymentResponse instanceof PaymentResponseFailure, () -> {
            return "paymentRequest returned PaymentResponseFailure (Failure cause = " + ((PaymentResponseFailure) paymentResponse).getFailureCause() + ", errorCode = " + ((PaymentResponseFailure) paymentResponse).getErrorCode();
        });
    }

    private void checkPaymentResponseIsRightClass(String requestName, PaymentResponse paymentResponse, Class clazz) {
        Assertions.assertTrue(paymentResponse.getClass().isAssignableFrom(clazz), () -> {
            return requestName + " did not return a " + clazz.getSimpleName() + " (" + paymentResponse.toString() + ")";
        });
    }
}
