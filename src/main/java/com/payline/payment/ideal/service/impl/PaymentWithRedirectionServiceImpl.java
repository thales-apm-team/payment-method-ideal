package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.bean.response.IdealStatusResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.PluginUtils;
import com.payline.payment.ideal.utils.http.IdealHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.apache.logging.log4j.Logger;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentWithRedirectionServiceImpl.class);

    private IdealHttpClient client = IdealHttpClient.getInstance();

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        String partnerTransactionId = redirectionPaymentRequest.getTransactionId();
        try {
            IdealStatusResponse response = client.statusRequest(redirectionPaymentRequest);
            return this.handleResponse(partnerTransactionId, response);

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

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        String partnerTransactionId = transactionStatusRequest.getTransactionId();
        try {
            IdealStatusResponse response = client.statusRequest(transactionStatusRequest);
            return handleResponse(partnerTransactionId, response);

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

    PaymentResponse handleResponse(String partnerTransactionId, IdealStatusResponse response) {


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

            // check the status response: "SUCCESS", "OPEN", other...
            String status = response.getTransaction().getStatus();

            if (Transaction.Status.SUCCESS.equalsIgnoreCase(status)) {
                return PaymentResponseSuccess.PaymentResponseSuccessBuilder
                        .aPaymentResponseSuccess()
                        .withPartnerTransactionId(partnerTransactionId)
                        .withStatusCode(status)
                        .withTransactionAdditionalData(response.getTransaction().getConsumerIBAN())
                        .withTransactionDetails(new EmptyTransactionDetails())
                        .build();
            } else if (Transaction.Status.OPEN.equalsIgnoreCase(status)) {
                return PaymentResponseOnHold.PaymentResponseOnHoldBuilder
                        .aPaymentResponseOnHold()
                        .withOnHoldCause(OnHoldCause.ASYNC_RETRY)
                        .withBuyerPaymentId(new EmptyTransactionDetails())
                        .withPartnerTransactionId(partnerTransactionId)
                        .withStatusCode(status)
                        .build();
            } else {
                FailureCause cause = FailureCause.PARTNER_UNKNOWN_ERROR;
                if (Transaction.Status.CANCELLED.equalsIgnoreCase(status)) {
                    cause = FailureCause.CANCEL;
                } else if (Transaction.Status.FAILURE.equalsIgnoreCase(status)) {
                    cause = FailureCause.REFUSED;
                } else if (Transaction.Status.EXPIRED.equalsIgnoreCase(status)) {
                    cause = FailureCause.SESSION_EXPIRED;
                }

                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withPartnerTransactionId(partnerTransactionId)
                        .withErrorCode(status)
                        .withFailureCause(cause)
                        .build();

            }
        }

    }


}
