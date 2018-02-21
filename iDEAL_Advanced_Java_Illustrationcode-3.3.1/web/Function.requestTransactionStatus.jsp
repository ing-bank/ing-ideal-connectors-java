<%@ page import="com.ing.ideal.connector.Config" %>
<%@ page import="com.ing.ideal.connector.IdealConnector" %>
<%@ page import="com.ing.ideal.connector.Transaction" %>
<%@ page import="org.w3._2000._09.xmldsig.SignatureType" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page errorPage="error.jsp" %>

<%!
    /**
     * Converts null strings to empty string.
     * @param string string
     * @return empty string if string is null,same string otherwise
     */
    public String trimToEmpty(String string) {
        return string == null ? "" : string;
    }

    /**
     * Extracts signature value and returns it as string.
     * @param signature signature
     * @return signature value
     * @throws UnsupportedEncodingException
     */
    public String extractSignatureValue(SignatureType signature) throws UnsupportedEncodingException {
        return Base64.encodeBase64String(signature.getSignatureValue().getValue());
    }
%>
<%
    response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    response.setContentType("text/html; charset=utf-8");

    IdealConnector c = new IdealConnector();
    Transaction result = null;

    String submitted = request.getParameter("submitted");
    if (submitted != null) {
        if (("Request Transaction Status".equals(submitted))) {
            result = c.requestTransactionStatus(request.getParameter("transactionId"));
        }
    }

%>

<!DOCTYPE html>
<html xmlns="http:‎//www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="StyleSheet" href="style.css"/>
    <title>IAC-JAVA - Function: Request Transaction</title>
</head>

<body>
<form method="post">

    <table border="0" width="100%">
        <tr>
            <td width="120"><a href="<%=request.getContextPath()%>"> <img src="ideal_logo.gif" alt=""/></a></td>
            <td><span style="font: bold 24pt arial">Advanced Connector - Java</span><br/>
                <span style="font: 18pt arial">**Test Page**</span></td>
        </tr>
    </table>

    <table class="box" width="100%">
        <tr>
            <td width="200"><i><u>Function:</u></i></td>
            <td>requestTransaction</td>
        </tr>
    </table>
    <br/>

    <table class="box" width="100%">
        <tr>
            <td colspan="2"><i><u>Configuration parameters:</u></i></td>
        </tr>
        <tr>
            <td width="200">Merchant ID:</td>
            <td><%= c.GetConfiguration().get("merchantId") %>
            </td>
        </tr>
        <tr>
            <td width="200">Sub ID:</td>
            <td><%= c.GetConfiguration().get("merchantSubId") %>
            </td>
        </tr>
    </table>
    <br/>

    <table class="box" width="100%">
        <tr>
            <td colspan="2"><i><u>Function parameters:</u></i></td>
        </tr>
        <tr>
            <td width="200">Transaction ID:</td>
            <td><input type="text" size="60" name="transactionId"
                       value="<%= (request.getParameter("trxid") != null) ? request.getParameter("trxid") : "" %>"/>
            </td>
        </tr>
    </table>
    <br/>

    <table class="box" width="100%">
        <tr>
            <td style="margin:0px;padding:0px">
                <center><input type="submit" name="submitted" value="Request Transaction Status"/></center>
            </td>
        </tr>
    </table>
    <br/>

    <table class="box" width="100%">
        <tr>
            <td colspan="2"><i><u>Result:</u></i></td>
        </tr>
        <tr>
            <td width="200">Acquirer ID:</td>
            <td><%= (result != null) ? result.getAcquirerID() : "" %>
            </td>
        </tr>
        <tr>
            <td width="200">Transaction_Status:</td>
            <td><%= (result != null) ? result.getStatus() + "" : "" %>
            </td>
        </tr>
        <tr>
            <td width="200">Consumer IBAN:</td>
            <td><%= (result != null) ? trimToEmpty(result.getConsumerIBAN()) : "" %>
            </td>
        </tr>
        <tr>
            <td width="200">Consumer BIC:</td>
            <td><%= (result != null) ? trimToEmpty(result.getConsumerBIC()) : "" %>
            </td>
        </tr>
        <tr>
            <td width="200">Status date timestamp:</td>
            <td><%= (result != null) ? trimToEmpty(result.getStatusDateTimestamp()) : "" %>
            </td>
        </tr>
        <tr>
            <td width="200">Consumer Name:</td>
            <td><%= (result != null) ? trimToEmpty(result.getConsumerName()) : "" %>
            </td>
        </tr>
        <tr>
            <td width="200">Signature:</td>
            <td>
                <div style="word-wrap:break-word; width: 70em;"><%= (result != null) ? extractSignatureValue(result.getSignature()) : "" %>
                </div>
            </td>
        </tr>
    </table>
    <br/>

</form>
</body>

</html>