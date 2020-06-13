<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.cottagesystems.albumserver.*, java.io.*, java.util.*" %>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
        String albumString= request.getParameter("album");
        if ( albumString.equals("") ) albumString=null;
        
        Album album= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(),albumString) );
        
        AlbumIndex index= AlbumIndex.getAlbumIndex( album );
        
        
        List<Media> list= album.getContents();
        
        %>
    </body>
</html>
