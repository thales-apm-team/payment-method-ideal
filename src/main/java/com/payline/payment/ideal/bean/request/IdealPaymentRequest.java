package com.payline.payment.ideal.bean.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.Issuer;
import com.payline.payment.ideal.bean.Merchant;
import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.utils.PluginUtils;
import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.payment.ideal.utils.constant.FormConfigurationKeys;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.PaymentRequest;

@JacksonXmlRootElement(localName = "AcquirerTrxReq", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
public class IdealPaymentRequest extends IdealBean {
    private static final String PT15M = "PT15M";


    @JacksonXmlProperty(localName = "Issuer")
    private Issuer issuer;

    @JacksonXmlProperty(localName = "Merchant")
    private Merchant merchant;

    @JacksonXmlProperty(localName = "Transaction")
    private Transaction transaction;


    public IdealPaymentRequest(PaymentRequest request) {
        // prepare needed data
        Amount amount = request.getAmount();
        ContractConfiguration configuration = request.getContractConfiguration();

        this.issuer = Issuer.builder()
                .issuerId(request.getPaymentFormContext().getPaymentFormParameter().get(FormConfigurationKeys.ISSUER_ID))
                .build();

        this.merchant = new Merchant(configuration.getProperty(ContractConfigurationKeys.MERCHANT_ID_KEY).getValue()
                , configuration.getProperty(ContractConfigurationKeys.MERCHANT_SUBID_KEY).getValue()
                , request.getEnvironment().getRedirectionReturnURL());

        this.transaction = Transaction.builder()
                .purchaseId(request.getOrder().getReference())
                .amount(PluginUtils.createStringAmount(amount.getAmountInSmallestUnit(), amount.getCurrency()))
                .currency(amount.getCurrency().getCurrencyCode())
                .expirationPeriod(PT15M)
                .language(request.getLocale().getLanguage())
                .build();
    }

}
