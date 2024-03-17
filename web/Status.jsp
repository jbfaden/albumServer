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
            Configuration.load( request.getServletContext().getInitParameter("albumServerHome") ); 
            File dbRoot = new File( Configuration.getImageDatabaseRoot() );
            File cacheFile= new File( Configuration.getCacheRoot() );
            File notesRoot= new File( Configuration.getNotesRoot() );
            String username= java.lang.System.getProperty("user.name");
            try {
                com.cottagesystems.albumserver.LoginBean.reload();
                %>password.txt and ipaccess.txt were reloaded.<%
            } catch ( Exception ex ) {
                %>Reload again soon.<%
            }
        %>        
        <p>The server pulls its albums from the ImageDatabase.  Each album
            is a Unix folder containing png and other media files.<br>
            ImageDataBase: <%= Configuration.getImageDatabaseRoot() %>
            <%= dbRoot.canRead() ? "(" + username + " can read)" : "<b>("+username + " cannot read)</b>" %></p>

        <p>The folder where notes are added to the server is called NotesRoot.  The server must
            be able to write to this location.<br>
            NotesRoot: <%= Configuration.getNotesRoot() %> 
               <%= notesRoot.canWrite() ? "(" + username + " can write)" : "<b>("+username + " cannot write)</b>" %></p>

        <p>CacheRoot is where computed images are stored.<br>
        CacheRoot: <%= Configuration.getCacheRoot() %>
        <%= cacheFile.canWrite() ? "(" + username + " can write)" : "<b>("+username + " cannot write)</b>" %>
        </p>
        
        <p>NotesURL is a GitLabs or GitHub server where the notes are kept.<br>
        NotesURL: 
        <%
            if ( Configuration.getNotesURL()==null ) { %>
                (not used)
            <% } else { %>
            <a href="<%=Configuration.getNotesURL()%>" target="top"><%=Configuration.getNotesURL()%></a>
            <% } %>
        </p>
        
        <p>Video thumbnailer is a program which extracts images from a video.
        <%
            if ( Configuration.getVideoThumbnailer()==null ) { %>
                (no thumbnailer)
            <% } else { %>
            <a href="<%=Configuration.getVideoThumbnailer()%>" target="top"><%=Configuration.getVideoThumbnailer()%></a>
            <% } %>
        </p>
        
        <p>Java: <%= System.getProperty("java.version") %>
        <%
            try {
                com.cottagesystems.albumserver.LoginBean.reload();
                %>password.txt and ipaccess.txt were reloaded.<%
            } catch ( Exception ex ) {
                %>Reload again soon.<%
            }
        %>
        <p>AlbumServer version: <%= Util.getAlbumServerVersion() %></p>
        <p><em><a href='AlbumServerContent0.jsp'>Return</a> to content.</em></p>
    </body>
</html>
