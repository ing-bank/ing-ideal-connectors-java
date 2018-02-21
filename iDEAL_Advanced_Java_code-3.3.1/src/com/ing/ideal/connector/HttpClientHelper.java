package com.ing.ideal.connector;

import com.ing.ideal.connector.crypto.PasswordUtility;
import com.ing.ideal.connector.util.ExceptionHelper;
import com.ing.ideal.connector.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static com.ing.ideal.connector.Constants.API_VERSION;
import static com.ing.ideal.connector.Constants.INTERFACE_SPECIFICATION_VERSION;

/**
 * Helper class for http connections.
 */
class HttpClientHelper {
    protected static final String SSL_MODE_RELAXED = "relaxed";
    protected static final String SSL_MODE_STRICT = "strict";
    protected static final String SSL_MODE_IGNORANCE = "ignorance";
    protected static final String HTTP_CONTENT_TYPE = "text/xml; charset=UTF-8";
    protected static final String HTTP_USER_AGENT = "IAC/" + API_VERSION + " (ING Group; Java Edition; protocol: " + INTERFACE_SPECIFICATION_VERSION + ")";


    /**
     * Creates a new Http client helper.
     * Config needs to be initialized before calling this constructor.
     *
     * @throws IdealException
     */
    protected HttpClientHelper() throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> new HttpClientHelper(String)");
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< new HttpClientHelper(String)");
    }

    /**
     * Sends an https post request and returns the response
     *
     * @param requestMessage request
     * @param targetUrl      target url
     * @return response
     * @throws IdealException
     */
    protected String sendHttpRequest(Config config, String requestMessage, String targetUrl) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace(">> sendHttpRequest(String) :: String");
        String responseString = null;
        HttpPost method = new HttpPost(targetUrl);

        HttpEntity entity = new ByteArrayEntity(requestMessage.getBytes(), ContentType.TEXT_XML);
        if (Util.log.isTraceEnabled()) {
            Util.log.trace("   content.type: " + entity.getContentType());
            Util.log.trace("   content.length: " + entity.getContentLength());
        }
        method.setEntity(entity);

        DefaultHttpClient httpclient = createHttpClient(config);
        HttpResponse httpResponse;
        try {
            int timeout = 10;
            String _timeout = config.get(Config.ACQUIRER_TIMEOUT);
            if (_timeout != null)
                try {
                    timeout = Integer.parseInt(_timeout);
                } catch (NumberFormatException e) {
                    throw ExceptionHelper.createException(ErrorCodes.IMEXCO16, e, "acquirerTimeout");
                }
            if (Util.log.isDebugEnabled())
                Util.log.debug("  Setting HTTP Socket timeout to " + timeout + " second(s) (" + 1000 * timeout + " millisecond(s)).");

            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000 * timeout);           // The time it takes to open TCP connection.
            httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000 * timeout);         // Timeout when server does not send data.

            httpResponse = httpclient.execute(method);
        } catch (Exception ex) {
            IdealException ex2 = ExceptionHelper.createException(ErrorCodes.IMEXHC01, ex);
            if (ex instanceof UnknownHostException) {
                ex2.setErrorDetail("Host " + ex.getMessage() + " in URL " + targetUrl + " is unknown or not valid!");
                ex2.setSuggestedAction("Check the validity and if it is the correct URL for the request.");
            }
            throw ex2;
        }
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            IdealException ex = ExceptionHelper.createException(ErrorCodes.IMEXHC02, httpResponse.getStatusLine());
            switch (statusCode) {
                case 407:
                    ex.setErrorDetail("Proxy authentication is required to access URL " + targetUrl + ".");
                    ex.setSuggestedAction("Setup proxy authentication in the iDEAL Advanced configuration.");
                    break;

                case 404:
                    ex.setErrorDetail("Requested page declared in URL " + targetUrl + " can not be found!");
                    ex.setSuggestedAction("Check that the URL is the correct URL for the request.");
                    break;

                default:
                    ex.setErrorDetail("Unexpected error while accessing URL " + targetUrl + ".");
                    ex.setSuggestedAction("Check that the URL is a valid URL and the correct URL for the request.");
                    break;
            }
            throw ex;
        }
        try {
            InputStream content = httpResponse.getEntity().getContent();
            byte rawdata[] = IOUtils.toByteArray(content);
            responseString = new String(rawdata, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXHC03, ex);
        } finally {
            if (method != null)
                method.releaseConnection();
        }
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< sendHttpRequest(String) :: String");
        return responseString;
    }

    /**
     * Creates a new DefaultHttpClient.
     *
     * @param config
     * @return DefaultHttpClient
     * @throws IdealException
     */
    private DefaultHttpClient createHttpClient(Config config) throws IdealException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        setupParameters(httpclient);
        try {
            setupSSLProtocol(config, httpclient);
        } catch (Exception e1) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXHC04, e1);
        }
        setupProxy(config, httpclient);
        return httpclient;
    }

    /**
     * Sets the ssl mode for httpclient based on config.SSL_PROTOCOL_MODE.
     *
     * @param config config
     * @param httpClient httpClient
     * @throws Exception
     */
    private void setupSSLProtocol(Config config, DefaultHttpClient httpClient) throws Exception {
        SchemeSocketFactory sf = null;
        String sslProtocolMode = config.get(Config.SSL_PROTOCOL_MODE);
        if (SSL_MODE_IGNORANCE.equals(sslProtocolMode)) {
            if (Util.log.isWarnEnabled()) {
                Util.log.warn("  Configuring connection with ignorance mode SSL protocol !!!");
                Util.log.warn("  SSL protocol configured will not validate certificates !!!");
            }
            sf = createIgnoreAllSSLFactory();
        } else if (SSL_MODE_RELAXED.equals(sslProtocolMode)) {
            if (Util.log.isInfoEnabled()) {
                Util.log.info("  Configuring connection with relaxed mode SSL protocol !!!");
                Util.log.info("  SSL protocol configured will allow self-signed certificates !!!");
            }
            sf = createRelaxedSSLFactory();
        } else if (sslProtocolMode == null || SSL_MODE_STRICT.equals(sslProtocolMode)) {
            if (Util.log.isInfoEnabled()) {
                Util.log.info("  Configuring connection with strict mode SSL protocol !!!");
                Util.log.info("  SSL protocol configured will verify hostnames !!!");
            }
            sf = createStrictSSLFactory( config);
        } else if (Util.log.isInfoEnabled()) {
            Util.log.warn("  Configuration for ssl mode not recognized. Found value:" + sslProtocolMode);
            throw ExceptionHelper.createException(ErrorCodes.IMEXHC04);
        }
        if (sf != null) {
            Scheme scheme = new Scheme("https", 443, sf);
            httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
        }
    }

    /**
     * Creates a {@link SSLSocketFactory} that allows self signed certificates.
     *
     * @return
     * @throws Exception
     */
    private SSLSocketFactory createRelaxedSSLFactory() throws Exception {
        return new SSLSocketFactory(new TrustSelfSignedStrategy());
    }

    /**
     * Creates a {@link SSLSocketFactory} that imposes strict hostname validation.
     *
     * @param config config
     * @return
     *
     * @throws Exception
     */
    private SSLSocketFactory createStrictSSLFactory(Config config) throws Exception {
        String tlsVersion = Boolean.valueOf(config.get(Config.ENABLE_TLS_v12)) ? "TLSv1.2" : "TLS";
        //String tlsVersion = "TLS";
        /*
        String tls = config.get(Config.ENABLE_TLS_v12);
        if(tls != null && tls.equals("true"))
        {
            tlsVersion="TLSv1.2";
        }
*/

        System.out.println("\n\n\n tls version: " + tlsVersion +"\n\n\n");

        SSLContext sslcontext = SSLContext.getInstance(tlsVersion);
        sslcontext.init(null, null, null);
        return new SSLSocketFactory(sslcontext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
    }

    /**
     * Creates a {@link SSLSocketFactory} that allows all certificates.
     *
     * @return
     * @throws Exception
     */
    public SSLSocketFactory createIgnoreAllSSLFactory() throws Exception {
        return new SSLSocketFactory(new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        });
    }

    /**
     * Sets up http protocol parameters.
     *
     * @param httpClient httpClient
     */
    private void setupParameters(HttpClient httpClient) {
        httpClient.getParams().setParameter("http.useragent", HTTP_USER_AGENT);
        httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
        if (Util.log.isTraceEnabled()) {
            Util.log.trace("   http.useragent: " + httpClient.getParams().getParameter("http.useragent"));
            Util.log.trace("   http.protocol.version: " + httpClient.getParams().getParameter("http.protocol.version"));
            Util.log.trace("   http.protocol.content-charset: " + httpClient.getParams().getParameter("http.protocol.content-charset"));
        }
    }

    /**
     * Searches the config for proxy configuration and applies it if found.
     *
     * @param httpClient httpClient
     * @throws IdealException
     */
    private void setupProxy(Config config, DefaultHttpClient httpClient) throws IdealException {
        String httpProxyHost = config.get(Config.HTTP_PROXY_HOST);
        String httpProxyPort = config.get(Config.HTTP_PROXY_PORT);
        String httpProxyUser = config.get(Config.HTTP_PROXY_USER);
        String encHttpProxyPass = config.get(Config.HTTP_PROXY_PASS);
        int _httpProxyPort = -1;
        if (httpProxyHost != null && httpProxyPort != null)
            try {
                _httpProxyPort = Integer.parseInt(httpProxyPort);

                if (Util.log.isDebugEnabled())
                    Util.log.debug("  Configuring proxy as: host=" + httpProxyHost + ", port=" + httpProxyPort);
                HttpHost proxy = new HttpHost(httpProxyHost, _httpProxyPort);
                httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

                if (httpProxyUser != null && encHttpProxyPass != null) {
                    if (Util.log.isTraceEnabled())
                        Util.log.trace("  Configuring proxy credentials as: httpProxyUser=" + httpProxyUser + ", encHttpProxyPass=" + encHttpProxyPass);
                    httpClient.getCredentialsProvider().setCredentials(
                            new AuthScope(httpProxyHost, _httpProxyPort, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME),
                            new UsernamePasswordCredentials(httpProxyUser, PasswordUtility.decode(encHttpProxyPass)));
                }
            } catch (NumberFormatException e1) {
                throw ExceptionHelper.createException(ErrorCodes.IMEXCO16, e1, Config.HTTP_PROXY_PORT);
            } catch (Exception e1) {
                throw ExceptionHelper.createException(ErrorCodes.IMEXCO11, e1, Config.HTTP_PROXY_PASS);
            }
    }
}