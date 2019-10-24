package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.bean.response.IdealPaymentResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.IdealConstant;
import com.payline.payment.ideal.utils.PluginUtils;
import com.payline.payment.ideal.utils.http.IdealHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;


public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

    private IdealHttpClient client = IdealHttpClient.getInstance();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        String partnerTransactionId = IdealConstant.UNKNOWN;

        try {
            IdealPaymentResponse response = client.transactionRequest(paymentRequest);

            if (response.getError() != null) {
                String errorCode = response.getError().getErrorCode();
                LOGGER.info(response.getError().toString());

                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withPartnerTransactionId(partnerTransactionId)
                        .withErrorCode(errorCode)
                        .withFailureCause(PluginUtils.getFailureCauseFromIdealErrorCode(errorCode))
                        .build();

            } else {
                partnerTransactionId = response.getTransaction().getTransactionId();

                PaymentResponseRedirect.RedirectionRequest redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                        .aRedirectionRequest()
                        .withUrl(new URL(response.getIssuer().getIssuerAuthenticationURL()))
                        .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                        .build();

                return PaymentResponseRedirect.PaymentResponseRedirectBuilder
                        .aPaymentResponseRedirect()
                        .withRedirectionRequest(redirectionRequest)
                        .withPartnerTransactionId(partnerTransactionId)
                        .withStatusCode(IdealConstant.STATUS_OK)
                        .build();

            }
        } catch (MalformedURLException e) {
            LOGGER.error("received URL isn't well formed", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(e.getMessage())
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        } catch (PluginException e) {
            return e.toPaymentResponseFailureBuilder()
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
    }


}
