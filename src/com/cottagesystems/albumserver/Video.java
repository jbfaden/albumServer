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
import java.awt.Font;
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
    
    private static final Logger logger= Logger.getLogger("albumServer");
    
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
        return "<img src=\"PhotoServer?id=" + id + "\">";
    }

//    private class StreamGobbler extends Thread {
//        InputStream is;
//
//        // reads everything from is until empty. 
//        StreamGobbler(InputStream is) {
//            this.is = is;
//        }
//
//        @Override
//        public void run() {
//            try {
//                InputStreamReader isr = new InputStreamReader(is);
//                BufferedReader br = new BufferedReader(isr);
//                String line=null;
//                while ( (line = br.readLine()) != null)
//                    System.err.println(line);    
//            } catch (IOException ioe) {
//                ioe.printStackTrace();  
//            }
//        }
//    }
    
    
    public static final BufferedImage noVideoThumbnailer;
    static {
        noVideoThumbnailer= new BufferedImage( 400,300, BufferedImage.TYPE_INT_RGB );
        for ( int i=0; i<400; i++ ) {
            for ( int j=0; j<300; j++ ) {
                int g= (int)( Math.random() * 100 + 100 );
                noVideoThumbnailer.setRGB( i, j, g*256*256 + g*256 + g );
            }
        }
        Graphics2D g= (Graphics2D) noVideoThumbnailer.getGraphics();
        g.setFont( Font.decode("sans-30") );
        g.setColor( Color.WHITE );
        g.drawString( "No Video Thumbnailer", 23, 100 );
        g.setColor( Color.DARK_GRAY );
        g.drawString( "No Video Thumbnailer", 25, 103 );
        g.setColor( Color.WHITE );
        g.drawString( "(image not available)", 23, 156 );
        g.setColor( Color.DARK_GRAY );
        g.drawString( "(image not available)", 25, 159 );

        int h= noVideoThumbnailer.getHeight();
        int w= noVideoThumbnailer.getWidth();
        
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,18,h);
        g.fillRect(w-18,0,18,h);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2.0f));
        int step= noVideoThumbnailer.getWidth()*7/150;
        for (int i = 0; i < h; i += step-1) {
            g.fill( new RoundRectangle2D.Double( 5, i+5, step-10, step-10, 3, 3 ) );
        }
        for (int i = 0; i < h; i += step-1) {
            g.fill( new RoundRectangle2D.Double( w-5-step+10, i+5, step-10, step-10, 3, 3 ) );
        }
                
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
                thumb.getParentFile().mkdirs();
                //System.err.println("ffmpeg -i " + video + " " + thumb);
                //Runtime.getRuntime().exec("ffmpeg -i " + video + " " + thumb);
                //String s= "ffmpeg -i " + video + " " + thumb;
                if ( Configuration.getVideoThumbnailer()==null ) {
                    return noVideoThumbnailer;
                }
                String s= Configuration.getVideoThumbnailer() + " " + video + " " + thumb;
                logger.fine(s);
                ProcessBuilder pb= new ProcessBuilder(s.split("\\s") );
                pb.environment().put( "DISPLAY", "localhost:2" );
                Process p= pb.start();
                //StreamGobbler ins= new StreamGobbler(p.getInputStream());
                //StreamGobbler errs= new StreamGobbler(p.getErrorStream());
                //ins.start();
                //errs.start();
                
                try {
                    if ( !p.waitFor( 30, TimeUnit.SECONDS ) ) {
                        throw new IllegalArgumentException("process failed to return");                            
                    }
                    if ( p.exitValue()!=0 ) {
                        System.out.println("exit code: "+p.exitValue());
                    }
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(Video.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            f = thumb;
            doDecorate = true; // totem-video-thumbnailer decorates.
        } else {
            f= thumb;
            doDecorate = true; // assume thumb has decorations.
            
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
            for (int i = 0; i < image.getHeight(); i += step-1) {
                g.fill( new RoundRectangle2D.Double( image.getWidth()-5-step+10, i+5, step-10, step-10, 3, 3 ) );
            }
        }
        return image;
    }

    @Override
    public Image getImage() throws IOException {
        return getImageIcon();
    }
}
