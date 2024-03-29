/*
 * MediaServer.java
 *
 * Created on February 19, 2007, 8:41 AM
 */

package com.cottagesystems.albumserver;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author jbf
 * @version
 */
public class MediaServer extends HttpServlet {
   
    private static File reformatFile( String id ) {
        File f= new File( Configuration.getCacheRoot(), "reformat/"+id+".mp4" );
        if ( f.exists() ) {
            return f;
        } else {
            return null;
        }
    }
    
    public static String mimeForExt( String id ) {
        
        String ext= id.substring( id.lastIndexOf(".")+1 ).toLowerCase();
        
        File f= reformatFile( id );
        if ( f!=null ) {
            id= f.getName();
            ext= id.substring( id.lastIndexOf(".")+1 ).toLowerCase();
        }
        
        String mime;
        if ( ext.equals("mpg") ) {
            mime= "video/mpg";
        } else if ( ext.equals("avi") ) {
            mime= "video/avi";
        } else if ( ext.equals("mp4" ) ) {
            mime= "video/mp4";
        } else if (ext.equals("jpg") ) {
            mime= "image/jpg";
        } else if ( ext.equals("html") ) {
            mime= "text/html;charset=UTF-8";
        } else if ( ext.equals("wav" ) ) {
            mime= "audio/wav";
        } else {
            mime= "application";
        }
        return mime;
                
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String id= request.getParameter("id");
        
        if ( id.contains("..") ) throw new IllegalArgumentException("attempt to access area outside of image database.");
        
        String mime= mimeForExt(id);
        
        File f= reformatFile( id );
        if ( f==null ) f=new File( Configuration.getImageDatabaseRoot(), id ); // Sonatype lift okay, see check on line 69.
        
        if ( mime!=null ) response.setContentType( mime );
        response.setContentLength( (int)f.length() );
        
        int i= id.lastIndexOf("/");
        String extName= i==-1 ? id : id.substring(i+1);
        
        response.setContentLength( (int)f.length() );
        response.setHeader("Content-Disposition", "inline;filename="+extName );
        
        OutputStream out = response.getOutputStream();
        
        InputStream in= new FileInputStream( f );
        
        Util.copy( in, out );
        
        in.close();
        out.close();
    }
    
    
// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
// </editor-fold>


}
