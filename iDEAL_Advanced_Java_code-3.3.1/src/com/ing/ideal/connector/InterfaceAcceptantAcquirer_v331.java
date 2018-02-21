package com.ing.ideal.connector;

import com.ing.ideal.connector.binding.*;
import com.ing.ideal.connector.crypto.IDigitalSignatureHelper;
import com.ing.ideal.connector.serializer.ISerializer;
import com.ing.ideal.connector.util.*;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.ing.ideal.connector.Constants.*;

// Referenced classes of package com.ing.ideal.connector:
//            Transaction, IdealException, Issuers, Issuer,
//            Util, Config

class InterfaceAcceptantAcquirer_v331 {
    public static final String INTERFACE_XML_SCHEMA = "InterfaceAcceptantAcquirer_v331.xsd";
    public static final String INTERFACE_XML_DSIG_SCHEMA = "dsigschema.xsd";
    public static final String INTERFACE_XML_SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";
    private static Validator validator;

    InterfaceAcceptantAcquirer_v331() {

    }

    protected static String createDirectoryRequest(Config config, IDigitalSignatureHelper signatureHelper, ISerializer serializer) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> createDirectoryRequest(String, SecurityHelper, String, String, String) :: String");
        String request = null;
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Retrieving required parameters");
        String merchantId = config.get(Config.MERCHANT_ID);
        String merchantSubId = config.get(Config.MERCHANT_SUB_ID);
        String privateCert = config.get(Config.PRIVATE_CERT);

        if (Util.log.isDebugEnabled())
            Util.log.debug("  Checking mandatory attributes.");
        ValidationHelper.validateDirectoryRequestParams(merchantId, merchantSubId, privateCert);

        if (Util.log.isDebugEnabled())
            Util.log.debug("  Generating required attributes.");
        String createDateTimestamp = Util.createDateTimestamp();

        if (Util.log.isDebugEnabled())
            Util.log.debug("  Creating request object.");
        DirectoryReq req = new DirectoryReq();
        XMLGregorianCalendar value = XMLGregorianCalendarImpl.parse(Util.encodeString(createDateTimestamp));
        req.setCreateDateTimestamp(value);
        req.setVersion(INTERFACE_SPECIFICATION_VERSION);
        DirectoryReq.Merchant merchant = new DirectoryReq.Merchant();
        merchant.setMerchantID(Util.encodeString(merchantId));
        merchant.setSubID(Integer.parseInt(merchantSubId));
        req.setMerchant(merchant);

        try {
            if (Util.log.isDebugEnabled())
                Util.log.debug("  Marshalling request object to XML.");
            request = serializer.serializeObject(DirectoryReq.class, req);
            request = signatureHelper.createDigitalSignedMessage(request);

        } catch (Exception ex) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXXB01, ex);
        }
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< createDirectoryRequest(String, SecurityHelper, String, String, String) :: String");
        return request;
    }

    protected static Issuers parseDirectoryResponse(ISerializer serializer, String responseMessage) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> parseDirectoryResponse(String) :: Issuers");
        DirectoryRes res = null;
        try {
            if (Util.log.isDebugEnabled())
                Util.log.debug("  UnMarshalling response XML to object.");

            Object obj = serializer.deserializeObject(DirectoryRes.class, responseMessage);
            res = (DirectoryRes) obj;

        } catch (Exception ex) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXXB02, ex);
        }
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Creating new domain object from response object.");

        Issuers issuers = new Issuers(res.getDirectory().getDirectoryDateTimestamp().toString());
        issuers.setAcquirerID(res.getAcquirer().getAcquirerID());
        LinkedList<Country> countryList = new LinkedList<Country>();
        for (DirectoryRes.Directory.Country country : res.getDirectory().getCountry()) {
            List<Issuer> issuerList = new ArrayList<Issuer>();
            for (DirectoryRes.Directory.Country.Issuer issuer : country.getIssuer()) {
                issuerList.add(new Issuer(issuer.getIssuerID(), issuer.getIssuerName()));
            }
            countryList.add(new Country(country.getCountryNames(), issuerList));
        }
        issuers.setCountryList(countryList);

        if (Util.log.isTraceEnabled())
            Util.log.trace("<< parseDirectoryResponse(String) :: Issuers");
        return issuers;
    }

    protected static String createTransactionRequest(Config config, IDigitalSignatureHelper signatureHelper, ISerializer serializer, Transaction transaction) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> createTransactionRequest(CryptoHelper, Transaction) :: String");
        String request = null;
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Retrieving required parameters");

        // used for instantiation of the data objects needed in xml
        DatatypeFactory dtFactory = null;
        try {
            dtFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            if (Util.log.isDebugEnabled())
                Util.log.debug(Strings.getString(ErrorCodes.IMEXXB03, e));
        }

        String merchantId = config.get(Config.MERCHANT_ID);
        String merchantSubId = config.get(Config.MERCHANT_SUB_ID);
        String merchantReturnURL = config.get(Config.MERCHANT_RETURN_URL);
        String privateCert = config.get(Config.PRIVATE_CERT);
        String expirationPeriod = config.get(Config.DEFAULT_EXPIRATION_PERIOD);

        if (transaction.getCurrency() == null)
            transaction.setCurrency(DEFAULT_CURRENCY);
        if (transaction.getLanguage() == null)
            transaction.setLanguage(DEFAULT_LANGUAGE);

        if (transaction.getMerchantReturnURL() == null) {
            if (Util.log.isTraceEnabled())
                Util.log.trace("  Using merchantReturnURL from config.");
            transaction.setMerchantReturnURL(merchantReturnURL);
        }

        if (transaction.getExpirationPeriod() == null) {
            if (expirationPeriod != null) {
                if (Util.log.isDebugEnabled())
                    Util.log.debug("  Using expirationPeriod from config.");
                transaction.setExpirationPeriod(expirationPeriod);
            } else if (Util.log.isTraceEnabled())
                Util.log.trace("  Will rely on Issuer for expirationPeriod.");
        }
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Checking mandatory attributes.");
        ValidationHelper.validateTransactionRequestParams(transaction, merchantId, merchantSubId, privateCert);
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Generating required attributes.");
        String createDateTimestamp = Util.createDateTimestamp();
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Creating request object.");
        AcquirerTrxReq req = new AcquirerTrxReq();
        req.setCreateDateTimestamp(XMLGregorianCalendarImpl.parse(Util.encodeString(createDateTimestamp)));
        req.setVersion(Util.encodeString(INTERFACE_SPECIFICATION_VERSION));
        AcquirerTrxReq.Merchant merchant = new AcquirerTrxReq.Merchant();
        merchant.setMerchantID(Util.encodeString(merchantId));
        merchant.setSubID(Integer.parseInt(merchantSubId));
        merchant.setMerchantReturnURL(Util.encodeString(transaction.getMerchantReturnURL()));
        req.setMerchant(merchant);

        AcquirerTrxReq.Issuer issuer = new AcquirerTrxReq.Issuer();
        issuer.setIssuerID(Util.encodeString(transaction.getIssuerID()));
        req.setIssuer(issuer);

        AcquirerTrxReq.Transaction merchantTransaction = new AcquirerTrxReq.Transaction();

        merchantTransaction.setPurchaseID(Util.encodeString(transaction.getPurchaseID()));
        merchantTransaction.setAmount(transaction.getAmount());
        merchantTransaction.setCurrency(Util.encodeString(transaction.getCurrency()));
        if (transaction.getExpirationPeriod() != null) {
            Duration value = dtFactory.newDuration(Util.encodeString(transaction.getExpirationPeriod()));
            merchantTransaction.setExpirationPeriod(value);
        }

        merchantTransaction.setLanguage(Util.encodeString(transaction.getLanguage()));
        merchantTransaction.setDescription(Util.encodeString(transaction.getDescription()));
        merchantTransaction.setEntranceCode(Util.encodeString(transaction.getEntranceCode()));

        req.setTransaction(merchantTransaction);

        try {
            if (Util.log.isDebugEnabled())
                Util.log.debug("  Marshalling request object to XML.");
            request = serializer.serializeObject(AcquirerTrxReq.class, req);
            request = signatureHelper.createDigitalSignedMessage(request);

        } catch (Exception ex) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXXB01, ex);
        }
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< createTransactionRequest(CryptoHelper, Transaction) :: String");
        return request;
    }

    protected static Transaction parseTransactionResponse(ISerializer serializer, String responseMessage) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< parseTransactionResponse(String) :: Transaction");
        AcquirerTrxRes res = null;
        try {
            if (Util.log.isDebugEnabled())
                Util.log.debug("  UnMarshalling response XML to object.");
            Object obj = serializer.deserializeObject(AcquirerTrxRes.class,
                    responseMessage);
            res = (AcquirerTrxRes) obj;
        } catch (Exception ex) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXXB02, ex);
        }
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Creating new domain object from response object.");
        Transaction trx = new Transaction();
        trx.setAcquirerID(res.getAcquirer().getAcquirerID());
        trx.setIssuerAuthenticationURL(res.getIssuer().getIssuerAuthenticationURL());
        trx.setTransactionID(res.getTransaction().getTransactionID());
        trx.setTransactionCreateDateTimestamp(res.getTransaction().gettransactionCreateDateTimestamp().toString());
        trx.setPurchaseID(res.getTransaction().getPurchaseID());

        if (Util.log.isTraceEnabled())
            Util.log.trace("<< parseTransactionResponse(String) :: Transaction");
        return trx;
    }

    protected static String createTransactionStatusRequest(Config config, IDigitalSignatureHelper signatureHelper, ISerializer serializer,
                                                           String transactionId) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> createTransactionStatusRequest(String, SecurityHelper, String) :: Transaction");
        String request = null;
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Retrieving required parameters");
        String merchantId = config.get(Config.MERCHANT_ID);
        String merchantSubId = config.get(Config.MERCHANT_SUB_ID);
        String privateCert = config.get(Config.PRIVATE_CERT);
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Checking mandatory attributes.");
        ValidationHelper.validateTransactionStatusParams(transactionId, merchantId, merchantSubId, privateCert);
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Generating required attributes.");
        String createDateTimestamp = Util.createDateTimestamp();
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Creating request object.");
        AcquirerStatusReq req = new AcquirerStatusReq();
        XMLGregorianCalendar value = XMLGregorianCalendarImpl.parse(Util
                .encodeString(createDateTimestamp));
        req.setCreateDateTimestamp(value);
        req.setVersion(Util.encodeString(INTERFACE_SPECIFICATION_VERSION));
        AcquirerStatusReq.Merchant merchant = new AcquirerStatusReq.Merchant();
        merchant.setMerchantID(Util.encodeString(merchantId));
        merchant.setSubID(Integer.parseInt(merchantSubId));
        req.setMerchant(merchant);

        AcquirerStatusReq.Transaction merchantTransaction = new AcquirerStatusReq.Transaction();
        merchantTransaction.setTransactionID(Util.encodeString(transactionId));
        req.setTransaction(merchantTransaction);
        try {
            if (Util.log.isDebugEnabled())
                Util.log.debug("  Marshalling request object to XML.");

            request = serializer.serializeObject(AcquirerStatusReq.class, req);
            request = signatureHelper.createDigitalSignedMessage(request);

        } catch (Exception ex) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXXB01, ex);
        }
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< createTransactionStatusRequest(String, SecurityHelper, String) :: Transaction");
        return request;
    }

    protected static Transaction parseTransactionStatusResponse(ISerializer serializer, String responseMessage) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< parseTransactionStatusResponse(String) :: Transaction");
        AcquirerStatusRes res = null;
        try {
            if (Util.log.isDebugEnabled())
                Util.log.debug("  UnMarshalling response XML to object.");
            Object obj = serializer.deserializeObject(AcquirerStatusRes.class,
                    responseMessage);
            res = (AcquirerStatusRes) obj;
        } catch (Exception ex) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXXB02, ex);
        }

        if (Util.log.isDebugEnabled())
            Util.log.debug("  Creating new domain object from response object.");
        Transaction trx = new Transaction();
        trx.setAcquirerID(res.getAcquirer().getAcquirerID());
        trx.setTransactionID(res.getTransaction().getTransactionID());
        trx.setStatus(res.getTransaction().getStatus());
        if (res.getTransaction().getStatusDateTimestamp() != null) {
            trx.setStatusDateTimestamp(res.getTransaction().getStatusDateTimestamp().toString());
        }
        trx.setConsumerName(res.getTransaction().getConsumerName());
        trx.setConsumerIBAN(res.getTransaction().getConsumerIBAN());
        trx.setConsumerBIC(res.getTransaction().getConsumerBIC());
        trx.setAmount(res.getTransaction().getAmount());
        trx.setCurrency(res.getTransaction().getCurrency());
        trx.setSignature(res.getSignature());
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< parseTransactionStatusResponse(String) :: Transaction");
        return trx;
    }

    protected static void checkResponseForError(ISerializer serializer, String responseMessage) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> checkResponseForError(String)");
        if (responseMessage != null && responseMessage.indexOf("<AcquirerErrorRes") != -1) {
            AcquirerErrorRes res = null;
            try {
                if (Util.log.isDebugEnabled())
                    Util.log.debug("  UnMarshalling response XML to object.");
                res = (AcquirerErrorRes) serializer.deserializeObject(AcquirerErrorRes.class, responseMessage);
            } catch (Exception ex) {
                throw ExceptionHelper.createException(ErrorCodes.IMEXXB02, ex);
            }
            if (Util.log.isDebugEnabled())
                Util.log.debug("  Creating new domain object from response object.");
            IdealException ie = new IdealException(res.getError()
                    .getErrorCode(), res.getError().getErrorMessage(), res
                    .getError().getErrorDetail(), res.getError()
                    .getConsumerMessage());
            ie.setSuggestedAction(res.getError().getSuggestedAction());
            throw ie;
        }
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< checkResponseForError(String)");
    }

    public static void validateRequest(String xml) throws IdealException {
        try {
            InterfaceAcceptantAcquirer_v331.validateMessage(xml);
        } catch (SAXException e) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXVA01, e, e.getMessage());
        } catch (IOException e) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXVA01, e);
        }
    }

    public static void validateResponse(String xml) throws IdealException {
        try {
            InterfaceAcceptantAcquirer_v331.validateMessage(xml);
        } catch (SAXException e) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXXB02, e, e.getMessage());
        } catch (IOException e) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXXB02, e);
        }
    }

    public static synchronized void validateMessage(String xml) throws SAXException, IOException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> validateMessage(String)");
        if (Util.log.isDebugEnabled()) {
            Util.log.debug("  Validating XML.");
            if (Util.log.isTraceEnabled())
                Util.log.trace("   XML: " + xml);
        }
        if (validator == null) {
            validator = createValidator();
        }
        validator.validate(new StreamSource(new StringReader(xml)));
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< validateMessage(String)");
    }

    private static Validator createValidator() throws SAXException {
        if (Util.log.isDebugEnabled())
            Util.log.debug("  Creating new SchemaFactory instance (http://www.w3.org/2001/XMLSchema)");
        SchemaFactory factory = SchemaFactory.newInstance(INTERFACE_XML_SCHEMA_LANGUAGE);
        java.io.InputStream interfaceXMLSchemaIs = (IdealConnector.class).getResourceAsStream(INTERFACE_XML_SCHEMA);
        java.io.InputStream interfaceXMLDSIGSchemaIs = (IdealConnector.class).getResourceAsStream(INTERFACE_XML_DSIG_SCHEMA);
        StreamSource interfaceXMLSchemaSs = new StreamSource(interfaceXMLSchemaIs);
        StreamSource interfaceXMLDSIGSchemaSs = new StreamSource(interfaceXMLDSIGSchemaIs);
        Schema schema = factory.newSchema(new Source[]{interfaceXMLDSIGSchemaSs, interfaceXMLSchemaSs});

        if (Util.log.isDebugEnabled())
            Util.log.debug("  Creating new validator instance for: " + INTERFACE_XML_SCHEMA);
        return schema.newValidator();
    }
}