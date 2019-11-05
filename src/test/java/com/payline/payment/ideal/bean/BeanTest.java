package com.payline.payment.ideal.bean;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.bean.request.IdealDirectoryRequest;
import com.payline.payment.ideal.bean.request.IdealPaymentRequest;
import com.payline.payment.ideal.utils.XMLUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BeanTest {
    private XMLUtils xmlUtils = XMLUtils.getInstance();

    private String bean = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<DirectoryReq xmlns=\"http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1\" version=\"3.3.1\">" +
            "   <createDateTimestamp>2008-11-14T09:30:47.0Z</createDateTimestamp>" +
            "   <Merchant>" +
            "      <merchantID>100000001</merchantID>" +
            "      <subID>1</subID>" +
            "   </Merchant>" +
            "   <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "      <SignedInfo>" +
            "         <CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />" +
            "         <SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\" />" +
            "         <Reference URI=\"\">" +
            "            <Transforms>" +
            "               <Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" />" +
            "            </Transforms>" +
            "            <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\" />" +
            "            <DigestValue>I7JHyxH/KHcF3KM2xWGVMzSXVQ1MBnD9vInj1XWVNpw=</DigestValue>" +
            "         </Reference>" +
            "      </SignedInfo>" +
            "      <SignatureValue>oMve3wVdMgj[...]iHFuaPWCHGK11GrvtgKf+Kl4J6oNd4Jug=</SignatureValue>" +
            "      <KeyInfo>" +
            "         <KeyName>7D665C81A[...]6F07BF2</KeyName>" +
            "      </KeyInfo>" +
            "   </Signature>" +
            "</DirectoryReq>";

    @Test
    void beanTest() {
        IdealDirectoryRequest idealDirectoryRequest = new IdealDirectoryRequest(Utils.createContractParametersCheckRequest());
        System.out.println(xmlUtils.toXml(idealDirectoryRequest));

        IdealPaymentRequest idealPaymentRequest = new IdealPaymentRequest(Utils.createCompletePaymentBuilder().build());
        System.out.println(xmlUtils.toXml(idealPaymentRequest));

        IdealDirectoryRequest idealDirectoryRequest2 = xmlUtils.fromXML(bean, IdealDirectoryRequest.class);

        Assertions.assertNotNull(idealDirectoryRequest2.getMerchant());
        Assertions.assertNotNull(idealDirectoryRequest2.getMerchant());

    }
}
