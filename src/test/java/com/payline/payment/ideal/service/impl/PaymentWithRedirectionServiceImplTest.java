package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.bean.IdealError;
import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.bean.response.IdealStatusResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.http.IdealHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentWithRedirectionServiceImplTest {
    @Mock
    private IdealHttpClient client;

    @InjectMocks
    @Spy
    private PaymentWithRedirectionServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void finalizeRedirectionPayment() {
        // create mock
        String id = "anId";
        doReturn(new IdealStatusResponse()).when(client).statusRequest(any(RedirectionPaymentRequest.class));

        PaymentResponseSuccess success = PaymentResponseSuccess.PaymentResponseSuccessBuilder
                .aPaymentResponseSuccess()
                .withTransactionDetails(new EmptyTransactionDetails())
                .withPartnerTransactionId(id)
                .build();
        doReturn(success).when(service).handleResponse(any(), any());

        // call method
        RedirectionPaymentRequest request = Utils.createCompleteRedirectionPayment(id);
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        // assertions
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
        PaymentResponseSuccess responseSuccess = (PaymentResponseSuccess) response;
        Assertions.assertEquals(id, responseSuccess.getPartnerTransactionId());
    }

    @Test
    void finalizeRedirectionPaymentWithPluginException() {
        // create mock
        RedirectionPaymentRequest request = Utils.createCompleteRedirectionPayment("anId");
        doThrow(new PluginException("an error")).when(service).handleResponse(any(), any());

        // call method
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void handleSessionExpired() {
        // create mock
        String id = "anId";

        doReturn(mock(IdealStatusResponse.class)).when(client).statusRequest(any(TransactionStatusRequest.class));

        PaymentResponseSuccess success = PaymentResponseSuccess.PaymentResponseSuccessBuilder
                .aPaymentResponseSuccess()
                .withTransactionDetails(new EmptyTransactionDetails())
                .withPartnerTransactionId(id)
                .build();
        doReturn(success).when(service).handleResponse(any(), any());

        // call method
        TransactionStatusRequest request = Utils.createTransactionRequestBuilder().build();
        PaymentResponse response = service.handleSessionExpired(request);

        // assertions
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
        PaymentResponseSuccess responseSuccess = (PaymentResponseSuccess) response;
        Assertions.assertEquals(id, responseSuccess.getPartnerTransactionId());
    }

    @Test
    void handleSessionExpiredWithPluginException() {
        // create mock
        TransactionStatusRequest request = Utils.createTransactionRequestBuilder().build();
        doThrow(new PluginException("an error")).when(service).handleResponse(any(), any());

        // call method
        PaymentResponse response = service.handleSessionExpired(request);

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void handleResponseSUCCESS() {
        // create mock
        String id = "anId";
        Transaction transaction = Transaction.builder()
                .transactionId(id)
                .status(Transaction.Status.SUCCESS)
                .build();

        IdealStatusResponse idealStatusResponse = new IdealStatusResponse(null, transaction);

        // call method
        PaymentResponse response = service.handleResponse(id, idealStatusResponse);

        // assertions
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
        PaymentResponseSuccess responseSuccess = (PaymentResponseSuccess) response;
        Assertions.assertEquals(id, responseSuccess.getPartnerTransactionId());
        Assertions.assertEquals(Transaction.Status.SUCCESS.name(), responseSuccess.getStatusCode());
    }

    @Test
    void handleResponseOTHER() {
        // create mock
        String id = "anId";
        Transaction transaction = Transaction.builder()
                .transactionId(id)
                .status(Transaction.Status.CANCELLED)
                .build();
        IdealStatusResponse idealStatusResponse = new IdealStatusResponse(null, transaction);

        // call method
        PaymentResponse response = service.handleResponse(id, idealStatusResponse);

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(id, responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(Transaction.Status.CANCELLED.name(), responseFailure.getErrorCode());
    }




    private static Stream<Arguments> parse_nonExistingTransaction_set() {
        return Stream.of(
                Arguments.of(Transaction.Status.CANCELLED, FailureCause.CANCEL),
                Arguments.of(Transaction.Status.FAILURE, FailureCause.REFUSED),
                Arguments.of(Transaction.Status.EXPIRED, FailureCause.SESSION_EXPIRED)
        );
    }

    @ParameterizedTest
    @MethodSource("parse_nonExistingTransaction_set")
    void handleResponseOTHER(Transaction.Status status, FailureCause expectedCause) {
        // create mock
        String id = "anId";
        Transaction transaction = Transaction.builder()
                .transactionId(id)
                .status(status)
                .build();
        IdealStatusResponse idealStatusResponse = new IdealStatusResponse(null, transaction);

        // call method
        PaymentResponse response = service.handleResponse(id, idealStatusResponse);

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(id, responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(status.name(), responseFailure.getErrorCode());
        Assertions.assertEquals(expectedCause, responseFailure.getFailureCause());
    }

    @Test
    void handleResponseWithError() {
        // create mock
        String id = "anId";
        String errorCode = "AP110";
        IdealError error = new IdealError(errorCode
                , "message"
                , "details"
                , "action"
                , "consumerMessage");
        IdealStatusResponse idealStatusResponse = new IdealStatusResponse(error, null, null);

        // call method
        PaymentResponse response = service.handleResponse(id, idealStatusResponse);

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(id, responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(errorCode, responseFailure.getErrorCode());
    }
}