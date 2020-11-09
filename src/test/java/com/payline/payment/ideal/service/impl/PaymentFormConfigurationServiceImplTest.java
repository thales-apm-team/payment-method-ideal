package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.pmapi.bean.paymentform.bean.field.SelectOption;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;


class PaymentFormConfigurationServiceImplTest {
    private static final String countries = "<Directory>" +
            "      <directoryDateTimestamp>2004-11-10T10:15:12.145Z</directoryDateTimestamp>" +
            "      <Country>" +
            "         <countryNames>Nederland</countryNames>" +
            "         <Issuer>" +
            "            <issuerID>ABNANL2AXXX</issuerID>" +
            "            <issuerName>ABN AMRO Bank</issuerName>" +
            "         </Issuer>" +
            "         <Issuer>" +
            "            <issuerID>FRBKNL2LXXX</issuerID>" +
            "            <issuerName>Friesland Bank</issuerName>" +
            "         </Issuer>" +
            "         <Issuer>" +
            "            <issuerID>INGBNL2AXXX</issuerID>" +
            "            <issuerName>ING</issuerName>" +
            "         </Issuer>" +
            "      </Country>" +
            "      <Country>" +
            "         <countryNames>België/Belgique</countryNames>" +
            "         <Issuer>" +
            "            <issuerID>KREDBE22XXX</issuerID>" +
            "            <issuerName>KBC</issuerName>" +
            "         </Issuer>" +
            "      </Country>" +
            "   </Directory>";

    @Spy
    PaymentFormConfigurationServiceImpl service = new PaymentFormConfigurationServiceImpl();

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getPaymentFormConfiguration() {
        // create data
        PaymentFormConfigurationRequest request = Utils.createDefaultPaymentFormConfigurationRequestBuilder()
                .withPluginConfiguration(countries)
                .build();

        // call method
        PaymentFormConfigurationResponse formConfiguration = service.getPaymentFormConfiguration(request);

        // assertion
        Assertions.assertEquals(PaymentFormConfigurationResponseSpecific.class, formConfiguration.getClass());
        PaymentFormConfigurationResponseSpecific responseSpecific = (PaymentFormConfigurationResponseSpecific) formConfiguration;

        Assertions.assertEquals(BankTransferForm.class, responseSpecific.getPaymentForm().getClass());
        BankTransferForm bankTransferForm = (BankTransferForm) responseSpecific.getPaymentForm();
        Assertions.assertFalse(bankTransferForm.getBanks().isEmpty());
    }

    @Test
    void getPaymentFormConfigurationKO() {
        // create data
        PaymentFormConfigurationRequest request = Utils.createDefaultPaymentFormConfigurationRequestBuilder()
                .withPluginConfiguration("foo")
                .build();

        // call method
        PaymentFormConfigurationResponse formConfiguration = service.getPaymentFormConfiguration(request);

        // assertion
        Assertions.assertEquals(PaymentFormConfigurationResponseFailure.class, formConfiguration.getClass());
    }


    @Test
    void getOptionFromPluginConfiguration() {
        // call method
        List<SelectOption> options = service.getOptionFromPluginConfiguration(countries);

        // assertion
        Assertions.assertNotNull(options);
        Assertions.assertEquals(4, options.size());
    }

    @Test
    void getOptionFromPluginConfigurationnull() {
        Assertions.assertThrows(PluginException.class, () -> service.getOptionFromPluginConfiguration(null));
    }

    @Test
    void getOptionFromPluginConfigurationKO() {
        Assertions.assertThrows(PluginException.class, () -> service.getOptionFromPluginConfiguration("foo"));
    }



}