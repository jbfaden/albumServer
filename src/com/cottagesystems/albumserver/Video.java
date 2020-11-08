/*
 * Video.java
 *
 * Created on February 10, 2007, 3:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.cottagesystems.albumserver;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Video clip produced by camera.  Linux's totem-video-thumbnailer is
 * used to produce thumbnails and image previews.
 * 
 * On an old Ubuntu system, I needed to run "sudo apt-get install gstreamer0.10-ffmpeg"
 * to get this to work.
 * @author jbf
 */
public class Video extends Media {

    public Video(String id) {
        super(id);
        File mp4 = new File(Configuration.getImageDatabaseRoot(), id.replaceAll("AVI", "mp4"));
        if (mp4.exists()) {
            capabilities.put(IpodVideoCapability.INSTANCE.getClass(), IpodVideoCapability.INSTANCE);
        }
        //File swf = new File(Configuration.getImageDatabaseRoot(), id.replaceAll("AVI", "swf"));
        //if (swf.exists()) {
        //    capabilities.put(SwfVideoCapability.INSTANCE.getClass(), SwfVideoCapability.INSTANCE);
        //}
        capabilities.put(Capability.DOWNLOAD_RAW.getClass(), Capability.DOWNLOAD_RAW);
        //capabilities.put( Capability.COPY_TO_CLIPBOARD.getClass(), Capability.COPY_TO_CLIPBOARD );
    }

    @Override
    public String getIconURL() {
        return "<image src='PhotoServer?id=" + id + "&icon=1&size=120'>";
    }

    @Override
    public String getURL() {
        return "<input type='image' src=\"PhotoServer?id=" + id + "\">";
    }

    @Override
    public Image getImageIcon() throws IOException {
        
        File f;

        boolean doDecorate;

        File convert=  new File(Configuration.getCacheRoot(), "convert/" );
        if ( !convert.exists() ) {
            if ( !convert.mkdirs() ) {
                throw new IOException("unable to create folder "+convert);
            }
        }
        
        File thumb = new File(Configuration.getCacheRoot(), "convert/" + id + ".png");
        
        if (!thumb.exists()) {
            doDecorate = false;

            File video = new File(Configuration.getImageDatabaseRoot(), id);
            //File mp4 = new File(Configuration.getImageDatabaseRoot(), id.replaceAll(".avi", ".mp4"));
            if (!thumb.exists()) {
                try {
                    thumb.getParentFile().mkdirs();
                    //System.err.println("ffmpeg -i " + video + " " + thumb);
                    //Runtime.getRuntime().exec("ffmpeg -i " + video + " " + thumb);
                    //String s= "ffmpeg -i " + video + " " + thumb;
                    String s= "totem-video-thumbnailer -s 640 " + video + " " + thumb;
                    System.err.println(s);
                    Process p= Runtime.getRuntime().exec(s);
                    if ( !p.waitFor( 30, TimeUnit.SECONDS ) ) {
                        throw new IllegalArgumentException("process failed to return");
                    }
                    
                    //Runtime.getRuntime().exec("ffmpeg -i "+ video + " "+mp4 + " &" );
                } catch (InterruptedException ex) {
                    Logger.getLogger(Video.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            f = thumb;
            doDecorate = true; // totem-video-thumbnailer decorates.
        } else {
            f= thumb;
            doDecorate = false; // assume thumb has decorations.
            
        }

        BufferedImage image = null; 
        
        try {
            image= ImageIO.read(f);
        } catch ( IOException ex ) {
            System.err.println(ex);
        }

        if ( image==null ) {
            return Negative.moment;
        }
        
        if (doDecorate) {
            Graphics2D g = (Graphics2D) image.getGraphics();
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2.0f));
            int step= image.getWidth()*7/150;
            for (int i = 0; i < image.getHeight(); i += step-1) {
                g.fill( new RoundRectangle2D.Double( 5, i+5, step-10, step-10, 3, 3 ) );
            }
        }
        return image;
    }

    @Override
    public Image getImage() throws IOException {
        return getImageIcon();
    }
}
