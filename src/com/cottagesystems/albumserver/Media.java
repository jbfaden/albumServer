/*
 * Media.java
 *
 * Created on January 22, 2007, 10:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jbf
 */
public class Media {
    
    private final static Logger logger= Logger.getLogger("albumServer");
    
    String id;
    
    /** Creates a new instance of Media 
     * @param id the identifier
     */
    public Media( String id ) {
        this.id= id;
        capabilities.put( Capability.DOWNLOAD_RAW.getClass(), Capability.DOWNLOAD_RAW );
        capabilities.put( Capability.HIDE.getClass(), Capability.HIDE );
        capabilities.put( Capability.LIKE_DISLIKE.getClass(), Capability.LIKE_DISLIKE );
        capabilities.put( Capability.GIT.getClass(), Capability.GIT );
    }
    
    /**
     * @param id the identifier
     * @return null if the extension is not supported, or id is null.
     */
    public static Media createMedia( String id ) {
        if ( id==null ) return null;
        
        int iext= id.lastIndexOf('.')+1;
        String ext= id.substring( iext );
        
        if ( ext.equalsIgnoreCase("jpg" ) ) {
            return new Negative( id );
        } else if ( ext.equalsIgnoreCase("png") ) {
            return new Negative( id );
        } else if ( ext.equalsIgnoreCase("mpg" ) ) {
            return new Video( id );
        } else if ( ext.equalsIgnoreCase("mp4" ) ) {
            return new Video( id );
        } else if ( ext.equalsIgnoreCase("avi" ) ) {
            return new Video(id);
        } else if ( ext.equalsIgnoreCase("wav") ) {
            return new Media(id);
        } else {
            return null;
        }
    }
    
    public String getLabel() {
        int i= id.lastIndexOf('/');
        String label= i==-1 ? id : id.substring( i+1 );
        return label;
    }
    
    public String getId() {
        return this.id;
    }
    
    private static final Pattern timeStampPattern= Pattern.compile(".*(\\d{8})(.)(\\d{6}).*" );
    
    public Date getTimeStamp() {
        Matcher m= timeStampPattern.matcher(id);
        if ( m.matches() ) {
            SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss" );
            try {
                return ISO8601DATEFORMAT.parse( m.group(1) + "_" + m.group(3) );
            } catch (ParseException ex) {
                logger.log(Level.SEVERE, null, ex); // fall to old code
            }
        }
        File f= new File( Configuration.getImageDatabaseRoot() + id );
        return new Date( f.lastModified() );
    }
    
    /**
     * return a list of notes or captions attached to the media item.
     * @return a list of notes or captions 
     */
    public String[] getNotes() {
        String metaFileStr= Configuration.getNotesRoot() + id + ".txt";
        File metaFile= new File( metaFileStr );
        if ( !metaFile.exists() ) {
            metaFile= new File( Configuration.getNotesRoot() + id + ".md" );
        }
        boolean isTrimming= true;
        if ( metaFile.exists() ) {
            ArrayList list= new ArrayList();
            try {
                BufferedReader reader;
                
                reader = new BufferedReader(new FileReader(metaFile));
                
                String line;
                
                line = reader.readLine();
                while ( line!=null ) {
                    if ( isTrimming && line.trim().length()>0 ) isTrimming= false;
                    if ( !isTrimming ) list.add(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
            return (String[]) list.toArray( new String[list.size()] );
        } else {
            return new String[0];
        }
        
    }
    
    /**
     * set or clear the notes.
     * @param meta null or the notes.
     */
    public void setNotes( String[] meta ) {
        String metaFileStr= Configuration.getNotesRoot() + id + ".txt";
        File metaFile= new File( metaFileStr );
        File tFile= new File( metaFileStr+".temp" );

        if ( meta!=null ) {
            boolean containsData= false;
            for (String meta1 : meta) {
                if (meta1.trim().length() > 0) {
                    containsData= true;
                }
            }
            if ( !containsData ) {
                meta= null;
            }
        }
        
        if ( meta==null ) {
            if ( !metaFile.delete() ) {
                logger.log( Level.WARNING, null, "unable to delete" );
            } else {
                return;
            }
        }
        
        if ( !tFile.getParentFile().exists() ) {
            if ( !tFile.getParentFile().mkdirs() ) {
                logger.log(Level.WARNING, "unable to mkdir {0}", tFile.getParentFile());
                return;
            }
        }
        
        try {
            try (BufferedWriter writer = new BufferedWriter( new FileWriter( tFile ) )) {
                for ( String meta1 : meta ) {
                    writer.write(meta1);
                }
            }
            
            if ( ! tFile.renameTo(metaFile) ) {
                metaFile.renameTo( new File( metaFileStr+".t" ) );
                tFile.renameTo(metaFile);
                new File( metaFileStr+".t" ).delete();
            }
            
        } catch (FileNotFoundException ex) {
            logger.log( Level.WARNING, null, ex );
        } catch ( IOException ex ) {
            logger.log( Level.WARNING, null, ex );
        }
        
    }
    
    public boolean equals( Object o ) {
        if ( o==this ) return true;
        if ( ! ( o instanceof Media ) ) return false;
        Media mo= (Media) o;
        return mo.id.equals( this.id );
    }
    
    /**
     * return the HTML for a small icon, e.g. a call to the PhotoServer
     * with the id and size=120.
     * 
     * @return the HTML for a small icon.
     */
    public String getIconURL() {
        return "<image src='PhotoServer?id="+id+"&icon=1&size=120'>";
    }
    
    
    public Image getImageIcon() throws IOException {
        BufferedImage result= new BufferedImage( 300, 300, BufferedImage.TYPE_INT_RGB );
        Graphics2D g= (Graphics2D)result.getGraphics();
        
        g.setColor( Color.WHITE );
        g.fillRect(0,0,400,300);
        
        g.setColor( new Color( 240,240,255 ) );
        g.fillRoundRect(0,0,400,300,30,30);
        
        g.setColor( new Color( 0,0,20 ) );
        g.setFont( g.getFont().deriveFont( 72 ) );
        g.drawString( id, 5, 150 );
        
        return result;
    }
    
    /** 
     * return a full resolution image of the media item
     */
    public Image getImage() throws IOException {
        return getImageIcon();
    }

    /**
     * return a half-reduce image of the media item, or null if none exists.
     */
    public Image getReducedImage() throws IOException {
        return null;
    }

    
    public int hashCode() {
        return id.hashCode();
    }
    
    /**
     * return a method for getting the raw media item
     */
    public String getURL() {
        //return "<button id='downloadButton' onclick=\"document.location='MediaServer?id="+id+" ' \">download<button>";
        return "<a href='MediaServer?id="+id+" ' target='_blank'>download</a>";
    }
    
    public String getURL( String params ) {
        return getURL();
    }
    
    HashMap capabilities= new HashMap();
    
    /**
     * returns a capability object or null if the capability doesn't exist.
     */
    public Capability getCapability( Class capability, AccessBean access ) {
        return (Capability)capabilities.get( capability );
    }
}
