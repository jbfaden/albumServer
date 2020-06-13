<%-- 
    Document   : refresh
    Created on : Jul 30, 2008, 11:52:55 PM
    Author     : jbf
--%>

<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%@ page import="com.cottagesystems.albumserver.*, java.io.*, java.util.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
    String albumId= request.getParameter("album");
    
    Album a= Album.getAlbum(albumId);
    if ( a!=null ) {
        a.refresh();
    }
    
    response.sendRedirect( "AlbumServerDirectory0.jsp?album="+albumId );
%>