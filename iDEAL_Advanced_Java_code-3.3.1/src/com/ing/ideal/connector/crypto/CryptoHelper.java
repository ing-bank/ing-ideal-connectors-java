// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 6/28/2012 2:58:47 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CryptoHelper.java

package com.ing.ideal.connector.crypto;

import com.ing.ideal.connector.ErrorCodes;
import com.ing.ideal.connector.IdealException;
import com.ing.ideal.connector.util.ExceptionHelper;
import com.ing.ideal.connector.util.Strings;
import com.ing.ideal.connector.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;


/**
 * Implementation of {@link IDigitalSignatureHelper} that implements the xml signature protocol.
 */
public class CryptoHelper implements IDigitalSignatureHelper {
    public static final String SIGNATURE_REFERENCE_URI = "";
    public static final String SIGNATURE_REFERENCE_DIGEST_METHOD = DigestMethod.SHA256;
    public static final String SIGNATURE_REFERENCE_TRANSFORM_MODE = Transform.ENVELOPED;
    public static final String SIGNATURE_SIGNED_INFO_ALGORITHM = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    public static final String SIGNATURE_SIGNED_INFO_CANONICALIZATION_METHOD = CanonicalizationMethod.EXCLUSIVE;
    public static final String SIGNATURE_ELEMENT_XMLNS = XMLSignature.XMLNS;
    public static final String SIGNATURE_ELEMENT_TAG_NAME = "Signature";

    private final KeyStoreItem merchantKS;
    private final KeyStoreItem acquirerKS;

    /**
     * @param merchantKS merchant key store information
     * @param acquirerKS acquirer key store information
     */
    public CryptoHelper(KeyStoreItem merchantKS, KeyStoreItem acquirerKS) {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> new SecurityHelper(String, String, String)");
        this.merchantKS = merchantKS;
        this.acquirerKS = acquirerKS;
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< new SecurityHelper(String, String, String)");
    }

    /**
     * Extracts key store from file.
     *
     * @param keyStoreFileName key store filename from classpath
     * @param keyStorePassword encrypted key store password
     * @return key store
     * @throws IdealException in case file not found, incorrect password or other
     */
    private static KeyStore getKeyStore(String keyStoreFileName, String keyStorePassword) throws IdealException {
        Util.log.trace(">> getKeyStore(String, String) :: KeyStore");
        Util.log.debug("   keyStoreFileName=" + keyStoreFileName);
        Util.log.debug("   keyStorePassword=" + keyStorePassword);
        URL fileUrl = searchFile(keyStoreFileName);
        if (fileUrl == null)
            throw new IdealException(ErrorCodes.IMEXSH01, "Unable to find specified keystore file '" + keyStoreFileName + "' on classpath!");
        InputStream is = null;
        KeyStore keyStore = null;
        try {
            is = fileUrl.openStream();
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(is, PasswordUtility.decode(keyStorePassword).toCharArray());
        } catch (Exception e) {
            throw new IdealException(ErrorCodes.IMEXSH01, "Unable to load keystore file '" + keyStoreFileName + "' on classpath!");
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (Exception e) {
                if (Util.log.isDebugEnabled())
                    Util.log.debug(Strings.getString(ErrorCodes.IMEXCO20, e));
            }
        }
        Util.log.trace("<< getKeyStore(String, String) :: KeyStore");
        return keyStore;
    }

    /**
     * Returns an URLto the given path from classpath.
     *
     * @param path classpath path
     * @return url
     */
    private static URL searchFile(String path) {
        Util.log.trace(">> searchFile(String) :: URL");
        ClassLoader loader = CryptoHelper.class.getClassLoader();
        URL result = loader.getResource(path);
        Util.log.trace("   result url: " + result);
        Util.log.trace("<< searchFile(String) :: URL");
        return result;
    }


    private String createSHA1Fingerprint(Certificate cert) throws IdealException {
        Util.log.trace(">> createSHA1Fingerprint(Certificate) :: String");
        MessageDigest sha1Md;
        try {
            sha1Md = MessageDigest.getInstance("SHA1");
            sha1Md.update(cert.getEncoded());
        } catch (Exception e) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXSH06, e);
        }
        byte fp[] = sha1Md.digest();
        String fingerprint = "";
        for (int i = 0; i < fp.length; i++) {
            String f = "00" + Integer.toHexString(fp[i]);
            fingerprint = fingerprint + f.substring(f.length() - 2);
        }
        fingerprint = fingerprint.toUpperCase();
        Util.log.trace("<< createSHA1Fingerprint(Certificate) :: String");
        return fingerprint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createDigitalSignedMessage(String xml) throws IdealException {
        Util.log.trace(">> createDigitalSignedMessage(String) :: KeyStore");
        try {
            /**
             * Signature creation: Step 1
             */
            // Create a DOM XMLSignatureFactory that will be used to generate the enveloped signature.
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            // Create a Reference to the enveloped document (in this case, you are signing the whole document, so a URI of "" signifies
            // that, and also specify the SHA1 digest algorithm and the ENVELOPED Transform.
            DigestMethod digestMethod = fac.newDigestMethod(SIGNATURE_REFERENCE_DIGEST_METHOD, null);
            List<Transform> transformList = Collections.singletonList(fac.newTransform(SIGNATURE_REFERENCE_TRANSFORM_MODE,
                    (TransformParameterSpec) null));
            Reference ref = fac.newReference(SIGNATURE_REFERENCE_URI, digestMethod, transformList, null, null);
            // Create the SignedInfo.
            SignatureMethod method = fac.newSignatureMethod(SIGNATURE_SIGNED_INFO_ALGORITHM, null);
            CanonicalizationMethod canonicalizationMethod = fac.newCanonicalizationMethod(SIGNATURE_SIGNED_INFO_CANONICALIZATION_METHOD,
                    (C14NMethodParameterSpec) null);
            SignedInfo signedInfo = fac.newSignedInfo(canonicalizationMethod, method, Collections.singletonList(ref));

            /**
             * Signature creation: Step 2
             */
            // Load the KeyStore and get the signing key and certificate.
            KeyStore ks = merchantKS.getKeyStore();
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(merchantKS.getAlias(), new KeyStore.PasswordProtection(merchantKS.getCharPassword()));
            X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

            // Create the KeyInfo containing the X509Data.
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(kif.newKeyName(createSHA1Fingerprint(cert))));

            /**
             * Signature creation: Step 3
             */
            // Instantiate the document to be signed.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

            // Create a DOMSignContext and specify the RSA PrivateKey and
            // location of the resulting XMLSignature's parent element.
            DOMSignContext dsc = new DOMSignContext(keyEntry.getPrivateKey(), doc.getDocumentElement());

            // Create the XMLSignature, but don't sign it yet.
            XMLSignature signature = fac.newXMLSignature(signedInfo, keyInfo);
            // Marshal, generate, and sign the enveloped signature.
            signature.sign(dsc);

            /**
             * Signature creation: Step 4
             */
            // Output the resulting document.
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(doc), result);

            Util.log.trace(">> createDigitalSignedMessage(String) :: KeyStore");
            return stringWriter.getBuffer().toString();

        } catch (Exception e) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXSH16, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verifyDigitalSignedMessage(String xml) throws IdealException {

        Util.log.trace(">> verifyDigitalSignedMessage(String) :: KeyStore");
        boolean answer = false;
        try {
            /**
             * Signature verification: Step 1
             */
            // Load the KeyStore and get the signing key and certificate.
            KeyStore ks = acquirerKS.getKeyStore();
            X509Certificate cert = (X509Certificate) ks.getCertificate(acquirerKS.getAlias());
            PublicKey publicKey = cert.getPublicKey();

            /**
             * Signature verification: Step 2
             */
            // Instantiate the document to be signed.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document tdoc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            // Find Signature element.
            NodeList nl = tdoc.getElementsByTagNameNS(SIGNATURE_ELEMENT_XMLNS, SIGNATURE_ELEMENT_TAG_NAME);
            if (nl.getLength() == 0) {
                throw ExceptionHelper.createException(ErrorCodes.IMEXME01, "Could not find signature element!");
            }
            // Create a DOMValidateContext and specify a KeySelector
            // and document context.
            DOMValidateContext valContext = new DOMValidateContext(KeySelector.singletonKeySelector(publicKey), nl.item(0));
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            XMLSignature signat = fac.unmarshalXMLSignature(valContext);
            answer = signat.validate(valContext);

            Util.log.trace(">> verifyDigitalSignedMessage(String) :: KeyStore");
            return answer;
        } catch (Exception e) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXSH16, e);
        }
    }

    /**
     * @param keyStoreFilename keyStore file name
     * @param keyStorePassword encrypted keyStore password
     * @param certName         certificate name
     * @return {@link KeyStoreItem} instance
     * @throws IdealException in case key store can not be created
     */
    public static KeyStoreItem createKeyStoreItem(String keyStoreFilename, String keyStorePassword, String certName) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> createKeyStoreItem(String, String, String)");
        KeyStore ks = getKeyStore(keyStoreFilename, keyStorePassword);
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> addKeyStore(String, String)");
        return new KeyStoreItem(ks, keyStorePassword, certName);
    }

}