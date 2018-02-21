<%@page
        language="java"
        import="com.ing.ideal.connector.IdealException"
        isErrorPage="true"
        info="Displays IdealException objects" %>
<%
    response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    response.setContentType("text/html; charset=utf-8");

%>

<!DOCTYPE html>
<html xmlns="http:‎//www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="StyleSheet" href="style.css"/>
    <title>IAC-JAVA - Error</title>
</head>

<body>

<table border="0" width="100%">
    <tr>
        <td width="120"><a href="<%=request.getContextPath()%>"> <img src="ideal_logo.gif" alt=""/></a></td>
        <td><span style="font: bold 24pt arial">Advanced Connector - Java</span><br/>
            <span style="font: 18pt arial">**Test Page**</span></td>
    </tr>
</table>

<table class="box" width="100%">
    <tr>
        <td colspan="2">
            <center><b>An application error has occurred.</b></center>
        </td>
    </tr>
</table>
<br/>

<table class="box" width="100%">
    <tr>
        <td colspan="2"><i>Exception:</i></td>
    </tr>
    <tr>
        <td width="200">Type:</td>
        <td><%= exception.getClass().getName() %>
        </td>
    </tr>
    <tr>
        <td width="200">Message:</td>
        <td><%= exception.getMessage() %>
        </td>
    </tr>
</table>
<br/>
<%
    if (exception.getCause() != null) {
%>

<table class="box" width="100%">
    <tr>
        <td colspan="2"><i>Cause:</i></td>
    </tr>
    <tr>
        <td width="200">Type:</td>
        <td><%= exception.getCause().getClass().getName() %>
        </td>
    </tr>
    <tr>
        <td width="200">Message:</td>
        <td><%= exception.getCause().getMessage() %>
        </td>
    </tr>
</table>
<br/>
<%
    }

    if (exception instanceof IdealException) {
        IdealException ex = (IdealException) exception;
%>
<table class="box" width="100%">
    <tr>
        <td colspan="2"><i>Details:</i></td>
    </tr>
    <tr>
        <td width="200">Error Code:</td>
        <td><%= (ex.getErrorCode() != null) ? ex.getErrorCode() : "(null)" %>
        </td>
    </tr>
    <tr>
        <td width="200">Error Message:</td>
        <td><%= (ex.getErrorMessage() != null) ? ex.getErrorMessage() : "(null)" %>
        </td>
    </tr>
    <tr>
        <td width="200">Error Detail:</td>
        <td><%= (ex.getErrorDetail() != null) ? ex.getErrorDetail() : "(null)" %>
        </td>
    </tr>
    <tr>
        <td width="200">Consumer Message:</td>
        <td><%= (ex.getConsumerMessage() != null) ? ex.getConsumerMessage() : "(null)" %>
        </td>
    </tr>
    <tr>
        <td width="200">Suggested Action:</td>
        <td><%= (ex.getSuggestedAction() != null) ? ex.getSuggestedAction() : "(null)" %>
        </td>
    </tr>
</table>
<br/>
<%
    }
%>

<table class="box" width="100%">
    <tr>
        <td colspan="2"><i>Stacktrace:</i></td>
    </tr>
    <tr>
        <td>
<pre>

<%
    if (exception != null) {
        exception.printStackTrace(new java.io.PrintWriter(out));
    } else if ((Exception) request.getAttribute("javax.servlet.error.exception") != null) {
        ((Exception) request.getAttribute("javax.servlet.error.exception")).printStackTrace(new java.io.PrintWriter(out));
    }
%>
</pre>
        </td>
    </tr>
</table>
<br/>

</body>

</html>