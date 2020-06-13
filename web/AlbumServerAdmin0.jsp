<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.cottagesystems.albumserver.*, java.net.*" %>
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

<jsp:useBean id="formHandler" class="com.cottagesystems.albumserver.AdminBean" scope="request">
<jsp:setProperty name="formHandler" property="*"/>
</jsp:useBean>

<%
   String id= request.getParameter("id");
   if ( id==null ) id= formHandler.getId();
   
   String album= null;
   if ( id==null ) {
       album= request.getParameter("album");
   }
   Media m= Media.createMedia( id );
   Album a= Album.getAlbum(album);
   
   String returnUrl= request.getParameter("returnUrl");
   if ( returnUrl==null ) returnUrl=  formHandler.getReturnUrl();
%> 

<%
   if ( "modify".equals( formHandler.getAction() ) ) {
      String meta= formHandler.getNotes();
      if ( m==null && a!=null ) {
          if ( meta==null ) {
              a.setNotes(null);
          } else {
              a.setNotes(formHandler.getNotes().split("\n"));
          }          
      } else {
          if ( meta==null ) {
              m.setNotes(null);
          } else {
              m.setNotes(formHandler.getNotes().split("\n"));
          }
      }
      response.sendRedirect( response.encodeRedirectURL( URLDecoder.decode(formHandler.getReturnUrl(),"UTF-8") ) );
   }
%>
<br>
<%= ( m!=null ) ? m.getURL( "&size=800" ) : "" %>

<form action="AlbumServerAdmin0.jsp"  method="POST">

   <br><i>Arbitrary textual notes for the image should be entered below.</i><br>
<textarea rows=10 cols=80 name='notes'><% 
    String[] notes= m!=null ? m.getNotes() : a.getNotes();
    for ( int i=0; i<notes.length; i++ ) {
      out.println(notes[i]);
    } %></textarea><br>
    <% if ( m!=null ) { %>
    <input type="hidden" name="id" value="<%= m.getId() %>" >
    <% } else { %>
    <input type="hidden" name="album" value="<%= a.getId() %>" >
    <% } %>
    
<input type="hidden" name="returnUrl" value="<%= URLEncoder.encode(returnUrl,"UTF-8") %>" >
   <input type="submit" name='action' value="modify"><br>
</form>


