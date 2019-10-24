package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Transaction {
    @JacksonXmlProperty(localName = "transactionCreateDateTimestamp")
    private String transactionCreateDateTimestamp;

    @JacksonXmlProperty(localName = "transactionID")
    private String transactionId;
    @JacksonXmlProperty(localName = "purchaseID")
    private String purchaseId;
    @JacksonXmlProperty(localName = "amount")
    private String amount;
    @JacksonXmlProperty(localName = "currency")
    private String currency;
    @JacksonXmlProperty(localName = "expirationPeriod")
    private String expirationPeriod;
    @JacksonXmlProperty(localName = "language")
    private String language;
    @JacksonXmlProperty(localName = "description")
    private String description;
    @JacksonXmlProperty(localName = "entranceCode")
    private String entranceCode;

    @JacksonXmlProperty(localName = "status")
    private String status;

    @JacksonXmlProperty(localName = "statusDateTimestamp")
    private String statusDateTimestamp;

    @JacksonXmlProperty(localName = "consumerName")
    private String consumerName;

    @JacksonXmlProperty(localName = "consumerIBAN")
    private String consumerIBAN;

    @JacksonXmlProperty(localName = "consumerBIC")
    private String consumerBIC;

    /**
     * Empty public constructor needed by JacksonXML
     */
    public Transaction() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getExpirationPeriod() {
        return expirationPeriod;
    }

    public String getLanguage() {
        return language;
    }

    public String getDescription() {
        return description;
    }

    public String getEntranceCode() {
        return entranceCode;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusDateTimestamp() {
        return statusDateTimestamp;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public String getConsumerIBAN() {
        return consumerIBAN;
    }

    public String getConsumerBIC() {
        return consumerBIC;
    }

    private Transaction(TransactionBuilder builder) {
        this.transactionId = builder.transactionId;
        this.purchaseId = builder.purchaseId;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.expirationPeriod = builder.expirationPeriod;
        this.language = builder.language;
        this.description = builder.description;
        this.entranceCode = builder.entranceCode;

        this.status = builder.status;
        this.statusDateTimestamp = builder.statusDateTimestamp;
        this.consumerName = builder.consumerName;
        this.consumerIBAN = builder.consumerIBAN;
        this.consumerBIC = builder.consumerBIC;
    }


    public static class TransactionBuilder {
        private String transactionId;
        private String purchaseId;
        private String amount;
        private String currency;
        private String expirationPeriod;
        private String language;
        private String description;
        private String entranceCode;

        private String status;
        private String statusDateTimestamp;
        private String consumerName;
        private String consumerIBAN;
        private String consumerBIC;

        private TransactionBuilder() {
        }

        public static TransactionBuilder aTransaction() {
            return new TransactionBuilder();
        }

        public TransactionBuilder withTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionBuilder withPurchaseId(String purchaseId) {
            this.purchaseId = purchaseId;
            return this;
        }

        public TransactionBuilder withAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder withCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public TransactionBuilder withExpirationPeriod(String expirationPeriod) {
            this.expirationPeriod = expirationPeriod;
            return this;
        }

        public TransactionBuilder withLanguage(String language) {
            this.language = language;
            return this;
        }

        public TransactionBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public TransactionBuilder withEntranceCode(String entranceCode) {
            this.entranceCode = entranceCode;
            return this;
        }

        public TransactionBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public TransactionBuilder withStatusDateTimestamp(String statusDateTimestamp) {
            this.statusDateTimestamp = statusDateTimestamp;
            return this;
        }

        public TransactionBuilder withConsumerName(String consumerName) {
            this.consumerName = consumerName;
            return this;
        }

        public TransactionBuilder withConsumerIBAN(String consumerIBAN) {
            this.consumerIBAN = consumerIBAN;
            return this;
        }

        public TransactionBuilder withConsumerBIC(String consumerBIC) {
            this.consumerBIC = consumerBIC;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }

    }

    /**
     * All possible transaction status
     */
    public static class Status {
        public static final String OPEN = "OPEN";
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILURE = "FAILURE";
        public static final String CANCELLED = "CANCELLED";
        public static final String EXPIRED = "EXPIRED";
    }
}
