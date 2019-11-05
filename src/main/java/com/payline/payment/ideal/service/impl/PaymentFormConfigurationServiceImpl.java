package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.bean.Country;
import com.payline.payment.ideal.bean.Directory;
import com.payline.payment.ideal.bean.Issuer;
import com.payline.payment.ideal.exception.InvalidDataException;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.IdealConstant;
import com.payline.payment.ideal.utils.PluginUtils;
import com.payline.payment.ideal.utils.XMLUtils;
import com.payline.payment.ideal.utils.i18n.I18nService;
import com.payline.payment.ideal.utils.propertiesFilesConstants.LogoConstants;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.bean.field.SelectOption;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentFormConfigurationService;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


public class PaymentFormConfigurationServiceImpl implements PaymentFormConfigurationService {
    private Logger LOGGER = LogManager.getLogger(PaymentFormConfigurationServiceImpl.class);
    private I18nService i18n = I18nService.getInstance();

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest request) {
        try {

            CustomForm customForm = BankTransferForm.builder()
                    .withBanks(getOptionFromPluginConfiguration(request.getPluginConfiguration()))
                    .withDisplayButton(true)
                    .withButtonText(i18n.getMessage(IdealConstant.FORM_BUTTON_IDEAL_TEXT, request.getLocale()))
                    .withDescription(i18n.getMessage(IdealConstant.FORM_BUTTON_IDEAL_DESCRIPTION, request.getLocale()))
                    .withCustomFields(new ArrayList<>())
                    .build();

            return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                    .aPaymentFormConfigurationResponseSpecific()
                    .withPaymentForm(customForm)
                    .build();
        } catch (PluginException e) {
            return e.toPaymentFormConfigurationResponseFailureBuilder()
                    .build();

        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder
                    .aPaymentFormConfigurationResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
    }

    List<SelectOption> getOptionFromPluginConfiguration(String configuration) throws InvalidDataException {
        if (configuration == null || PluginUtils.isEmpty(configuration)) {
            throw new InvalidDataException("plugin configuration can't be empty");

        }

        List<SelectOption> options = new ArrayList<>();

        Directory directory = XMLUtils.getInstance().fromXML(configuration, Directory.class);
        if (directory == null || directory.getCountries() == null) {
            throw new InvalidDataException("Unable to parse plugin configuration");

        }

        for (Country country : directory.getCountries()) {
            for (Issuer issuer : country.getIssuers()) {
                SelectOption option = SelectOption.SelectOptionBuilder
                        .aSelectOption()
                        .withKey(issuer.getIssuerId())
                        .withValue(issuer.getIssuerName())
                        .build();

                options.add(option);
            }
        }

        if (options.isEmpty()) {
            throw new InvalidDataException("Issuer list can't be empty");
        }

        return options;
    }

    @Override
    public PaymentFormLogoResponse getPaymentFormLogo(PaymentFormLogoRequest paymentFormLogoRequest) {
        Properties props = new Properties();
        try {
            props.load(ConfigurationServiceImpl.class.getClassLoader().getResourceAsStream(LogoConstants.LOGO_PROPERTIES));
            return PaymentFormLogoResponseFile.PaymentFormLogoResponseFileBuilder.aPaymentFormLogoResponseFile()
                    .withHeight(Integer.valueOf(props.getProperty(LogoConstants.LOGO_HEIGHT)))
                    .withWidth(Integer.valueOf(props.getProperty(LogoConstants.LOGO_WIDTH)))
                    .withTitle(i18n.getMessage(props.getProperty(LogoConstants.LOGO_TITLE), paymentFormLogoRequest.getLocale()))
                    .withAlt(i18n.getMessage(props.getProperty(LogoConstants.LOGO_ALT), paymentFormLogoRequest.getLocale()))
                    .build();
        } catch (IOException e) {
            LOGGER.error("An error occurred reading the file logo.properties", e);
            throw new RuntimeException("Failed to reading file logo.properties: ", e);

        }
    }

    @Override
    public PaymentFormLogo getLogo(String s, Locale locale) {
        Properties props = new Properties();
        try {
            props.load(ConfigurationServiceImpl.class.getClassLoader().getResourceAsStream(LogoConstants.LOGO_PROPERTIES));
        } catch (IOException e) {
            LOGGER.error("An error occurred reading the file logo.properties", e);
            throw new RuntimeException("Failed to reading file logo.properties: ", e);

        }
        String fileName = props.getProperty(LogoConstants.LOGO_FILE_NAME);
        try {
            // Read logo file
            InputStream input = PaymentFormConfigurationServiceImpl.class.getClassLoader().getResourceAsStream(fileName);
            BufferedImage logo = ImageIO.read(input);

            // Recover byte array from image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(logo, props.getProperty(LogoConstants.LOGO_FORMAT), baos);

            return PaymentFormLogo.PaymentFormLogoBuilder.aPaymentFormLogo()
                    .withFile(baos.toByteArray())
                    .withContentType(props.getProperty(LogoConstants.LOGO_CONTENT_TYPE))
                    .build();
        } catch (IOException e) {
            LOGGER.error("Unable to load the logo", e);
            throw new RuntimeException(e);
        }
    }

}