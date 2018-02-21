<%@ page import="com.ing.ideal.connector.Config" %>
<%@ page import="com.ing.ideal.connector.IdealConnector" %>
<%@ page import="com.ing.ideal.connector.Transaction" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page errorPage="error.jsp" %>

<%
    response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    response.setContentType("text/html; charset=utf-8");

    IdealConnector c = new IdealConnector();
    Transaction result = null;

    String seletedIssuerId = request.getParameter("selectedIssuerId");

    String expPeriod = c.GetConfiguration().get("expirationPeriod");
    if (expPeriod == null) expPeriod = "";

    String submitted = request.getParameter("submitted");
    if (submitted != null) {
        if (("Request Transaction".equals(submitted))) {

            Transaction trx = new Transaction();

            trx.setIssuerID(request.getParameter("issuerId"));
            trx.setPurchaseID(request.getParameter("purchaseId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            trx.setAmount(amount);
            trx.setDescription(request.getParameter("description"));
            trx.setEntranceCode(request.getParameter("entranceCode"));
            trx.setMerchantReturnURL(request.getParameter("merchantReturnURL"));

            if ((request.getParameter("expirationPeriod") != null) && (request.getParameter("expirationPeriod").trim().length() > 0)) {
                trx.setExpirationPeriod(request.getParameter("expirationPeriod"));
            }
            result = c.requestTransaction(trx);

        } else if ("Issuer Authentication".equals(submitted)) {
            response.sendRedirect(result.getIssuerAuthenticationURL());
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
            <td width="200">Issuer ID:</td>
            <td><input type="text" size="60" name="issuerId"
                       value="<%= (request.getParameter("issuerId") != null) ? request.getParameter("issuerId") : seletedIssuerId %>"/>
            </td>
        </tr>
        <tr>
            <td width="200">Purchase ID:</td>
            <td><input type="text" size="60" name="purchaseId"
                       value="<%= (request.getParameter("purchaseId") != null) ? request.getParameter("purchaseId") : "" %>"/>
            </td>
        </tr>
        <tr>
            <td width="200">Amount:</td>
            <td><input type="text" size="60" name="amount"
                       value="<%= (request.getParameter("amount") != null) ? request.getParameter("amount") : "" %>"/>
            </td>
        </tr>
        <tr>
            <td width="200">Description:</td>
            <td><input type="text" maxlength="32" size="60" name="description"
                       value="<%= (request.getParameter("description") != null) ? request.getParameter("description") : "" %>"/>
            </td>
        </tr>
        <tr>
            <td width="200">Entrance Code:</td>
            <td><input type="text" size="60" name="entranceCode"
                       value="<%= (request.getParameter("entranceCode") != null) ? request.getParameter("entranceCode") : "" %>"/>
            </td>
        </tr>
        <tr>
            <td width="200">Merchant Return URL:</td>
            <td><input type="text" size="60" name="merchantReturnURL"
                       value="<%= (request.getParameter("merchantReturnURL") != null) ? request.getParameter("merchantReturnURL") : c.GetConfiguration().get("merchantReturnURL") %>"/>
            </td>
        </tr>
        <tr>
            <td width="200">Expiration Period*:</td>
            <td><input type="text" size="60" name="expirationPeriod"
                       value="<%= (request.getParameter("expirationPeriod") != null) ? request.getParameter("expirationPeriod") : expPeriod %>"/>
            </td>
        </tr>
    </table>
    <br/>

    <table class="box" width="100%">
        <tr>
            <td style="margin:0px;padding:0px">
                <center><input type="submit" name="submitted" value="Request Transaction"/></center>
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
            <td width="200">Transaction ID:</td>
            <td><%= (result != null) ? result.getTransactionID() : "" %>
            </td>
        </tr>
        <tr>
            <td width="200">Issuer Authentication URL:</td>
            <td><%= (result != null) ? result.getIssuerAuthenticationURL() : "" %>
            </td>
        </tr>
    </table>
    <br/>

    <table class="box" width="100%">
        <tr>
            <td style="margin:0px;padding:0px">
                <center><input id="issuerAuthentication" type="button"
                               name="submitted" value="Issuer Authentication"
                        <%
                            if (result == null) {
                                out.print(" disabled=\"disabled\"");
                            } else {
                                if (result.getIssuerAuthenticationURL() != null) {
                                    out.print(" onclick=\"document.location='" + result.getIssuerAuthenticationURL() + "';\"");
                                }
                            }
                        %>/></center>
            </td>
        </tr>
    </table>
    <br/>


</form>
</body>

</html>