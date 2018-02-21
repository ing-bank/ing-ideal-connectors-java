package com.ing.ideal.connector;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.w3._2000._09.xmldsig.SignatureType;

import java.math.BigDecimal;

public class Transaction {
    /**
     * Open transaction status.
     */
    public static final String OPEN = "Open";
    /**
     * Success transaction status.
     */
    public static final String SUCCESS = "Success";
    /**
     * Cancelled transaction status.
     */
    public static final String CANCELLED = "Cancelled";
    /**
     * Expired transaction status.
     */
    public static final String EXPIRED = "Expired";
    /**
     * Failure transaction status.
     */
    public static final String FAILURE = "Failure";

    /**
     * Unique identification of the transaction as issued by the acquirer. It is recommended to link this number to your
     * own purchase number (purchaseId) to support your own accounting system.
     */
    private String transactionID;
    /**
     * the status of the transaction.
     */
    private String status;
    /**
     * The ID of the acquirer.
     */
    private String acquirerID;
    /**
     * The ID of the issuer the consumer has selected from the pick list.
     */
    private String issuerID;
    /**
     * The Full Url Of The Issuer (the Consumer’s Bank). The Webshop Should Redirect The Consumer Automatically To This Url.
     */
    private String issuerAuthenticationURL;
    /**
     * The purchase number according to the webshop’s system.
     */
    private String purchaseID;
    /**
     * The amount of the transaction.
     */
    private BigDecimal amount;
    /**
     * The description of the product.
     */
    private String description;
    /**
     * A code determined by the webshop with which the purchase can be authenticated upon redirection to the webshop.
     */
    private String entranceCode;
    /**
     * The consumer name of the transaction.
     */
    private String consumerName;
    /**
     * Expiration period. (Optional, if different from the configured value).
     */
    private String expirationPeriod;
    /**
     * The transaction currency. Default currency is EUR (but also it is the only accepted)
     */
    private String currency;
    /**
     * The transaction language. Default language (if nothing specified) is "NL".
     */
    private String language;
    /**
     * URL to return to merchant.(Optional, if different from the configured value.)
     */
    private String merchantReturnURL;
    /**
     * Transaction creation date.
     */
    private String transactionCreateDateTimestamp;
    /**
     * The consumer IBAN of the transaction.
     */
    private String consumerIBAN;
    /**
     * the consumerBIC of the transaction.
     */
    private String consumerBIC;
    /**
     * The timestamp of the transaction status.
     */
    private String statusDateTimestamp;
    /**
     * Response signature.
     */
    private SignatureType signature;

    public Transaction() {
        status = OPEN;
    }

    /**
     * @return The ID of the acquirer
     */
    public String getAcquirerID() {
        return acquirerID;
    }

    protected void setAcquirerID(String acquirerID) {
        this.acquirerID = acquirerID;
    }

    /**
     * @return amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the transaction.
     *
     * @param amount amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the consumer name of the transaction
     */
    public String getConsumerName() {
        return consumerName;
    }

    /**
     * Sets the consumer name.
     *
     * @param consumerName consumer Name
     */
    protected void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    /**
     * @return currency, if set
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the transaction currency. Default currency is EUR (but also it is the only accepted).
     *
     * @param currency currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return The description of the product
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the product.
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return A code determined by the webshop with which the purchase can be authenticated upon redirection to the webshop
     */
    public String getEntranceCode() {
        return entranceCode;
    }

    /**
     * Sets  A code determined by the webshop with which the purchase can be authenticated upon redirection to the webshop
     *
     * @param entranceCode entrance Code
     */
    public void setEntranceCode(String entranceCode) {
        this.entranceCode = entranceCode;
    }

    /**
     * @return expiration period in seconds
     */
    public String getExpirationPeriod() {
        return expirationPeriod;
    }

    /**
     * Sets the expiration period. (Optional, if different from the configured value).
     *
     * @param expirationPeriod expiration period in seconds
     */
    public void setExpirationPeriod(String expirationPeriod) {
        this.expirationPeriod = expirationPeriod;
    }

    /**
     * Gets the value of the transaction id.
     *
     * @return Unique identification of the transaction as issued by the acquirer. It is recommended to link this number to your
     *         own purchase number (purchaseId) to support your own accounting system.
     */
    public String getTransactionID() {
        return transactionID;
    }

    protected void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    /**
     * @return The Full Url Of The Issuer (the Consumer’s Bank). The Webshop Should Redirect The Consumer Automatically To This Url.
     */
    public String getIssuerAuthenticationURL() {
        return issuerAuthenticationURL;
    }

    protected void setIssuerAuthenticationURL(String issuerAuthenticationURL) {
        this.issuerAuthenticationURL = issuerAuthenticationURL;
    }

    /**
     * @return The ID of the issuer the consumer has selected from the pick list.
     */
    public String getIssuerID() {
        return issuerID;
    }

    /**
     * @param issuerID the ID of the issuer the consumer has selected from the pick list
     */
    public void setIssuerID(String issuerID) {
        this.issuerID = issuerID;
    }

    /**
     * @return the transaction language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the transaction language. Default language (if nothing specified) is "NL".
     *
     * @param language language
     */
    protected void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the purchase number according to the webshop’s system
     */
    public String getPurchaseID() {
        return purchaseID;
    }

    /**
     * Sets the purchase number according to the webshop’s system.
     *
     * @param purchaseID purchase ID
     */
    public void setPurchaseID(String purchaseID) {
        this.purchaseID = purchaseID;
    }

    /**
     * @return the status of the transaction
     */
    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return URL to return to merchant
     */
    public String getMerchantReturnURL() {
        return merchantReturnURL;
    }

    /**
     * Sets the URL to return to merchant.(Optional, if different from the configured value.)
     *
     * @param merchantReturnURL URL to return to merchant
     */
    public void setMerchantReturnURL(String merchantReturnURL) {
        this.merchantReturnURL = merchantReturnURL;
    }

    /**
     * @return true if the transaction status is OPEN
     */
    public boolean isOpen() {
        return OPEN.equals(status);
    }

    /**
     * @return true if the transaction status is SUCCESS
     */
    public boolean isSuccess() {
        return SUCCESS.equals(status);
    }

    /**
     * @return true if the transaction status is CANCELLED
     */
    public boolean isCancelled() {
        return CANCELLED.equals(status);
    }

    /**
     * @return true if the transaction status is EXPIRED
     */
    public boolean isExpired() {
        return EXPIRED.equals(status);
    }

    /**
     * @return true if the transaction status is FAILURE
     */
    public boolean isFailure() {
        return FAILURE.equals(status);
    }

    /**
     * @return transaction creation date
     */
    public String getTransactionCreateDateTimestamp() {
        return transactionCreateDateTimestamp;
    }

    /**
     * @param transactionCreateDateTimestamp the transactionCreateDateTimestamp to set
     */
    public void setTransactionCreateDateTimestamp(
            String transactionCreateDateTimestamp) {
        this.transactionCreateDateTimestamp = transactionCreateDateTimestamp;
    }

    /**
     * @return the consumerIBAN
     */
    public String getConsumerIBAN() {
        return consumerIBAN;
    }

    /**
     * @param consumerIBAN the consumerIBAN to set
     */
    public void setConsumerIBAN(String consumerIBAN) {
        this.consumerIBAN = consumerIBAN;
    }

    /**
     * @return the consumerBIC
     */
    public String getConsumerBIC() {
        return consumerBIC;
    }

    /**
     * @param consumerBIC the consumerBIC to set
     */
    public void setConsumerBIC(String consumerBIC) {
        this.consumerBIC = consumerBIC;
    }

    /**
     * @return the statusDateTimestamp
     */
    public String getStatusDateTimestamp() {
        return statusDateTimestamp;
    }

    /**
     * @param statusDateTimestamp the statusDateTimestamp to set
     */
    public void setStatusDateTimestamp(String statusDateTimestamp) {
        this.statusDateTimestamp = statusDateTimestamp;
    }

    /**
     * Sets the signature for
     * @param signature response signature
     */
    protected void setSignature(SignatureType signature) {
        this.signature = signature;
    }

    /**
     * Gets the response signature.
     * @return response
     *
     */
    public SignatureType getSignature() {
        return this.signature;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return new EqualsBuilder()
                .append(acquirerID, that.acquirerID)
                .append(amount, that.amount)
                .append(consumerBIC, that.consumerBIC)
                .append(consumerIBAN, that.consumerIBAN)
                .append(consumerName, that.consumerName)
                .append(currency, that.currency)
                .append(description, that.description)
                .append(entranceCode, that.entranceCode)
                .append(expirationPeriod, that.expirationPeriod)
                .append(issuerAuthenticationURL, that.issuerAuthenticationURL)
                .append(issuerID, that.issuerID)
                .append(language, that.language)
                .append(merchantReturnURL, that.merchantReturnURL)
                .append(purchaseID, that.purchaseID)
                .append(status, that.status)
                .append(statusDateTimestamp, that.statusDateTimestamp)
                .append(transactionCreateDateTimestamp, that.transactionCreateDateTimestamp)
                .append(transactionID, that.transactionID)
                .append(signature, that.signature)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(transactionID)
                .append(status)
                .append(acquirerID)
                .append(issuerID)
                .append(issuerAuthenticationURL)
                .append(purchaseID)
                .append(amount)
                .append(description)
                .append(entranceCode)
                .append(consumerName)
                .append(expirationPeriod)
                .append(currency)
                .append(language)
                .append(merchantReturnURL)
                .append(transactionCreateDateTimestamp)
                .append(consumerIBAN)
                .append(consumerBIC)
                .append(statusDateTimestamp)
                .append(signature)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("transactionID", transactionID)
                .append("status", status)
                .append("acquirerID", acquirerID)
                .append("issuerID", issuerID)
                .append("issuerAuthenticationURL", issuerAuthenticationURL)
                .append("purchaseID", purchaseID)
                .append("amount", amount)
                .append("description", description)
                .append("entranceCode", entranceCode)
                .append("consumerName", consumerName)
                .append("expirationPeriod", expirationPeriod)
                .append("currency", currency)
                .append("language", language)
                .append("merchantReturnURL", merchantReturnURL)
                .append("transactionCreateDateTimestamp", transactionCreateDateTimestamp)
                .append("consumerIBAN", consumerIBAN)
                .append("consumerBIC", consumerBIC)
                .append("statusDateTimestamp", statusDateTimestamp)
                .append("signature",signature)
                .toString();
    }

}