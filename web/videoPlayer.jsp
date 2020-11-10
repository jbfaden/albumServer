<%-- 
    Document   : videoPlayer
    Created on : Nov 9, 2020, 6:52:58 AM
    Author     : jbf
--%>

<%@page import="com.cottagesystems.albumserver.MediaServer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%
        String id= request.getParameter("id");
        %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Video Player</title>
    </head>
    <body>
        <h1></h1>
        <%
            String mime= MediaServer.mimeForExt(id);
            %>
        <video width="640" height="480" controls poster="PhotoServer?id=<%=id%>" >
            <source src="MediaServer?id=<%=id%>" type=<%=mime%> >
        </video>
  </body>
</html>
    </body>
</html>
