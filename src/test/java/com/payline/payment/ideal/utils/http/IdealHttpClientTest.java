package com.payline.payment.ideal.utils.http;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.bean.request.IdealDirectoryRequest;
import com.payline.payment.ideal.bean.response.IdealDirectoryResponse;
import com.payline.payment.ideal.bean.response.IdealPaymentResponse;
import com.payline.payment.ideal.bean.response.IdealStatusResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.security.SignatureUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.FieldSetter;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class IdealHttpClientTest {
    private String signedResponse = "foo";

    @InjectMocks
    @Spy
    private IdealHttpClient client = IdealHttpClient.getInstance();

    @Mock
    private SignatureUtils signatureUtils;

    @Mock
    private CloseableHttpClient closeableHttpClient;



    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        doReturn(null).when(signatureUtils).getPrivateKeyFromString(anyString());
        doReturn(null).when(signatureUtils).getPublicKeyFromString(anyString());
        doReturn(signedResponse).when(signatureUtils).signXML(any(), any(), any(), any());

    }

    @Test
    void directoryRequest() throws Exception {
        // init mock
        StringResponse stringResponse = mockStringResponse(200, null, Utils.directoryResponseOK, null);


        doReturn("hello").when(client).createBody(any(), any());
        doReturn(stringResponse).when(client).getStringResponse(anyString(), any());
        doNothing().when(signatureUtils).verifySignatureXML(any(), any());


        // call method
        ContractParametersCheckRequest contractParametersCheckRequest = Utils.createContractParametersCheckRequest();
        IdealDirectoryResponse response = client.directoryRequest(contractParametersCheckRequest);

        // assertions
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.getDirectory().getCountries().isEmpty());

        // then: no HTTP call is made
        verify(closeableHttpClient, never()).execute(any(HttpRequestBase.class));
    }

    @Test
    void directoryRequestWithError() throws Exception {
        // init mock
        StringResponse stringResponse = mockStringResponse(200, null, Utils.errorResponse, null);

        doReturn("hello").when(client).createBody(any(), any());
        doReturn(stringResponse).when(client).getStringResponse(anyString(), any());
        doNothing().when(signatureUtils).verifySignatureXML(any(), any());

        // call method
        ContractParametersCheckRequest contractParametersCheckRequest = Utils.createContractParametersCheckRequest();
        IdealDirectoryResponse response = client.directoryRequest(contractParametersCheckRequest);

        // assertions
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getError());

        // then: no HTTP call is made
        verify(closeableHttpClient, never()).execute(any(HttpRequestBase.class));
    }


    @Test
    void transactionRequest() throws Exception {
        // init mock
        StringResponse stringResponse = mockStringResponse(200, null, Utils.TransactionResponse, null);

        doReturn("hello").when(client).createBody(any(), any());
        doReturn(stringResponse).when(client).getStringResponse(anyString(), any());
        doNothing().when(signatureUtils).verifySignatureXML(any(), any());

        // call method
        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        IdealPaymentResponse response = client.transactionRequest(request);

        // assertions
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getTransaction());
        Assertions.assertNotNull(response.getTransaction().getTransactionId());
        Assertions.assertNotNull(response.getIssuer());
        Assertions.assertNotNull(response.getIssuer().getIssuerAuthenticationURL());

        // then: no HTTP call is made
        verify(closeableHttpClient, never()).execute(any(HttpRequestBase.class));
    }

    @Test
    void statusRequest() throws Exception {
        // init mock
        StringResponse stringResponse = mockStringResponse(200, null, Utils.statusResponseOK, null);

        doReturn("hello").when(client).createBody(any(), any());
        doReturn(stringResponse).when(client).getStringResponse(anyString(), any());
        doNothing().when(signatureUtils).verifySignatureXML(any(), any());


        // call method
        TransactionStatusRequest request = Utils.createTransactionRequestBuilder().build();
        IdealStatusResponse response = client.statusRequest(request);

        // assertions
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getTransaction());
        Assertions.assertNotNull(response.getTransaction().getTransactionId());
        Assertions.assertNotNull(response.getTransaction().getConsumerIBAN());
        Assertions.assertNotNull(response.getTransaction().getStatus());

        // then: no HTTP call is made
        verify(closeableHttpClient, never()).execute(any(HttpRequestBase.class));
    }


    @Test
    void checkResponse() {
        // create data
        StringResponse response = mockStringResponse(200, null, "this is an answer", null);

        // call method
        Assertions.assertDoesNotThrow(() -> client.checkResponse(response));
    }

    @Test
    void checkResponse403() {


        // create data
        StringResponse response = mockStringResponse(403, null, "this is an answer", null);

        // call method
        try {
            client.checkResponse(response);
            Assertions.fail("CheckResponse with code 403 did not throws Exception");
        } catch (PluginException e) {
            // assertions
            Assertions.assertEquals("this is an answer", e.getErrorCode());
            Assertions.assertEquals(FailureCause.INVALID_DATA, e.getFailureCause());
        }
    }

    @Test
    void checkResponse505() {


        // create data
        StringResponse response = mockStringResponse(505, null, "this is an answer", null);

        // call method
        try {
            client.checkResponse(response);
            Assertions.fail("CheckResponse with code 505 did not throws Exception");
        } catch (PluginException e) {
            // assertions
            Assertions.assertEquals("this is an answer", e.getErrorCode());
            Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, e.getFailureCause());
        }
    }

    @Test
    void createBody() {
        IdealDirectoryRequest request = new IdealDirectoryRequest(Utils.createContractConfiguration());

        String body = client.createBody(request, Utils.createDefaultPartnerConfiguration());

        Assertions.assertNotNull(body);
        Assertions.assertEquals(signedResponse, body);
    }


    private static StringResponse mockStringResponse(int statusCode, String statusMessage, String content, Map<String, String> headers) {
        StringResponse response = new StringResponse();

        try {
            if (content != null && !content.isEmpty()) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("content"), content);
            }
            if (headers != null && headers.size() > 0) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("headers"), headers);
            }
            if (statusCode >= 100 && statusCode < 600) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("statusCode"), statusCode);
            }
            if (statusMessage != null && !statusMessage.isEmpty()) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("statusMessage"), statusMessage);
            }
        } catch (NoSuchFieldException e) {
            // This would happen in a testing context: spare the exception throw, the test case will probably fail anyway
            return null;
        }

        return response;
    }
}