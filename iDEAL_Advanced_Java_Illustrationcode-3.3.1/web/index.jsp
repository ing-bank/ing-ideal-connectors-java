<%@page
        language="java"
        isErrorPage="true"
        info="Displays IdealException objects"
        %>
<%
    response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    response.setContentType("text/html; charset=utf-8");

%><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http:‎//www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="StyleSheet" href="style.css"/>
    <title>IAC-JAVA</title>
</head>

<body>

<table border="0" width="100%">
    <tr>
        <td width="120"><a href="<%=request.getContextPath()%>"> <img src="ideal_logo.gif" alt=""/></a></td>
        <td><span style="font: bold 24pt arial">Advanced Connector - Java</span><br/>
            <span style="font: 18pt arial">**Test Page**</span></td>
    </tr>
</table>

For a successfull iDeal payment the following flow should be followed.<br/><br/>

<table cellpadding="4" cellspacing="0" border="1">
    <tr>
        <td><b>Step</b></td>
        <td><b>Description</b></td>
        <td><b>Action</b></td>
    </tr>
    <tr>
        <td align="center">1</td>
        <td>Requests a list* of issuers.</td>
        <td>(Function: <a href="Function.getIssuerList.jsp">getIssuerList</a>)</td>
    </tr>
    <tr>
        <td align="center">2</td>
        <td>Select an issuer.</td>
        <td><i>User action</i></td>
    </tr>
    <tr>
        <td align="center">3</td>
        <td>Requests a new transaction.</td>
        <td>(Function: <a href="Function.requestTransaction.jsp">requestTransaction</a>)</td>
    </tr>
    <tr>
        <td align="center">4</td>
        <td>Authenticate transaction.</td>
        <td><i>User/Acceptant action</i></td>
    </tr>
    <tr>
        <td align="center">5</td>
        <td>Request transaction status.</td>
        <td>(Function: <a href="Function.requestTransactionStatus.jsp">requestTransactionStatus</a>)</td>
    </tr>
</table>
<br/>
This API provides functionality for performing steps <b>1, 3</b> &amp; <b>5</b>.<br/>
<br/>
<i>
    * For optimal performance the retrieved list could be cached.<br/>
</i>


</body>

</html>