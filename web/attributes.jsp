<%-- 
    Document   : attributes
    Created on : Aug 18, 2009, 9:17:32 AM
    Author     : jbf
--%>

<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%@ page import="com.cottagesystems.albumserver.*, java.io.*, java.util.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
String action= request.getParameter("action");
String name= request.getParameter("name");
String id= request.getParameter("id");
String album= request.getParameter("album");

if (action==null ) action="";

Properties p= new Properties();
File propFile= new File( Configuration.getNotesRoot() + id + ".properties" );
propFile.getParentFile().mkdirs();
if ( propFile.exists() ) {
    p.load( new FileInputStream( propFile ) );
}

if ( !Configuration.isValidAttribute(name) ) {
    throw new IllegalArgumentException("invalid attribute name");
}

if ( action.equals("set") ) {
    String value= request.getParameter("value");
    p.put( name, value );
    p.store( new FileOutputStream(propFile), "last modified "+new Date() );
}

out.print( "<h1>Attributes for "+id+"</h1>" );
Enumeration e= p.propertyNames();
String key=null;
while ( e.hasMoreElements() ) {
    key=(String)e.nextElement();
    out.print( String.format( "%s=%s<br>", key, p.getProperty(key) ) );
    }

%>