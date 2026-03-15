/*
 * PhotoServer.java
 *
 * Created on January 21, 2007, 10:53 AM
 */

package com.cottagesystems.albumserver;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author jbf
 * @version
 */
public class PhotoServer extends HttpServlet {
    
    private void handleException( String msg ) {
        throw new RuntimeException( msg );
    }
    
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        System.setProperty("java.awt.headless","true");
        
        response.setContentType("image/jpg");
        
        // http://www.sarahandjeremy.net/~jbf/family/photoServer.php?id=/20070120_g7/IMG_0069_2.JPG&width=640&rotate=0
        String id= request.getParameter("image");
        if ( id==null ) id= request.getParameter("id");
        
        int width1= request.getParameter("width")==null ? 0 : Integer.parseInt( request.getParameter("width") );
        int height1= request.getParameter("height")==null ? 0 : Integer.parseInt( request.getParameter("height") );
        int size1= request.getParameter("size")==null ? 0 :  Integer.parseInt( request.getParameter("size") );
        
        String imageRotate= request.getParameter("rotate");
        if ( imageRotate==null ) imageRotate= "0";
        
        String icon= request.getParameter("icon");
        
        // look for cached icon 
        if ( icon!=null ) {
            File f= new File( Configuration.getCacheRoot() + "icons/" + size1 + "/" + id +".jpg" );
            if ( f.exists() ) {
                response.setContentLength( (int)f.length() );
                response.setHeader("Content-Disposition", "inline;filename="+f.getName() );
                FileInputStream fin= new FileInputStream(f);
                Util.copy( fin, response.getOutputStream() ) ;
                fin.close();
                
                return;
            }
        }
        
        BufferedImage image;
        int srcScale= 1;  // scaling from original of the source image.  2=half the size

        Media m;
        if ( icon!=null ) {
            m= Media.createMedia(id);
            image= (BufferedImage) m.getReducedImage();
            if ( image==null ) {
                image= (BufferedImage) m.getImageIcon();
            } else {
                srcScale= 2;
            }
        } else {
            m= Media.createMedia(id);
            image= (BufferedImage) m.getReducedImage();
            if ( image==null ) {
                image= (BufferedImage) m.getImage();
            } else {
                srcScale= 2;
            }
            
        }
        
        int rot= Integer.parseInt(imageRotate);
        if ( rot % 90 != 0 ) handleException( "Rotation "+imageRotate+". not allowed.  Allowed values: 0, 90, 180, 270");

        OrientationCapability orient= (OrientationCapability) m.getCapability( Capability.ORIENTATION.getClass() , null);
        if ( orient!=null ) {
            String s= orient.load(m);
            if ( s!=null && s.startsWith("rot") ) {
                rot-= Integer.parseInt(s.substring(3));
            }
        }
        
        if ( rot < 0 ) rot= rot % 360 + 360;
        if ( rot > 360 ) rot= rot % 360;
        
        Rectangle cropRectangle; // crop in the source image space, which might be scale of original
        if ( request.getParameter("crop")!=null ) {
            Rectangle r= new Rectangle();
            String rs= request.getParameter("crop");
            if ( rs.startsWith("[") && rs.endsWith("]")) {
                String[] ss= rs.substring(1,rs.length()-1).split(",");
                r.x= Integer.parseInt( ss[0] ) / srcScale;
                r.y= Integer.parseInt( ss[1] ) / srcScale;
                r.width= Integer.parseInt( ss[2] ) / srcScale;
                r.height= Integer.parseInt( ss[3] ) / srcScale;
            }
            cropRectangle= r;
        } else {
            cropRectangle= new Rectangle( 0, 0, image.getWidth(),  image.getHeight() );
        }
        
        int width0= cropRectangle.width;
        int height0= cropRectangle.height;
        
        // calculate output image dimensions.
        
        double aspect= width0 / ( double )height0 ;
        
        if ( width1==0 && height1==0 ) {
            width1= width0 * srcScale;
            height1= height0 * srcScale;
        } else if ( width1==0 && height1!=0 ) {
            width1= (int)(height1 * aspect );
        } else if ( width1!=0 && height1==0 ) {
            height1= (int)(width1 / aspect);
        } else if ( width1!=0 && height1!=0 ) {
            height1= (int) (width1 / aspect);  // can't change aspect ratio even if both specified
        }

        double scalex, scaley;
        
        if ( size1>0 ) {
            scalex= size1 / Math.sqrt( width0*width0 + height0*height0 ) ;
            scaley= scalex;
            width1= (int)(width0 * scalex);
            height1= (int)(height0 * scaley);
        } else {
            scalex= width1 / (double)width0;
            scaley= height1 / (double)height0;
        }
        
        if ( rot==90 || rot==270 ) {
            int t= width1;
            width1= height1;
            height1= t;
        }
        
        double drot= Math.toRadians(rot);
        int w = image.getWidth();
        int h = image.getHeight();
        double sin = Math.abs(Math.sin(drot));
        double cos = Math.abs(Math.cos(drot));

        if ( width1>w && srcScale>1 ) {
            image= (BufferedImage) m.getImage();
            w= image.getWidth();
            h= image.getHeight();
            scalex= scalex / srcScale;
            scaley= scaley / srcScale;
            cropRectangle.x *= srcScale;
            cropRectangle.y *= srcScale;
            cropRectangle.width *= srcScale;
            cropRectangle.height *= srcScale;
            srcScale= 1;
        }

        BufferedImage newImage= new BufferedImage( width1, height1, BufferedImage.TYPE_INT_RGB );

        Graphics2D g= (Graphics2D)newImage.getGraphics();

        g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );

        //Graphics2D g2= (Graphics2D) image.getGraphics(); // used to verify transformation
        //Rectangle drawRect= new Rectangle( 0, 0, width1, height1 );
        
        AffineTransform atInv= new AffineTransform();
        atInv.translate( cropRectangle.x, cropRectangle.y );
        
        atInv.rotate( drot, cropRectangle.height/2, cropRectangle.width/2  );
        
        int w0= cropRectangle.width;
        int h0= cropRectangle.height;
        
        int neww0 = (int)Math.round(w0*cos+h0*sin);
        int newh0 = (int)Math.round(h0*cos+w0*sin);
        
        // TODO: look for unifying code that allows for arbitrary rotation.
        if ( rot==0 ) {
            atInv.translate( (neww0-w0)/2, (newh0-h0)/2); // okay for rot=0
        } else if ( rot==90 ) {
            atInv.translate( (neww0-w0)/2, (newh0-h0)/2+cropRectangle.height-cropRectangle.width );   // okay for rot=270
        } else if ( rot==270 ) {
            atInv.translate( (newh0-h0)/2, -(neww0-w0)/2 ); // okay for rot=90
        } else if ( rot==180 ) {
            atInv.translate( (neww0-w0)/2+cropRectangle.height-cropRectangle.width , (newh0-h0)/2-cropRectangle.height+cropRectangle.width  );   // okay for rot=180
        }

        Image imagge= image;

        boolean aa= drot==0;
        if ( !aa ) {
            atInv.scale( 1/scalex, 1/scaley );
        } else {
            //imagge= image.getScaledInstance( (int)(cropRectangle.width), (int)(cropRectangle.height), Image.SCALE_AREA_AVERAGING );
            imagge= image.getScaledInstance( (int)(w*scalex), (int)(h*scaley), Image.SCALE_AREA_AVERAGING );
        }

        /*
         //This code was to verify the transform.
         g2.setColor( Color.GREEN );
         g2.setStroke( new BasicStroke( 4.0f ) );
        g2.draw( cropRectangle );
         
        g2.transform(atInv);
        g2.setColor( Color.RED );
        g2.setStroke( new BasicStroke( 4.0f ) );
        g2.draw( drawRect );
         
        g2.dispose();
         
         */
        try {
            g.setTransform( atInv.createInverse() );
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
        }
        
        //g.drawImage(image, null, null );
        g.drawImage( imagge, null, null );

        g.dispose();

        int i= id.lastIndexOf("/");
        String extName= i==-1 ? id : id.substring(i+1);
        response.setHeader("Content-Disposition", "inline;filename="+extName );
        if ( image==Negative.moment ) {
            response.setHeader("Expires", "-1" );
        }

        // create cache icon
        if ( icon!=null && !(image==Negative.moment) ) {
            File f= new File( Configuration.getCacheRoot() + "icons/" + size1 + "/" + id +".jpg" );
            File df= f.getParentFile();
            df.mkdirs();
            
            FileOutputStream fout= new FileOutputStream(f);
            ImageIO.write( newImage, "jpeg", fout );
            fout.close();
            
            FileInputStream fin= new FileInputStream(f);
            Util.copy( fin, response.getOutputStream() );
            fin.close();
            return;
            
        } else {
            ImageIO.write( newImage, "jpeg", response.getOutputStream() );
        }

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
