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
    
    <body>
        
        <table>
            <%    
            
            String albumString= request.getParameter("album");
            if ( albumString.equals("") ) albumString=null;
            
            if ( albumString!=null ) { // show the media within an album.
                
                Album album;
                
                if ( albumString.equals("searchAlbum" ) ) {
                    album= (Album) session.getAttribute( "searchAlbum" );
                } else {
                    album= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(),albumString) );
                }
                
                List<Media> list= album.getContents();
                
                out.println( "<tr><td colspan=2><a href=\"AlbumServer0.jsp\" target=\"_top\">");
                out.println( "albums</a>&gt;<b>"+album.getLabel());
                out.println( "</b></td></tr>" );

                AccessBean access= (AccessBean)session.getAttribute("access" );

                for ( Media m:list ) {
                    String image= m.getIconURL();
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
                    
                }

	        out.println( "<tr><td colspan=2><a href=\"refresh.jsp?album="+album.getId()+"\">(refresh)</a>");
	        out.println( "<tr><td colspan=2><a href=\"SearchForm.jsp\" target=\"_top\">(search)</a>");
                out.println( "<tr><td colspan=2><a href=\"AlbumServer0.jsp\" target=\"_top\">albums</a>><b>"+album.getLabel()+"</b></td></tr>" );

            } else {
                
                if ( !( new File( Configuration.getImageDatabaseRoot() ).exists() ) ) {
                    out.println("albums database doesn't exist!<br>Expected to find folders containing images at<br>"+Configuration.getImageDatabaseRoot() );

                } else {

                    List<Album> albums= Album.getAlbums( (AccessBean)session.getAttribute("access" ) );

                    out.println( "<tr><td colspan=2><a href=\"SearchForm.jsp\" target=\"_top\">(search)</a>");
                    for ( Album a:albums ) {
                        String link= "<a href=\"AlbumServer0.jsp?album="+a.getId()+"\" target=\"_top\">"+a.getLabel()+"</a>";
                        out.println( "<tr><td>"+link+"</td></tr>");
                    }
                    out.println( "<tr><td colspan=2><a href=\"SearchForm.jsp\" target=\"_top\">(search)</a>");
               }
                
            }
            
            %>
        </table>
    </body>
    
</html>
