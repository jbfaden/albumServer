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
        <%
            File cacheFile= new File( Configuration.getCacheRoot() );
            File notesRoot= new File( Configuration.getNotesRoot() );
            String username= java.lang.System.getenv("user.name");
            try {
                com.cottagesystems.albumserver.LoginBean.reload();
                %>password.txt and ipaccess.txt were reloaded.<%
            } catch ( Exception ex ) {
                %>Reload again soon.<%
            }
        %>        
        <p>The server pulls its albums from the ImageDataBase.  Each album
            is a Unix folder containing png and other media files.<br>
            ImageDataBase: <%= Configuration.getImageDatabaseRoot() %></p>
        <p>The folder where notes are added to the server is called NotesRoot.  The server must
            be able to write to this location.<br>
            NotesRoot: <%= Configuration.getNotesRoot() %> <%= notesRoot.canWrite() ? "ok" : username + "cannot write" %></p>
        <p>CacheRoot is where computed images are stored.<br>
        CacheRoot: <%= Configuration.getCacheRoot() %>
        <%= cacheFile.canWrite() ? "ok" : username + " cannot write" %>
        
        </p>
        <%
            try {
                com.cottagesystems.albumserver.LoginBean.reload();
                %>password.txt and ipaccess.txt were reloaded.<%
            } catch ( Exception ex ) {
                %>Reload again soon.<%
            }
        %>
        <p><em><a href='AlbumServerContent0.jsp'>Return</a> to content.</em></p>
    </body>
</html>
