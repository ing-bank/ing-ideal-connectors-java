<%@ page import="com.ing.ideal.connector.*" %>
<%@ page errorPage="error.jsp" %>

<%
    response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    response.setContentType("text/html; charset=utf-8");


    IdealConnector c = new IdealConnector();
    Issuers issuers = null;

    String submitted = request.getParameter("submitted");
    if (submitted != null) {
        if (("Get Issuer List".equals(submitted))) {
            issuers = c.getIssuerList();
        } else if (("Transaction Request".equals(submitted))) {
            String issuerId = request.getParameter("issuerId");
            if (issuerId != null) {
                response.sendRedirect("Function.requestTransaction.jsp?selectedIssuerId=" + issuerId);
            }
        }
    }

%>

<!DOCTYPE html>
<html xmlns="http:‎//www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="StyleSheet" href="style.css"/>
    <title>IAC-JAVA - Function: Get Issuer List</title>
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
            <td>getIssuerList</td>
        </tr>
    </table>
    <br/>

    <table class="box" width="100%">
        <tr>
            <td colspan="2"><i><u>Function parameters:</u></i></td>
        </tr>
        <tr>
            <td colspan="2">(none)</td>
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
        <tr>
            <td width="200">Acquirer URL:</td>
            <td><%= c.GetConfiguration().get("acquirerURL") %>
            </td>
        </tr>
    </table>
    <br/>

    <table class="box" width="100%">
        <tr>
            <td style="margin:0px;padding:0px">
                <center><input type="submit" name="submitted" value="Get Issuer List"/></center>
            </td>
        </tr>
    </table>
    <br/>

    <table class="box" width="100%">
        <tr>
            <td colspan="2"><i><u>Result:</u></i></td>
        </tr>
        <tr>
            <td width="200">DateTimeStamp:</td>
            <td><%= (issuers != null) ? issuers.getDateTimeStamp() : "" %>
            </td>
        </tr>
        <tr>
            <td width="200">Issuer List:</td>
            <td><%
                if (issuers != null) {
                    out.println("<select name=\"issuerId\" onchange=\"document.getElementById('transactionRequest').disabled = false;\">");

                    java.util.List<Country> countryList = issuers.getCountryList();
                    out.println("<option>--Selecteer--</option>");
                    if (countryList != null)
                        for (Country country : countryList) {
                            out.println("<optgroup label=\"" + country.getCountryNames() + " Overige Banken\">");
                            java.util.List<Issuer> list = country.getIssuers();
                            for (Issuer issuer : list) {
                                out.println("  <option value=\"" + issuer.getIssuerID() + "\">" + issuer.getIssuerName() + "</option>");
                            }
                            out.println("</optgroup>");
                        }
                    out.println("</select>");
                }
            %></td>
        </tr>
    </table>
    <br/>

    <table class="box" width="100%">
        <tr>
            <td style="margin:0px;padding:0px">
                <center><input id="transactionRequest" type="submit" name="submitted" value="Transaction Request"
                               disabled="disabled"/></center>
            </td>
        </tr>
    </table>
    <br/>

</form>
</body>

</html>