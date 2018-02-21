package com.ing.ideal.connector.util;

import com.ing.ideal.connector.Config;
import com.ing.ideal.connector.ErrorCodes;
import com.ing.ideal.connector.IdealException;
import com.ing.ideal.connector.Transaction;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements validations that are applied on interface parameters.
 */
public class ValidationHelper {
    public static final String INTERFACE_ALLOWED_CHARS = "^[-A-Za-z0-9= %*+,./&@\"':;?()$]*$";
    public static final String INTERFACE_NUMERIC_CHARS = "^[0-9]*$";
    public static final String INTERFACE_NUMERIC_CHARS_WITH_PERIOD = "^[0-9]*.[0-9]*$";
    public static final String INTERFACE_ISO8601_PERIOD = "^(P([0-9]+Y)?([0-9]+M)?([0-9]+D)?)?(T([0-9]+H)?([0-9]+M)?([0-9]+S)?)?$";
    public static final String INTERFACE_ISO8601_PERIOD_MINMAX = "^PT(([0-9]+H)?)(([0-9]+M)?)(([0-9]+S)?)$";

    private static final Pattern REGEXP_ALLOWED_CHARS = Pattern.compile(INTERFACE_ALLOWED_CHARS);
    private static final Pattern REGEXP_NUMERIC_CHARS_WITH_PERIOD = Pattern.compile(INTERFACE_NUMERIC_CHARS_WITH_PERIOD);
    private static final Pattern REGEXP_NUMERIC_CHARS = Pattern.compile(INTERFACE_NUMERIC_CHARS);
    private static final Pattern REGEXP_ISO8601_PERIOD = Pattern.compile(INTERFACE_ISO8601_PERIOD);
    private static final Pattern REGEXP_ISO8601_PERIOD_MINMAX = Pattern.compile(INTERFACE_ISO8601_PERIOD_MINMAX);

    /**
     * Validates that the given string is not empty.
     *
     * @param value         string value
     * @param attributeName attribute name to be used in exception
     * @throws IdealException
     */
    public static void validateNotEmpty(String value, String attributeName) throws IdealException {
        if (StringUtils.trimToNull(value) == null) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXCO07, attributeName);
        }
    }

    /**
     * Validates that the string contains only allowed characters.
     *
     * @param string        string to be tested
     * @param attributeName attribute name tobe used in error message
     * @throws IdealException
     */
    public static void validateString(String string, String attributeName) throws IdealException {
        validateNotEmpty(string, attributeName);
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> validateString(String) :: boolean");
        boolean match = REGEXP_ALLOWED_CHARS.matcher(string).matches();
        if (Util.log.isTraceEnabled())
            Util.log.trace("   isValid: " + match);
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< validateString(String) :: boolean");
        if (!match) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXCO06, attributeName);
        }
    }

    /**
     * Validates that the value represents an integer value.
     *
     * @param value         string representing number
     * @param attributeName attribute name to be used in exception
     * @throws IdealException
     */
    public static void validateNumber(String value, String attributeName) throws IdealException {
        validateNotEmpty(value, attributeName);
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> validateNumberWithPeriod(String) :: boolean");
        boolean match = REGEXP_NUMERIC_CHARS.matcher(value).matches();
        if (Util.log.isTraceEnabled())
            Util.log.trace("   isValid: " + match);
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< validateNumberWithPeriod(String) :: boolean");
        if (!match) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXCO16, attributeName);
        }
    }

    /**
     * Validates that the number is represent an integer value.
     *
     * @param value         string representing number
     * @param attributeName attribute name to be used in exception
     * @throws IdealException
     */
    public static void validateNumberWithPeriod(String value, String attributeName) throws IdealException {
        validateNotEmpty(value, attributeName);
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> validateNumberWithPeriod(String) :: boolean");
        boolean match = REGEXP_NUMERIC_CHARS_WITH_PERIOD.matcher(value)
                .matches();
        if (Util.log.isTraceEnabled())
            Util.log.trace("   isValid: " + match);
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< validateNumberWithPeriod(String) :: boolean");
        if (!match) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXCO09, attributeName);
        }
    }

    /**
     * Validates the merchant id.
     * value must be PN..9  - padded number,9 digits
     *
     * @param id            id
     * @param attributeName attributeName
     * @throws IdealException
     */
    public static void validateMerchantID(String id, String attributeName) throws IdealException {
        validateNotEmpty(id, attributeName);
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> validateMerchantID(String) :: boolean");
        boolean match = StringUtils.isNumeric(id) && id.length() == 9;
        if (Util.log.isTraceEnabled())
            Util.log.trace("   isValid: " + match);
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< validateMerchantSubID(String) :: boolean");
        if (!match) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXCO15);
        }
    }

    /**
     * Validates merchant sub id.
     * value must be N..max 6   , numeric, max 6 digits
     *
     * @param subID         merchantSubID
     * @param attributeName attributeName
     * @throws IdealException
     */
    public static void validateMerchantSubID(String subID, String attributeName) throws IdealException {
        validateNotEmpty(subID, attributeName);
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> validateMerchantSubID(String) :: boolean");
        boolean match = StringUtils.isNumeric(subID) && subID.length() <= 6;
        if (Util.log.isTraceEnabled())
            Util.log.trace("   isValid: " + match);
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< validateMerchantSubID(String) :: boolean");
        if (!match) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXCO14, attributeName);
        }
    }

    /**
     * Validates that the given period is a valid ISO period.
     *
     * @param period           period
     * @param minMaxValidation validate min-max values
     * @param attributeName    attributeName
     * @throws IdealException
     */
    public static void validatePeriod(String period, boolean minMaxValidation, String attributeName) throws IdealException {
        validateNotEmpty(period, attributeName);
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> validatePeriod(String) :: boolean");

        Matcher matcher = REGEXP_ISO8601_PERIOD.matcher(period);
        boolean matches = matcher.matches();
        if (matches && period.contains("T") && !period.contains("H") && !period.contains("M") && !period.contains("S")) {
            matches = false;
        }
        if (matches && minMaxValidation) {
            Matcher matcher2 = REGEXP_ISO8601_PERIOD_MINMAX.matcher(period);
            boolean found = matcher2.find();
            if (found) {
                long totalSeconds = 0L;
                String hours = matcher2.group(1);
                if (!"".equals(hours)) {
                    hours = hours.replaceAll("H", "");
                    long hoursNum = Long.valueOf(hours);
                    totalSeconds += hoursNum * 60L * 60L;
                }
                String minutes = matcher2.group(3);
                if (!"".equals(minutes)) {
                    minutes = minutes.replaceAll("M", "");
                    long minutesNum = Long.valueOf(minutes);
                    totalSeconds += minutesNum * 60L;
                }
                String seconds = matcher2.group(5);
                if (!"".equals(seconds)) {
                    seconds = seconds.replaceAll("S", "");
                    long secondsNum = Long.valueOf(seconds);
                    totalSeconds += secondsNum;
                }
                if (totalSeconds > 3600L || totalSeconds < 60L)
                    matches = false;
            } else {
                IdealException ex = ExceptionHelper.createException(ErrorCodes.IMEXCO13, attributeName);
                ex.setErrorDetail("The value exceeds the minimum/maximum bounds (60-3600 seconds)");
                throw ex;
            }
        }
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< validatePeriod(String) :: boolean");
        if (!matches) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXCO12, attributeName);
        }
    }

    /**
     * Not necessarily begin with http or https.
     *
     * @param value uri
     * @throws com.ing.ideal.connector.IdealException
     *
     */
    public static void validateURL(String value, String attributeName) throws IdealException {
        validateNotEmpty(value, attributeName);
        try {

            URI uri = new URI(value);

            //lines of code removed in order to accept custom protocols other than http or https
            if (uri.getScheme() == null) {
                uri = new URI("http://" + value);
            }

            //uri.toURL();

        } catch (Exception e) {
            if (Util.log.isDebugEnabled())
                Util.log.debug(Strings.getString(ErrorCodes.IMEXCO10, value));

            throw ExceptionHelper.createException(ErrorCodes.IMEXCO10, attributeName);
        }
    }

    /**
     * Validates parameters used in a Directory request.
     *
     * @param merchantId    merchantId
     * @param merchantSubId merchantSubId
     * @param privateCert   privateCert
     * @throws IdealException
     */
    public static void validateDirectoryRequestParams(String merchantId, String merchantSubId, String privateCert) throws IdealException {
        validateMerchantID(merchantId, Parameters.MERCHANT_ID);
        validateMerchantSubID(merchantSubId, Parameters.MERCHANT_SUB_ID);
        validateNotEmpty(privateCert, Parameters.PRIVATE_CERT);
    }

    /**
     * Validates parameters used in a transaction request.
     *
     * @param transaction   transaction
     * @param merchantId    merchantId
     * @param merchantSubId merchantSubId
     * @param privateCert   privateCert
     * @throws IdealException
     */
    public static void validateTransactionRequestParams(Transaction transaction, String merchantId, String merchantSubId, String privateCert) throws IdealException {
        validateMerchantID(merchantId, Parameters.MERCHANT_ID);
        validateMerchantSubID(merchantSubId, Parameters.MERCHANT_SUB_ID);
        validateNotEmpty(privateCert, Parameters.PRIVATE_CERT);
        validateURL(transaction.getMerchantReturnURL(), Parameters.MERCHANT_RETURN_URL);
        validateString(transaction.getCurrency(), Parameters.KEY_CURRENCY);
        validateString(transaction.getLanguage(), Parameters.KEY_LANGUAGE);
        validateString(transaction.getIssuerID(), Parameters.KEY_ISSUER_ID);
        validateNumberWithPeriod(transaction.getAmount().toPlainString(), Parameters.KEY_AMOUNT);
        validateString(transaction.getPurchaseID(), Parameters.KEY_PURCHASE_ID);
        validateString(transaction.getDescription(), Parameters.KEY_DESCRIPTION);
        validateString(transaction.getEntranceCode(), Parameters.KEY_ENTRANCE_CODE);
        if (transaction.getExpirationPeriod() != null) {
            validatePeriod(transaction.getExpirationPeriod(), true, Config.DEFAULT_EXPIRATION_PERIOD);
        }
    }

    /**
     * Validates parameters used in a transaction status request.
     *
     * @param transactionId transactionId
     * @param merchantId    merchantId
     * @param merchantSubId merchantSubId
     * @param privateCert   privateCert
     * @throws IdealException
     */
    public static void validateTransactionStatusParams(String transactionId, String merchantId, String merchantSubId, String privateCert) throws IdealException {
        validateMerchantID(merchantId, Parameters.MERCHANT_ID);
        validateMerchantSubID(merchantSubId, Parameters.MERCHANT_SUB_ID);
        validateNotEmpty(privateCert, Parameters.PRIVATE_CERT);
        validateNumber(transactionId, Parameters.KEY_TRANSACTION_ID);
    }
}
