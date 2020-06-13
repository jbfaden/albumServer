<%-- 
    Document   : Status
    Created on : Mar 22, 2008, 9:39:28 AM
    Author     : jbf
--%>

<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%@page import="com.cottagesystems.albumserver.*, java.util.*, java.io.File" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <title>Status Page</title>
    </head>
    <body>
        <p>ImageDataBase: <%= Configuration.getImageDatabaseRoot() %></p>
        <p>NotesRoot: <%= Configuration.getNotesRoot() %></p>
        <p>CacheRoot: <%= Configuration.getCacheRoot() %></p>
        <%
            try {
                com.cottagesystems.albumserver.LoginBean.reload();
                %>password.txt and ipaccess.txt were reloaded.<%
            } catch ( Exception ex ) {
                %>Reload again soon.<%
            }
        %>
                
        
    </body>
</html>
