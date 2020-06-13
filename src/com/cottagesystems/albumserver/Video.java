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
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

    public String getIconURL() {
        return "<image src='PhotoServer?id=" + id + "&icon=1&size=120'>";
    }

    public String getURL() {
        return "<input type='image' src=\"PhotoServer?id=" + id + "\">";
    }

    @Override
    public Image getImageIcon() throws IOException {
        String ss = id.replaceAll("AVI", "THM");
        ss = ss.replaceAll("avi", "thm");
        ss= ss.replace("mpg", "thm");
        
        File f = new File(Configuration.getImageDatabaseRoot(), ss);

        boolean doDecorate = true;

        File thumb = new File(Configuration.getCacheRoot(), "convert/" + id + ".png");
        
        if (!thumb.exists()) {
            doDecorate = false;

            File video = new File(Configuration.getImageDatabaseRoot(), id);
            //File mp4 = new File(Configuration.getImageDatabaseRoot(), id.replaceAll(".avi", ".mp4"));
            if (!thumb.exists()) {
                thumb.getParentFile().mkdirs();
                //System.err.println("ffmpeg -i " + video + " " + thumb);
                //Runtime.getRuntime().exec("ffmpeg -i " + video + " " + thumb);
                //String s= "ffmpeg -i " + video + " " + thumb;
                String s= "totem-video-thumbnailer -s 640 " + video + " " + thumb;
                System.err.println(s);
                Runtime.getRuntime().exec(s);

                //Runtime.getRuntime().exec("ffmpeg -i "+ video + " "+mp4 + " &" );
            }
            f = thumb;
            doDecorate = false; // totem-video-thumbnailer decorates.
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
            Stroke s= new BasicStroke( step/7.f );
            g.setStroke(s);
            g.drawLine(1, 0, 1, image.getHeight());
            g.drawLine(step, 0, step, image.getHeight());
            for (int i = 0; i < image.getHeight(); i += step-1) {
                g.drawLine(0, i, step, i);
            }
        }
        return image;
    }

    public Image getImage() throws IOException {
        return getImageIcon();
    }
}
