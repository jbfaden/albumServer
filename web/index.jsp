<%@page import="com.cottagesystems.albumserver.Configuration"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
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
        <title>Album Server</title>
    </head>
    <body>
        <h1>Album Server</h1>
        The Album Server is a tool for managing family photos.  Images, Videos,
        Sounds and other content is stored in Albums, which correspond to 
        directories on the server.  Simple security is allowed, but most
        albums are public by default.
        <br>
        <hr>
    <a href="AlbumServer0.jsp">open album server</a>
    <hr>
    <small>Version 20240304.1</small>
    </body>
</html>
