<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.cottagesystems.albumserver.*, java.io.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <style type="text/css">
            <!--
   img { border: none; }
        -->
        </style>
        
        <title>Album Server</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <meta name="ROBOTS" content="NOFOLLOW">
    </head>
    
    <jsp:useBean id="loginBean" class="com.cottagesystems.albumserver.LoginBean" scope="request">
        
    </jsp:useBean>

    <%
        String album= request.getParameter("album");
        if ( album==null ) album="";
        String id= request.getParameter("id");
        if ( id==null ) id= request.getParameter("image");
        if ( id==null ) id="";
    %>
    <frameset cols="20%,*" frameborder="YES" framespacing="1">
        <frameset rows="30,*" frameborder="YES" framespacing="1">
            <frame src="Login.jsp">
                <!-- here -->
<%     
             boolean hasIndex= true;
             File f= new File( Configuration.getImageDatabaseRoot(), "cache/"+ album +".html" );
             if ( !f.exists() ) hasIndex= false;
             
             if ( !hasIndex || album==null || album.equals("") ) {   %>
             <frame src="AlbumServerDirectory0.jsp?album=<%=album%>&id=<%=id%>" name="directory" >
<%     } else {                     %>
             <frame src="MediaServer.html?id=cache/<%=album%>.html" name="directory" >
<%     }                                %>             
        </frameSet>
        <frame src="AlbumServerContent0.jsp?album=<%=album%>&id=<%=id%>" name="content" >
    </frameset><noframes></noframes><body>
        
    </body></noframes>
    <%
          AccessBean access=(AccessBean) session.getAttribute("access");
          if ( access==null || request.getParameter("access")!=null ) {
               access= loginBean.getAccessBean(request);
               session.setAttribute( "access", access );
          }
          PreferencesBean preferences= (PreferencesBean) session.getAttribute("preferences");
          if ( preferences==null ) session.setAttribute( "preferences", new PreferencesBean() );
    %>
</html>
