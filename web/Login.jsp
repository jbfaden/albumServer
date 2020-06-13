<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.cottagesystems.albumserver.AccessBean" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
    <jsp:useBean id="formHandler" class="com.cottagesystems.albumserver.LoginBean" scope="request">
        <jsp:setProperty name="formHandler" property="*"/>
    </jsp:useBean>

<%
if ( "logout".equals( request.getParameter("action") ) ) {
    session.setAttribute("access",null);
    response.sendRedirect(  response.encodeURL( "Login.jsp?reload=true" ) ) ;
    
} else if ( "login".equals( request.getParameter("action") ) ) {
%>

<script "JavaScript1.2" TYPE="text/javascript">
        function login() {
        window.opener.document.forms[0].username.value = document.forms[0].username.value;
        window.opener.document.forms[0].password.value = document.forms[0].password.value;
        window.opener.document.forms[0].submit();
        window.opener.focus();
        window.close();
        }
        </script>
<html><body>
        <form method='post' onsubmit='login()'>
            <table style="margin:-4px; padding:0px;">
                <tr>
                    <td width=300>
                        <i>Username: </i><input name='username' size=10><br>
                        <i>Password: </i><input name='password' type='password' size=10><br>
                    </td>
                    <td width=30>
                        <input type="submit" value="login" >
                    </td>
                </tr>
            </table>
        </form>
</body></html>
<%
} else if ( "authenticate".equals(request.getParameter("action") ) ) {
%>
<%
    session.setAttribute( "access", formHandler.getAccessBean(request) );
    response.sendRedirect( response.encodeURL( "Login.jsp?reload=true" ) );
} else {
        AccessBean bean;
        if ( session.getId()=="" ) {
            bean=formHandler.getAccessBean(request);
        } else {
            bean= (AccessBean)session.getAttribute("access");
            if ( bean==null ) {
                bean= formHandler.getAccessBean(request);
            }
        }
        if ( bean.hasRole(AccessBean.ROLE_AUTHOR) ) {
%>
<html>
    <body>
        <i><%= bean.getUsername() %>
            (<a href="Login.jsp?action=logout">logout</a>)
        </i>
<%
        } else {
%>
<html>
    <body>
<i>
<%
if ( bean==AccessBean.PUBLIC ) {
    out.println("not logged in");
} else {
    out.println("" + bean.getUsername() );
}
 %>
(<a href="#" onclick="window.open('Login.jsp?action=login','login','width=230,height=100',1);">log in</a>)</i>
<FORM METHOD="POST" ACTION="Login.jsp?action=authenticate">
     <INPUT  NAME="username"  TYPE="hidden" >
     <INPUT  NAME="password"  TYPE="hidden" >
     <!-- <input type="submit" value="login" > -->
</FORM>
<!--<br>sessionId= "<? echo session_id(); ?>"-->
<% 
        }
        if ( "true".equals( request.getParameter("reload") ) ) {
%>
<script language="javascript">
    //window.frames['content'].location.reload(); 
    //window.frames['directory'].location.reload();

    <% //<!-- window.frames['content'].location.reload(); -->
    //<!-- window.frames['directory'].location.reload(); --> %>
</script>
<%
        }
 %>
       
        </html>
    </body>
 <%
    
}%>
