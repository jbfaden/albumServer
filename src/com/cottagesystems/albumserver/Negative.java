/*
 * Negative.java
 *
 * Created on January 22, 2007, 9:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author jbf
 */
public class Negative extends Media {
    
    public static final BufferedImage moment;
    static {
        moment= new BufferedImage( 400,300, BufferedImage.TYPE_INT_RGB );
        for ( int i=0; i<400; i++ ) {
            for ( int j=0; j<300; j++ ) {
                int g= (int)( Math.random() * 100 + 100 );
                moment.setRGB( i, j, g*256*256 + g*256 + g );
            }
        }
        Graphics2D g= (Graphics2D) moment.getGraphics();
        g.setFont( Font.decode("sans-48") );
        g.setColor( Color.WHITE );
        g.drawString( "Calculating...", 3, 100 );
        g.setColor( Color.DARK_GRAY );
        g.drawString( "Calculating...", 5, 103 );
        g.setColor( Color.WHITE );
        g.drawString( "(hit reload)", 3, 156 );
        g.setColor( Color.DARK_GRAY );
        g.drawString( "(hit reload)", 5, 159 );
    }


    /** Creates a new instance of Negative */
    public Negative( String id ) {
        super(id);
        capabilities.put( Capability.DOWNLOAD_MODIFIED.getClass(), Capability.DOWNLOAD_MODIFIED );
        capabilities.put( Capability.ROTATE.getClass(), Capability.ROTATE );
        capabilities.put( Capability.PRINT.getClass(), Capability.PRINT );
        if ( id.endsWith("JPG") ||id.endsWith("jpg") ) {
            capabilities.put( Capability.ORIENTATION.getClass(), Capability.ORIENTATION );
        }
        capabilities.put( Capability.HIDE.getClass(), Capability.HIDE );
        capabilities.put( LocationCapability.class, new LocationCapability() );
        //capabilities.put( Capability.COPY_TO_CLIPBOARD.getClass(), Capability.COPY_TO_CLIPBOARD );
    }
    
    public String getIconURL( ) {
        return "<image src=\"PhotoServer?id="+id+"&icon=1&size=120\">";
    }

    public String getURL() {
        return "<input type='image' src=\"PhotoServer?id="+id+"&size=800\">";
    }
    
    public String getURL( String parms ) {
        return "<image src=\"PhotoServer?image="+id+"&"+parms+"\">";
        /*return "<applet archive='AlbumServerClient.jar' width=800 height=800 "
                +"code='albumserverclient.ContentApplet' >\n"
                +"  <param name='image' value='"+id+"'>\n"
                +"  <param name='size' value='800'>\n"
                +"  <param name='rotate' value='0'>\n"
                +"</applet>\n";*/
    }
    
    /** 
     * return an ImageIcon of roughly size.
     */
    public Image getImageIcon(  ) throws IOException {
        BufferedImage image= ImageIO.read( new File( Configuration.getImageDatabaseRoot(), id ) );
        return image;
    }
    
    public Image getImage() throws IOException {
        BufferedImage image= ImageIO.read( new File( Configuration.getImageDatabaseRoot(), id ) );
        return image;
    }

    public Image getReducedImage() throws IOException {
        final File f= new File( Configuration.getCacheRoot(), "half/"+id );
        if ( !f.exists() ) {
            Runnable run= new Runnable() {
                public void run() {
                    try {
                        BufferedImage image = null;
                        String format = Util.getExt(id);
                        image = ImageIO.read(new File(Configuration.getImageDatabaseRoot(), id));
                        final int w = image.getWidth();
                        final int h = image.getHeight();
                        BufferedImage half = new BufferedImage(w / 2, h / 2, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = (Graphics2D) half.getGraphics();
                        g.drawImage(image.getScaledInstance(w / 2, h / 2, Image.SCALE_AREA_AVERAGING), null, null);
                        f.getParentFile().mkdirs();
                        ImageIO.write(half, format, f);
                        image = half;
                    } catch (IOException ex) {
                        Logger.getLogger(Negative.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            WorkQueue.getInstance().execute(run);
            return moment;
            
        } else {
            BufferedImage image= null;
            image= ImageIO.read( f );
            return image;
        }
        
    }
}
