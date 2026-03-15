<%@page import="java.io.FileFilter"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.cottagesystems.albumserver.*, java.util.*, java.io.File" %>
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

<!-- 
This shows either the list of albums, or the media within an album.
-->
<html>
    <head>
        <!-- <meta http-equiv="refresh" content="5" /> -->
    </head>
        
    <body>
    <script>
function filterFunction() {
  var input, filter, ul, li, a, i;
  input = document.getElementById("myInput");
  filter = input.value.toUpperCase();
  div = document.getElementById("albumsTable");
  a = div.getElementsByTagName("a");
  for (i = 0; i < a.length; i++) {
    txtValue = a[i].textContent || a[i].innerText;
    if (txtValue.toUpperCase().indexOf(filter) > -1) {
      a[i].style.display = "";
    } else {
      a[i].style.display = "none";
    }
  }
}
</script>
        
        <%
            String albumString= request.getParameter("album");
            if ( albumString.equals("") ) albumString=null;

            if ( albumString==null ) {
                %>
                <input type="text" placeholder="Search.." id="myInput" onkeyup="filterFunction()">
                <%
            }
            boolean reload= false;
            boolean first20reload= false;
        %>
        
        
        <table id="albumsTable">
            <%    
                        
            if ( albumString!=null ) { // show the media within an album.
                
                Album album;
                
                if ( albumString.equals("searchAlbum" ) ) {
                    album= (Album) session.getAttribute( "searchAlbum" );
                } else {
                    album= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(),albumString) );
                }
                
                List<Media> list= album.getContents();
                
                out.println( "<tr><td colspan=2><a href=\"AlbumServer0.jsp\" target=\"_top\">");
                out.println( "albums</a>&gt;<b>");
                if ( album.getId().contains("/") ) {
                    String[] ss= album.getId().split("\\/");
                    String accum="";
                    for ( int i=0; i<ss.length-1; i++ ) {
                        out.print("<a href=\"refresh.jsp?album="+accum + "/"+ ss[i]+"\">");
                        out.print(ss[i]);
                        out.print("</a>/");
                    }
                    out.println(ss[ss.length-1]);
                } else {
                    out.println(album.getLabel());
                }
                out.println( "</b></td></tr>" );

                out.println( "<tr><td colspan=2><a href=\"refresh.jsp?album="+album.getId()+"\">(refresh)</a>");

                AccessBean access= (AccessBean)session.getAttribute("access" );

                int i=0;
                for ( Media m:list ) {
                    String image= m.getIconURL();
                    if ( image.contains("src=\"PhotoServer?id=") ) {
                        if ( m.getReducedImage()==Negative.moment ) {
                            reload= true;
                            if ( i<20 ) {
                                first20reload= true;
                            }
                        }
                    }
                    HideCapability hideCapability= (HideCapability) m.getCapability( Capability.HIDE.getClass(), access );
                    boolean hidden= false;
                    if ( hideCapability!=null ) {
                        if ( hideCapability.isHidden(m) && access==AccessBean.PUBLIC ) continue;
                        if ( hideCapability.isHidden(m) && access==AccessBean.LIMITED_PUBLIC ) continue;
                        if ( hideCapability.isHidden(m) && access==AccessBean.UIPUBLIC ) continue;
                        if ( hideCapability.isHidden(m) && access==AccessBean.CFUPUBLIC ) continue;
                        hidden= hideCapability.isHidden(m);
                    }
                    String l= "<a href=\"AlbumServerContent0.jsp?id="+m.getId()+"&album="+album.getId()+"\" target=content>";
                    image= l+image+"</a>";
                    String label= l+m.getLabel()+"</a>";
                    out.println( "<tr><td>\n"+image );
                    out.println( "</td><td>" );
                    if ( hidden ) {
                        out.println("&#10060;");
                    }
                    out.println( ""+label );
                    String [] notes= m.getNotes();
                    if ( notes.length>0 ) {
                        String shor= notes[0];
                        if ( shor.length()>40 ) shor= shor.substring(0,40)+"...";
                        out.println("\n<br><small>"+shor+"</small>");
                    }
                    out.println( "</td></tr>\n");
                    i=i+1;
                }
                
                if ( list.size()==0 ) { // whoops, it's actually a Box, which contains other albums.
                    List<Album> albums= Album.getAlbums( new Box( album.getId() ), (AccessBean)session.getAttribute("access" ) );                    
                    for ( Album a:albums ) {
                        String link= "<a href=\"AlbumServer0.jsp?album="+a.getId()+"\" target=\"_top\">"+a.getLabel()+"</a>";
                        out.println( "<tr><td>"+link+"</td></tr>");
                    }
                }

	        //out.println( "<tr><td colspan=2><a href=\"SearchForm.jsp\" target=\"_top\">(search)</a>");
                out.println( "<tr><td colspan=2><a href=\"AlbumServer0.jsp\" target=\"_top\">albums</a>><b>"+album.getLabel()+"</b></td></tr>\n" );

            } else {
                
                if ( !( new File( Configuration.getImageDatabaseRoot() ).exists() ) ) {
                    out.println("<br>albums database doesn't exist!<br>Expected to find folders containing images at<br>"+Configuration.getImageDatabaseRoot()+"<br>"
                            + "Webmaster, please see configuration at<br>" + Configuration.getHome() + "config.properties ." );

                } else {

                    List<Album> albums= Album.getAlbums( (AccessBean)session.getAttribute("access" ) );

                    //out.println( "<tr><td colspan=2><a href=\"SearchForm.jsp\" target=\"_top\">(search)</a>");
                    for ( Album a:albums ) {
                        String link= "<a href=\"AlbumServer0.jsp?album="+a.getId()+"\" target=\"_top\">"+a.getLabel()+"</a>";
                        out.println( "<tr><td>"+link+"</td></tr>");
                    }
                    //out.println( "<tr><td colspan=2><a href=\"SearchForm.jsp\" target=\"_top\">(search)</a>");
               }
                
            }
            
            %>
        </table>

        <%
            if ( reload ) {
                int milliseconds= first20reload ? 2000 : 10000;
                %>
                    <script language="javascript">
                        setTimeout(function () { location.reload(1); }, <%=milliseconds%>);
                    </script>
                <%
            }
        %>

    </body>
    
</html>
