package com.ing.ideal.connector;

import com.ing.ideal.connector.util.ExceptionHelper;
import com.ing.ideal.connector.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;

public class Config {

    private Properties properties = null;
    /**
     * Id of the webshop, received by the acceptant via the iDEAL Dashboard.   (required)
     */
    protected static final String MERCHANT_ID = "merchantId";
    /**
     * URL of the webshop page where the consumer will be redirected after the iDEAL transaction.    (required)
     */
    protected static final String MERCHANT_RETURN_URL = "merchantReturnURL";
    /**
     * Alias of the certificate created by the acceptant.    (required)
     */
    public static final String PRIVATE_CERT = "privateCert";
    /**
     * Alias of the certificate created by the acquirer (by default the value for this is "iDEAL").    (required)
     */
    public static final String PUBLIC_CERT = "publicCert";
    /**
     * Truststore File with the acquirer public key used for verifying acquirer responses.   (required)
     */
    public static final String ACQUIRER_KEY_STORE_FILENAME = "acquirerKeyStoreFilename";
    /**
     * Truststore Password with the acquirer public key used for verifying acquirer responses.   (required)
     * Must be encrypted using the PasswordUtility encryption tool!
     */
    public static final String ACQUIRER_KEY_STORE_PASSWORD = "acquirerKeyStorePassword";
    /**
     * Keystore File with the merchant public key used for encoding merchant requests responses.      (required)
     */
    public static final String MERCHANT_KEY_STORE_FILENAME = "merchantKeyStoreFilename";
    /**
     * Keystore Password with the merchant public key used for encoding merchant requests responses.     (required)
     * Must be encrypted using the PasswordUtility encryption tool!
     */
    public static final String MERCHANT_KEY_STORE_PASSWORD = "merchantKeyStorePassword";
    /**
     * URL of the acceptants acquirer.    (required)
     */
    protected static final String ACQUIRER_URL = "acquirerURL";
    //optional
    /**
     * Sub Id of the webshop, change only with consent from the acquirer. (optional)
     * Default value: 0 (zero)
     */
    protected static final String MERCHANT_SUB_ID = "merchantSubId";
    /**
     * URL of the acceptants acquirer for transaction request.
     */
    protected static final String ACQUIRER_TRANSACTION_URL = "acquirerTransactionURL";
    /**
     * URL of the acceptants acquirer for transaction status request.
     */
    protected static final String ACQUIRER_TRANSACTION_STATUS_URL = "acquirerTransactionStatusURL";
    /**
     * URL of the acceptants acquirer for directory request.
     */
    protected static final String ACQUIRER_DIRECTORY_URL = "acquirerDirectoryURL";
    /**
     * Http proxy host, in case you are using a proxy.
     */
    protected static final String HTTP_PROXY_HOST = "httpProxyHost";
    /**
     * Http proxy port, in case you are using a proxy.
     */
    protected static final String HTTP_PROXY_PORT = "httpProxyPort";
    /**
     * Http proxy username, in case you are using a proxy and it has authentication enabled.
     */
    protected static final String HTTP_PROXY_USER = "httpProxyUser";
    /**
     * Http proxy password, in case you are using a proxy and it has authentication enabled.
     * Must be encrypted using the PasswordUtility encryption tool!
     */
    protected static final String HTTP_PROXY_PASS = "httpProxyPass";
    /**
     * Connection timeout in seconds.
     */
    protected static final String ACQUIRER_TIMEOUT = "acquirerTimeout";
    /**
     * Set the SSL protocol factory mode, allowed values are:
     * ignorance      = Do not validate certificates.
     * relaxed        = Just validate trusted certificates (self signed).
     * strict         = Also validate Hostnames [default] (strongly advised - anti man in the middle).
     */
    public static final String SSL_PROTOCOL_MODE = "SSLProtocolMode";
    /**
     * Transaction expiration time.
     * Default:If not set the issuer will use the default value of PT1H (one hour)
     */
    public static final String DEFAULT_EXPIRATION_PERIOD = "expirationPeriod";

    /**
     * Flag that indicates if TLS 1.2 is enabled.
     * Default: If not set, default value is false. It will be used TLS1.0
     */
    public static final String ENABLE_TLS_v12 = "enableTLSv12";

    public Config() {
    }

    private static final HashSet<String> REQUIRED_CONFIG = new HashSet<String>(Arrays.asList(
            MERCHANT_ID, MERCHANT_RETURN_URL,
            MERCHANT_KEY_STORE_FILENAME, MERCHANT_KEY_STORE_PASSWORD, PRIVATE_CERT,
            ACQUIRER_KEY_STORE_FILENAME, ACQUIRER_KEY_STORE_PASSWORD, PUBLIC_CERT, ACQUIRER_URL));

    public void init(String config) throws IdealException {
        try {
            properties = loadProperties(config);
        } catch (Exception e) {
            IdealException ex = ExceptionHelper.createException(ErrorCodes.IMEXCO01, e);
            Util.log.fatal(ex.getMessage(), e);
            throw ex;
        }
        checkMandatoryProperties(properties);
    }

    private synchronized Properties loadProperties(String config) throws IdealException {
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Initialising Config");
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Loading merchant configuration");
        try {
            Properties properties = new Properties();
            InputStream inStream = Config.class.getResourceAsStream(config);
            properties.load(inStream);
            IOUtils.closeQuietly(inStream);
            return properties;
        } catch (Exception e) {
            IdealException ex = ExceptionHelper.createException(ErrorCodes.IMEXCO01, e);
            Util.log.fatal(ex.getMessage(), e);
            throw ex;
        }
    }

    private void checkMandatoryProperties(Properties properties) throws IdealException {
        LinkedList<String> missingConfig = new LinkedList<String>();
        for (String key : REQUIRED_CONFIG) {
            if (properties.get(key) == null) {
                missingConfig.add(key);
            }
        }
        if (!missingConfig.isEmpty()) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXCO03, missingConfig);
        }
    }

    private Properties getProperties() throws IdealException {
        if (properties == null) {
            throw new IdealException(ErrorCodes.IMEXCO00, "Config has not yet been initialised.");
        }
        return properties;
    }

    /**
     * Gets the value for the given key.
     *
     * @param key configuration key
     * @return value for key, {@code null} if no value found
     * @throws IdealException
     */
    public String get(String key) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> get(String) :: String");
        if (Util.log.isTraceEnabled())
            Util.log.trace("   key: " + key);
        String value = null;
        if (StringUtils.trimToNull(key) != null) {
            value = getProperties().getProperty(key);
        }
        if (Util.log.isTraceEnabled())
            Util.log.trace("   value: " + value);
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< get(String) :: String");
        return value;
    }
}