<%-- 
    Document   : SearchProgress
    Created on : Jan 10, 2008, 7:42:08 PM
    Author     : jbf
--%>

<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%@ page import="com.cottagesystems.albumserver.*, java.io.*, java.util.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
    String key= request.getParameter("keywords");
    final String[] keywords= key.split("\\s+");
    
    final HttpSession thisSession= session;
    
    Runnable run= new Runnable() {
        public void run() {
            SearchEngine se= new SearchEngine();
            for ( int i=0; i<keywords.length; i++ ) {
                se.addKeyword( keywords[i] );
            }
            
            Album album= new Album("searchAlbum"); 
            
            int count= se.doSearch( new File( Configuration.getNotesRoot() ), album, 0, 100 );
            
            thisSession.setAttribute( "searchAlbum", album );
        }
    };
    
    run.run();
    
    response.sendRedirect( "AlbumServer0.jsp?album=searchAlbum" );
%>
