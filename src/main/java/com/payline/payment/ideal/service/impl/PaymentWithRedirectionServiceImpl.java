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
            LOGGER.info("an error occurred: {}",response.getError());

            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(errorCode)
                    .withFailureCause(PluginUtils.getFailureCauseFromIdealErrorCode(errorCode))
                    .build();

        } else {
            partnerTransactionId = response.getTransaction().getTransactionId();

            // check the status response: "SUCCESS", "OPEN", other...
            Transaction.Status status = response.getTransaction().getStatus();
            if (Transaction.Status.SUCCESS.equals(status)) {
                return PaymentResponseSuccess.PaymentResponseSuccessBuilder
                        .aPaymentResponseSuccess()
                        .withPartnerTransactionId(partnerTransactionId)
                        .withStatusCode(status.name())
                        .withTransactionAdditionalData(response.getTransaction().getConsumerIBAN())
                        .withTransactionDetails(new EmptyTransactionDetails())
                        .build();
            } else if (Transaction.Status.OPEN.equals(status)) {
                return PaymentResponseOnHold.PaymentResponseOnHoldBuilder
                        .aPaymentResponseOnHold()
                        .withOnHoldCause(OnHoldCause.ASYNC_RETRY)
                        .withBuyerPaymentId(new EmptyTransactionDetails())
                        .withPartnerTransactionId(partnerTransactionId)
                        .withStatusCode(status.name())
                        .build();
            } else {
                FailureCause cause = FailureCause.PARTNER_UNKNOWN_ERROR;
                if (Transaction.Status.CANCELLED.equals(status)) {
                    cause = FailureCause.CANCEL;
                } else if (Transaction.Status.FAILURE.equals(status)) {
                    cause = FailureCause.REFUSED;
                } else if (Transaction.Status.EXPIRED.equals(status)) {
                    cause = FailureCause.SESSION_EXPIRED;
                }

                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withPartnerTransactionId(partnerTransactionId)
                        .withErrorCode(status.name())
                        .withFailureCause(cause)
                        .build();

            }
        }

    }


}
