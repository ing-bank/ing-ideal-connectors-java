package com.ing.ideal.connector;

/**
 * Exception thrown by the {@link IdealConnector}.
 */
public class IdealException extends Exception {

    private String errorCode;
    private String errorMessage;
    private String errorDetail;
    private String consumerMessage;
    private String suggestedAction;
    private String suggestedExpirationPeriod;

    public IdealException(String errorCode) {
        this.errorCode = errorCode;
    }

    public IdealException(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        errorDetail = cause.getMessage();
    }

    public IdealException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public IdealException(String errorCode, String errorMessage, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        errorDetail = cause.getMessage();
    }

    public IdealException(String errorCode, String errorMessage,
                          String errorDetail, String consumerMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
        this.consumerMessage = consumerMessage;
    }

    public String getConsumerMessage() {
        return consumerMessage;
    }

    public void setConsumerMessage(String consumerMessage) {
        this.consumerMessage = consumerMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuggestedAction() {
        return suggestedAction;
    }

    public void setSuggestedAction(String suggestedAction) {
        this.suggestedAction = suggestedAction;
    }

    public String getSuggestedExpirationPeriod() {
        return suggestedExpirationPeriod;
    }

    public void setSuggestedExpirationPeriod(String suggestedExpirationPeriod) {
        this.suggestedExpirationPeriod = suggestedExpirationPeriod;
    }

    public String getMessage() {
        return errorCode + ": " + errorMessage
                + (suggestedAction == null ? "" : " (" + suggestedAction + ")");
    }
}