<%@page import="java.net.URL"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.cottagesystems.albumserver.*, java.util.*, java.io.File, java.net.URLEncoder " %> 

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
    <style type="text/css">
        <!--
   img { border: none; }
        -->
    </style>
</head>
<body>
    <%
    
    String albumString= request.getParameter("album");
    if ( "".equals(albumString) ) albumString= null;
    
    Album album=null;
    if ( albumString!=null ) album= Album.createFromDirectory(new File( Configuration.getImageDatabaseRoot(), albumString) );
    
    out.println( "<body>" );
    String id= request.getParameter("image");
    if ( id==null ) id= request.getParameter("id");
    
    if ( id!=null && id.equals("") ) id=null;
    
    Negative image= id==null ? null : new Negative( id );
    Media media= Media.createMedia(id);
    
    String prevLink=null, nextLink=null;
    String prevImage="";
    String prevGif="noPrevious.png";
    String nextImage="";
    String nextGif="noNext.png";

    AccessBean access= (AccessBean)session.getAttribute("access");    
    boolean isAuthor= ( access!=null && access.hasRole( AccessBean.ROLE_AUTHOR ) );
            
    if (image!=null ) {
        
        List<Media> images= album.getContents();
        
        int i= images.indexOf(media);
        
        int previousMedia= i-1;
        while ( previousMedia>=0 ) {
            Media m0= images.get(previousMedia);
            HideCapability hideCapability= (HideCapability) m0.getCapability( Capability.HIDE.getClass(), access );
            if ( isAuthor || hideCapability==null || !hideCapability.isHidden(m0) ) {
                prevImage= m0.getURL();
                prevGif= "previous.png";
                prevLink= "<a href=\"AlbumServerContent0.jsp?&id="+m0.getId()+"&album="+album.getId()+"\">";
                break;
            }
            previousMedia=previousMedia-1;
        }
        
        int nextMedia= i+1;
        while ( nextMedia<images.size() ) {
            Media m0= images.get(nextMedia);
            HideCapability hideCapability= (HideCapability) m0.getCapability( Capability.HIDE.getClass(), access );
            if ( isAuthor || hideCapability==null || !hideCapability.isHidden(m0) ) {
                nextImage= m0.getURL();
                nextGif= "next.png";
                nextLink= "<a href=\"AlbumServerContent0.jsp?frame=content&id="+m0.getId()+"&album="+album.getId()+"\">";
                break;
            }
            nextMedia=nextMedia+1;
        }
    }
    
    String rotateString= request.getParameter("rotate");
    int rotate=  ( rotateString==null ) ? 0 : Integer.parseInt(rotateString);
    rotate= rotate % 360 ;
    
    String printString= request.getParameter("print");
    boolean print= printString!=null;
    
    if ( print ) {
        out.println( "<script language=javascript>" );
        out.println( "  alert( \"please wait for high-resolution version\" ); " );
        out.println( "</script>" );
    }
    
    if ( media==null ) {
        out.println( "<span style=\"color:grey\">" );
        if ( album==null ) {
            out.println( "<i>select an album from the list on the left.</i>");
            out.println( "<p><em><a href='about.jsp'>about</a> this server</em><br>");
            out.println( "<em><a href='Status.jsp'>configuration</a> of this server</em></p>");
        } else {
            out.println( "<i>select an image from the list on the left</i>");
            out.println( "<br><br>" );
            out.println( "<p>Album Notes:<p>");
            String [] meta= album.getNotes();
            for ( String i: meta ) out.println( ""+i+"<br>" );
            
            if ( access.hasRole(AccessBean.ROLE_AUTHOR) ) {
                String returnUrl= "AlbumServerContent0.jsp?album="+album.getId();
                out.println( "<a align=left href=\"AlbumServerAdmin0.jsp?album="+album.getId()+"&returnUrl="+URLEncoder.encode(returnUrl,"UTF-8")+"\">edit</a> " );
            }
        }
        out.println( "</span>");
        
        
    } else {

        Media m= media;
        HideCapability hideCapability= (HideCapability) m.getCapability( Capability.HIDE.getClass(), access );
        boolean doHide= true;
        if ( hideCapability!=null ) {
            boolean reallyDoHide= false;
            if ( hideCapability.isHidden(m) && access==AccessBean.PUBLIC ) reallyDoHide= true;
            if ( hideCapability.isHidden(m) && access==AccessBean.LIMITED_PUBLIC ) reallyDoHide= true;
            if ( hideCapability.isHidden(m) && access==AccessBean.UIPUBLIC ) reallyDoHide= true;
            if ( hideCapability.isHidden(m) && access==AccessBean.CFUPUBLIC ) reallyDoHide= true;
            doHide= reallyDoHide;
        } else {
            doHide= false;
        }
        
        if ( doHide ) {
            out.println( "HIDDEN..." );
        } else {

            out.println( "<table>" );
            if ( !print ) {
                out.println( "<tr>" );
                out.println("<td >");
                if ( prevLink!=null ) out.print( prevLink );
                out.print("<img alt=\"previous\" src=\"userInterface/"+prevGif+"\">" );
                if ( prevLink!=null ) out.println("</a>");
                if ( nextLink!=null ) out.print( nextLink );
                out.print( "<img alt=\"next\" src=\"userInterface/"+nextGif+"\">" );
                if ( nextLink!=null ) out.println("</a>");

                RotateCapability rotateCapability= (RotateCapability)media.getCapability( Capability.ROTATE.getClass(), access );
                if ( rotateCapability!=null ) {
                    rotateCapability.setAlbum(album);
                    rotateCapability.setRotate(rotate);
                    rotateCapability.setCCW(true);
                    out.println( rotateCapability.getHtmlPresentor(media,access) );
                    rotateCapability.setCCW(false);
                    out.println( rotateCapability.getHtmlPresentor(media,access) );
                }
                
                out.println( "</td>");
                out.println( "<td width=537>" );
                out.println( "<p>"+album.getId()+"&gt;<b>"+media.getId().substring(album.getId().length()+1)+"</b>" );
                //if ( rotate!=0 ) out.println( "(rotate="+rotate+")" );
                out.println( "</td>");
                out.println("</tr>");
            }

            String thisUrl= media.getURL( "&size=800&rotate="+rotate );

            if ( !print ) {
                out.println( "<tr><td valign=top colspan=2 width=640>");
                out.println( "<form name='viewmap' method='post' action='#'>" );
                out.println( thisUrl );
                out.println( "</form>");
            } else {
                out.println( "<tr><td width=640 valign=top colspan=2>");
                out.println( thisUrl );
            }
            out.println( "</td></tr>" );

            if ( !print ) {
                out.println( "<tr><td colspan=2 valign=top align=right><span style=\"color:grey \">" );

                PrintCapability printCapability= (PrintCapability)media.getCapability( Capability.PRINT.getClass(), access );
                if ( printCapability!=null ) {
                    printCapability.setAlbum( album );
                    printCapability.setRotate(rotate);
                    out.println( printCapability.getHtmlPresentor(media, access) );
                }

                Capability download= media.getCapability( Capability.DOWNLOAD_RAW.getClass(), access );
                if ( download!=null ) {
                    out.println( download.getHtmlPresentor(media, access) );
                }

                Capability ipod= media.getCapability( IpodVideoCapability.INSTANCE.getClass(), access );
                if ( ipod!=null ) {
                    out.println( ipod.getHtmlPresentor(media, access) );
                }

                Capability swf= media.getCapability( SwfVideoCapability.INSTANCE.getClass(), access );
                if ( swf!=null ) {
                    out.println( swf.getHtmlPresentor(media, access) );
                }
                
                Capability git= media.getCapability( GitCapability.INSTANCE.getClass(), access );
                if ( git!=null ) {
                    out.println( git.getHtmlPresentor(media, access) );
                }                

                DownloadModifiedCapability dmc= (DownloadModifiedCapability)media.getCapability( Capability.DOWNLOAD_MODIFIED.getClass(), access );
                if ( dmc!=null ) {
                    dmc.setRotate(rotate);
                    out.println( dmc.getHtmlPresentor(media, access) );
                }
                                        
                Date stamp= media.getTimeStamp( );
                if ( stamp!=null ) { out.println( stamp.toString() ); }

                out.println("<br>");

                //Capability orient= media.getCapability( Capability.ORIENTATION.getClass(), access );
                //if ( orient!=null ) {
                //    out.println( orient.getHtmlPresentor(media, access) );
                //}

                if ( access!=null && access.hasRole( AccessBean.ROLE_AUTHOR ) ) {
                    String returnUrl= "AlbumServerContent0.jsp?id="+media.getId()+"&album="+album.getId()+"&rotate="+rotate;
                    out.println( "<a align=left href=\"AlbumServerAdmin0.jsp?id="+media.getId()+"&returnUrl="+URLEncoder.encode(returnUrl,"UTF-8")+"\">edit</a> " );

                    Capability like= media.getCapability( Capability.LIKE_DISLIKE.getClass(), access );
                    if ( like!=null ) {
                        out.println( like.getHtmlPresentor(media, access) );
                    }

                    Capability hide= media.getCapability( Capability.HIDE.getClass(), access );
                    if ( hide!=null ) {
                        out.println( hide.getHtmlPresentor(media, access) );
                    }
                }

                LocationCapability locationCapability= (LocationCapability) media.getCapability( LocationCapability.class, access );
                if ( locationCapability!=null ) {
                    out.println( locationCapability.getHtmlPresentor(media,access) );
                }
                
                out.println( "</span></td></tr>\n" );

            }
            out.println( "<tr><td colspan=2 valign=top>\n" );

            CopyToClipboardCapability copyToClip= (CopyToClipboardCapability) media.getCapability( Capability.COPY_TO_CLIPBOARD.getClass(), access );
            if ( copyToClip!=null ) {
                if ( thisUrl.startsWith("<image" ) ) {
                    thisUrl= "PhotoServer?image="+media.getId()+"&size=800&rotate="+rotate;
                }
                copyToClip.setUrl(thisUrl);
                out.println( copyToClip.getHtmlPresentor(media, access) );
            }

            out.println("<br>");

            // check meta data
            String[] notes= media.getNotes();
            if ( notes!=null ) {
                for ( int i=0; i<notes.length; i++ ) out.println(notes[i]+"<br>");
            }

            out.println( "</td></tr>\n" );

            out.println( "</table>\n");
            out.println( "" );


            if ( print ) {
                out.println( "<script language=javascript>" );
                out.println( "  window.print(); " );
                out.println( "</script>" );
            }
        }
    }
    out.println( "</body>" );
    
    %>
    
    <!-- <DIV ID="rubberBand" 
          style="border: 1px solid #ff0000; position: absolute; visibility: hidden; width: 0px; height: 0px; ">
        </DIV>
        <script src="rubberBand.js"> -->
            
    <span id="log"></span>
    
</body>
</html>
