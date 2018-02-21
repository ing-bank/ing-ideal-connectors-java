package com.ing.ideal.connector;

import com.ing.ideal.connector.crypto.CryptoHelper;
import com.ing.ideal.connector.crypto.IDigitalSignatureHelper;
import com.ing.ideal.connector.crypto.KeyStoreItem;
import com.ing.ideal.connector.serializer.ISerializer;
import com.ing.ideal.connector.serializer.JAXBSerializer;
import com.ing.ideal.connector.util.Util;
import org.apache.commons.lang3.StringUtils;

/**
 * Main point which assures the communication with the acquirer.</ br> For using
 * this library it is required to read the following documents:
 * <ol>
 * <li>Acquirer-Issuer messages v3.3.1 - 1.0.pdf</li>
 * <li>iDEAL_Advanced_Java_EN_v2.3.pdf</li>
 * </ol>
 *
 *
 * @author Codrin
 */
public class IdealConnector {
    /**
     * Default config bundle to be used.
     */
    private static final String CONFIG_BUNDLE = "/config.properties";
    /**
     * HttpClient Helper.
     */
    private HttpClientHelper httpClientHelper;
    /**
     * Signature Helper.
     */
    private IDigitalSignatureHelper signatureHelper;
    /**
     * Request serializer.
     */
    private ISerializer serializer;

    private Config config;

    public Config GetConfiguration()
    {
        return config;
    }

    /**
     * Instantiates the connector. Requires the config.proprietes (properly
     * configured) to be placed in the classpath root.
     *
     * @throws IdealException
     */
    public IdealConnector() throws IdealException {
        this(CONFIG_BUNDLE);
    }

    /**
     * Instantiates the connector
     *
     * @param configuration path relative to the classpath root for the config file, ex: <i>/config.properties</i>
     * @throws IdealException
     */
    public IdealConnector(String configuration) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> new IdealConnector(String)");

        config = new Config();
        config.init(configuration);

        KeyStoreItem merchantKS = CryptoHelper.createKeyStoreItem(config.get(Config.MERCHANT_KEY_STORE_FILENAME), config.get(Config.MERCHANT_KEY_STORE_PASSWORD), config.get(Config.PRIVATE_CERT));
        KeyStoreItem acquirerKS = CryptoHelper.createKeyStoreItem(config.get(Config.ACQUIRER_KEY_STORE_FILENAME), config.get(Config.ACQUIRER_KEY_STORE_PASSWORD), config.get(Config.PUBLIC_CERT));

        signatureHelper = new CryptoHelper(merchantKS, acquirerKS);
        serializer = new JAXBSerializer();
        httpClientHelper = new HttpClientHelper();

        if (Util.log.isTraceEnabled())
            Util.log.trace("<< new IdealConnector(String)");
    }

    /**
     * Gets the issuer list which work with your acquirer.
     *
     * @return the issuer list organized by countries.
     * @throws IdealException
     */
    public Issuers getIssuerList() throws IdealException {
        long startTime = -1L;
        if (Util.log.isTraceEnabled()) {
            Util.log.trace(">> getIssuerList() :: Issuers");
            startTime = System.currentTimeMillis();
        }
        Issuers issuerList = null;
        Util.log.debug("> Creating request message");
        String requestMessage = InterfaceAcceptantAcquirer_v331.createDirectoryRequest(config, signatureHelper, serializer);
        if (Util.log.isTraceEnabled())
            Util.log.trace("  Request message: " + requestMessage);
        if (Util.log.isDebugEnabled())
            Util.log.debug(">  Validating request message");
        InterfaceAcceptantAcquirer_v331.validateRequest(requestMessage);
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Sending request/Receiving response.");
        String responseMessage = httpClientHelper.sendHttpRequest(config, requestMessage, getDirectoryReqUrl(config));
        if (Util.log.isTraceEnabled())
            Util.log.trace("  Response message: " + responseMessage);
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Validating response message");
        InterfaceAcceptantAcquirer_v331.validateResponse(responseMessage);
        if (!signatureHelper.verifyDigitalSignedMessage(responseMessage)) {
            throw new IdealException("The response from server is not well signed...");
        }

        InterfaceAcceptantAcquirer_v331.checkResponseForError(serializer, responseMessage);
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Unmarshalling response message to Issuers object");

        issuerList = InterfaceAcceptantAcquirer_v331.parseDirectoryResponse(serializer, responseMessage);
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< getIssuerList() :: Issuers (" + (System.currentTimeMillis() - startTime) + "ms)");
        return issuerList;
    }

    /**
     * Request a transaction for a specific issuer.
     *
     * @param transaction - which must have (at least) set the following:
     *                    <ul>
     *                    <li>
     *                    Issuer ID - identifies the issuer, obtained via {@link #getIssuerList()} method</li>
     *                    <li>
     *                    Purchase ID - which is associated with the transaction</li>
     *                    <li>
     *                    Amount - the money which the issuer should pay</li>
     *                    <li>
     *                    Description
     *                    </li>
     *                    <li>
     *                    Entrance Code - works as an „authentication identifier‟ created by the Merchant to facilitate continuation of the
     *                    session between a Merchant and a Consumer, even if the existing session has been lost. See the Merchant-Acquirer
     *                    Messages v.3.3.1 document for details.</li>
     *                    <li>
     *                    Merchant Return URL - represents where the user will be redirected after the transaction has been
     *                    successfully processed.
     *                    </li>
     *                    </ul>
     * @return the returned object has set (at least) the following attributes:
     *         <p/>
     *         <ul>
     *         <li>Acquirer ID</li>
     *         <li>Transaction ID - which can be used in order to verify the transaction's status</li>
     *         <li>Issuer Authentication URL - represents the url where the user
     *         needs to be redirected in order to make the payment.</li>
     *         <ul>
     * @throws IdealException
     */
    public Transaction requestTransaction(Transaction transaction) throws IdealException {
        long startTime = -1L;
        if (Util.log.isTraceEnabled()) {
            Util.log.trace(">> requestTransaction(Transaction) :: Transaction");
            startTime = System.currentTimeMillis();
        }
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Marshalling request message");
        String requestMessage = InterfaceAcceptantAcquirer_v331.createTransactionRequest(config, signatureHelper, serializer, transaction);
        if (Util.log.isTraceEnabled())
            Util.log.trace("> Request message: " + requestMessage);
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Validating request message");
        InterfaceAcceptantAcquirer_v331.validateRequest(requestMessage);
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Sending request/Receiving response.");
        String responseMessage = httpClientHelper.sendHttpRequest(config, requestMessage, getTransactionReqUrl(config));
        if (Util.log.isTraceEnabled())
            Util.log.trace("> Response message: " + responseMessage);
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Validating response message");
        InterfaceAcceptantAcquirer_v331.validateResponse(responseMessage);

        if (!signatureHelper.verifyDigitalSignedMessage(responseMessage)) {
            throw new IdealException(
                    "The response from server is not well signed...");
        }

        InterfaceAcceptantAcquirer_v331.checkResponseForError(serializer, responseMessage);

        Transaction trxResponse = InterfaceAcceptantAcquirer_v331.parseTransactionResponse(serializer, responseMessage);
        transaction.setAcquirerID(trxResponse.getAcquirerID());
        transaction.setTransactionID(trxResponse.getTransactionID());
        transaction.setPurchaseID(trxResponse.getPurchaseID());
        transaction.setIssuerAuthenticationURL(trxResponse.getIssuerAuthenticationURL());
        trxResponse.setTransactionCreateDateTimestamp(trxResponse.getTransactionCreateDateTimestamp());
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< requestTransaction(Transaction) :: Transaction (" + (System.currentTimeMillis() - startTime) + "ms)");
        return transaction;
    }

    /**
     * Requests a status for a transaction identified by a transactionId.
     *
     * @param transactionId transaction id
     * @return the transaction which has (at least) set the following attributes:
     *         <ul>
     *         <li>Acquirer ID
     *         </li>
     *         <li>Status
     *         </li>
     *         <li>ConsumerName
     *         </li>
     *         <li>ConsumerIBAN
     *         </li>
     *         <li>ConsumerBIC
     *         </li>
     *         <li>StatusDateTimestamp
     *         </li>
     *         </ul>
     * @throws IdealException
     */
    public Transaction requestTransactionStatus(String transactionId) throws IdealException {
        long startTime = -1L;
        if (Util.log.isTraceEnabled()) {
            Util.log.trace(">> requestTransactionStatus(String) :: Transaction");
            startTime = System.currentTimeMillis();
        }
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Marshalling request message");
        String requestMessage = InterfaceAcceptantAcquirer_v331
                .createTransactionStatusRequest(config, signatureHelper, serializer, transactionId);
        if (Util.log.isTraceEnabled())
            Util.log.trace("> Request message: " + requestMessage);
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Validating request message");
        InterfaceAcceptantAcquirer_v331.validateRequest(requestMessage);
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Sending request/Receiving response.");
        String responseMessage = httpClientHelper.sendHttpRequest(config, requestMessage, getTransactionStatusReqUrl(config));
        if (Util.log.isTraceEnabled())
            Util.log.trace("> Response message: " + responseMessage);
        if (Util.log.isDebugEnabled())
            Util.log.debug("> Validating response message");
        InterfaceAcceptantAcquirer_v331.validateResponse(responseMessage);

        if (!signatureHelper.verifyDigitalSignedMessage(responseMessage)) {
            throw new IdealException("The response from server is not well signed...");
        }

        InterfaceAcceptantAcquirer_v331.checkResponseForError(serializer, responseMessage);

        Transaction trxResponse = InterfaceAcceptantAcquirer_v331.parseTransactionStatusResponse(serializer, responseMessage);
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< requestTransactionStatus(String) :: Transaction ("
                    + (System.currentTimeMillis() - startTime) + "ms)");
        return trxResponse;
    }

    /**
     * Computes the default target acquirer URL if the value passed is null or empty.
     *
     * @param optionalTargetUrl optionalTargetUrl
     * @return optionalTargetUrl or default target acquirer URL if the value passed is null or empty
     * @throws IdealException
     */
    private String getDefaultTargetUrlIfFollowingNotSet(Config config, String optionalTargetUrl) throws IdealException {
        if (StringUtils.trimToNull(optionalTargetUrl) != null) {
            return optionalTargetUrl;
        }
        // the key acquirer url is mandatory.
        return config.get(Config.ACQUIRER_URL);
    }

    /**
     * @return url for directory request
     * @throws IdealException
     */
    private String getDirectoryReqUrl(Config config) throws IdealException {
        return getDefaultTargetUrlIfFollowingNotSet(config, config.get(Config.ACQUIRER_DIRECTORY_URL));
    }

    /**
     * @return url for the transaction request
     * @throws IdealException
     */
    private String getTransactionReqUrl(Config config) throws IdealException {
        return getDefaultTargetUrlIfFollowingNotSet(config, config.get(Config.ACQUIRER_TRANSACTION_URL));
    }

    /**
     * @return url for transaction status request
     * @throws IdealException
     */
    private String getTransactionStatusReqUrl(Config config) throws IdealException {
        return getDefaultTargetUrlIfFollowingNotSet(config, config.get(Config.ACQUIRER_TRANSACTION_STATUS_URL));
    }
}