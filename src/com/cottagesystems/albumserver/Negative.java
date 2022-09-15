/*
 * Negative.java
 *
 * Created on January 22, 2007, 9:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author jbf
 */
public class Negative extends Media {
    
    private static final Logger logger= Logger.getLogger("albumServer");
    
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


    /** 
     * Creates a new instance of Negative
     * @param id the identifier like 20020121_kitties/P1210045.JPG
     */
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
    
    @Override
    public String getIconURL( ) {
        return "<image src=\"PhotoServer?id="+id+"&icon=1&size=120\">";
    }

    @Override
    public String getURL() {
        return "<input type='image' src=\"PhotoServer?id="+id+"&size=800\">";
    }
    
    @Override
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

    /**
     * read useful JPG metadata, such as the Orientation.  This also looks to see if GPS
     * metadata is available.
     * @param f the JPG file
     * @return
     * @throws IOException 
     */    
    public static Map<String, Object> getJpegExifMetaData( File f ) throws IOException {
        try ( InputStream ins= new FileInputStream(f) ) {
            return getJpegExifMetaData(ins);
        }
    }
    
    /**
     * read useful JPG metadata, such as the Orientation.  This also looks to see if GPS
     * metadata is available.
     * @param in inputStream from a jpeg source.
     * @return
     * @throws IOException 
     */
    public static Map<String, Object> getJpegExifMetaData(InputStream in) throws IOException {
        Metadata metadata;
        
        try {
            metadata = JpegMetadataReader.readMetadata(in);
        } catch ( JpegProcessingException ex ) {
            throw new IllegalArgumentException("Error in JPG");
        }
        
        Map<String, Object> map = new LinkedHashMap<>();

        Directory exifDirectory;

        Collection<ExifSubIFDDirectory> dds= metadata.getDirectoriesOfType(ExifSubIFDDirectory.class);
        if ( dds!=null ) {
            for ( ExifSubIFDDirectory ds: dds ) {
                for ( Tag t : ds.getTags() ) {
                    map.put(t.getTagName(), t.getDescription());
                }
            }
        }

        Collection<ExifIFD0Directory> dds2= metadata.getDirectoriesOfType(ExifIFD0Directory.class);
        if ( dds2!=null ) {
            for ( ExifIFD0Directory ds2 : dds2 ) {
                for (Tag t : ds2.getTags()) {
                    map.put(t.getTagName(), t.getDescription());
                }
            }
        }

        Collection<GpsDirectory> dds3 = metadata.getDirectoriesOfType(GpsDirectory.class);
        if ( dds2!=null ) {
            for ( GpsDirectory ds3: dds3 ) {
                for (Tag t : ds3.getTags()) {
                    map.put(t.getTagName(), t.getDescription());
                }
            }
        }

        return map;
    }
    
    @Override
    public Image getReducedImage() throws IOException {
        final File f= new File( Configuration.getCacheRoot(), "half/"+id );
        if ( !f.exists() ) {
            Runnable run= () -> {
                try {
                    BufferedImage image = null;
                    String format = Util.getExt(id);
                    image = ImageIO.read(new File(Configuration.getImageDatabaseRoot(), id));
                    final int w = image.getWidth();
                    final int h = image.getHeight();
                    BufferedImage half = new BufferedImage(w / 2, h / 2, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = (Graphics2D) half.getGraphics();
                    g.drawImage(image.getScaledInstance(w / 2, h / 2, Image.SCALE_AREA_AVERAGING), null, null);
                    if ( !f.getParentFile().exists() ) {
                        if ( !f.getParentFile().mkdirs() ) {
                            logger.log(Level.WARNING, "unable to make home for reduced image: {0}", f);
                        }
                    }
                    if ( !ImageIO.write(half, format, f) ) {
                        logger.log(Level.WARNING, "no appropriate writer found for format: {0}", format);
                    }
                    image = half;
                } catch (IOException ex) {
                    Logger.getLogger(Negative.class.getName()).log(Level.SEVERE, null, ex);
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
