/*
 * Configuration.java
 *
 * Created on January 21, 2007, 10:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbf
 */
public class Configuration {
    
    private static String home = null;
    private static String imageDatabaseRoot = null;
    private static String cacheRoot = null;
    private static String notesRoot = null;
    private static URL notesURL = null;
    private static String videoThumbnailer = null;
            
    /**
     * return true if the configuration has not been loaded.
     * @return 
     */
    public static boolean isNotLoaded() {
        return imageDatabaseRoot==null;
    }
    
    public static void load( String home ) throws FileNotFoundException {
        try {
            
            if ( Configuration.home==null ) {
                if ( home==null ) home= System.getProperty("ALBUM_SERVER_HOME");
                if ( home==null ) home= "/tmp/albumserver/";
                if ( !home.endsWith("/") ) home= home+"/";
                Configuration.home= home;
            }
            
            if ( !Configuration.home.endsWith("/") ) {
                throw new IllegalArgumentException("Configuration.home doesn't end in /.");
            }
            
            Properties prop= new Properties();
            File configFile= new File( Configuration.home + "config.properties" );
            if ( !configFile.exists() ) {
                if ( !configFile.getParentFile().exists() ) {
                    if ( !configFile.getParentFile().mkdir() ) {
                        throw new IllegalArgumentException("can't make the config directory: " + configFile.getParentFile() );
                    }
                }
                try (FileWriter fw = new FileWriter(configFile)) {
                    fw.write("# directory relative to this configuration\n");
                    fw.write("imageDatabaseRoot=imageDatabase/\n");
                    fw.write("\n");
                    fw.write("# location of directory where server can generate thumbnails and reduced resolution images.\n");
                    fw.write("cacheRoot=imageCache/\n");
                    fw.write("\n");
                    fw.write("# location of directory where server can find annotations to images.  This might be a clone of a Gitlab server.\n");
                    fw.write("notesRoot=notes/\n");
                    fw.write("\n");
                    fw.write("# location of Gitlab server project root where annotations can be editted.\n");
                    fw.write("notesURL=\n");
                    fw.write("\n");
                    fw.write("# command to run to generate thumbnails of videos. \" <video> <thumb>\" is appended to the line and it is executed.\n");
                    fw.write("videoThumbnailer=/usr/bin/totem-video-thumbnailer -s 640\n");
                }
            }
            prop.load( new InputStreamReader( new FileInputStream( configFile ) ) );
            imageDatabaseRoot = prop.getProperty("imageDatabaseRoot");
            cacheRoot = prop.getProperty("cacheRoot");
            notesRoot = prop.getProperty("notesRoot");
            String notesURLString = prop.getProperty("notesURL",null);
            if ( notesURLString==null || notesURLString.length()==0 ) {
                notesURL = null;
            } else {
                notesURL = new URL( notesURLString );
            }
            
            if ( !imageDatabaseRoot.startsWith("/") ) {
                imageDatabaseRoot= Configuration.home + imageDatabaseRoot;
            }
            
            if ( !cacheRoot.startsWith("/") ) {
                cacheRoot = Configuration.home + cacheRoot;
            }
            
            if ( !notesRoot.startsWith("http") ) {
                if ( !notesRoot.startsWith("/") ) {
                    notesRoot = Configuration.home + notesRoot;
                }
            }
            
            if ( !notesRoot.endsWith("/") ) {
                notesRoot= notesRoot + "/";
            }
            
            if ( !imageDatabaseRoot.endsWith("/") ) {
                imageDatabaseRoot= imageDatabaseRoot + "/";
            }

            if ( !cacheRoot.endsWith("/") ) {
                cacheRoot= cacheRoot + "/";
            }
            
            videoThumbnailer= prop.getProperty("videoThumbnailer");
            
        } catch (IOException ex) {
            if ( ex instanceof FileNotFoundException ) {
                throw (FileNotFoundException)ex;
            }
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * return the home directory containing secure information like the
     * configuration and passwords file.
     * @return 
     */
    public static String getHome() {
        return home;
    }
    
    /**
     * soon-to-be read only folder of "negatives"
     * @return
     */
    public static String getImageDatabaseRoot() {
        return imageDatabaseRoot;
    }
    
    /**
     * return the properties for the media id.  These include:<ul>
     * <li>hidden: if true then don't show the picture.
     * <li>orient: default rotation for the image.
     * </ul>
     * @param id the media id.
     * @return the properties.
     */
    public static Properties getAttr( String id ) {
        Properties p= new Properties();
        File propFile= new File( Configuration.getNotesRoot() + id + ".properties" );
        File parent= propFile.getParentFile();
        if ( !parent.exists() ) parent.mkdirs();
        if ( propFile.exists() ) {
            try ( Reader r=new FileReader(propFile) ) {
                p.load(r);
            } catch (IOException ex) {
                Logger.getLogger(HideCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return p;
    }
    
    public static boolean isValidAttribute( String name ) {
        return ( name.equals("hidden") || name.equals("like") );
    }
    
    /**
     * Return the writable folder that is not necessarily backed up, used to
     * store cached resources.  This contains subdirectories:<ul>
     * <li>half: images reduced to half resolution
     * <li>icons: thumbnail icons for resources
     * </ul>
     * 
     * @return the writable folder for cache items.
     */
    public static String getCacheRoot() {
        return cacheRoot;
    }
    
    /**
     * return the logger for the album server
     * @return 
     */
    public static Logger getLogger() {
        return Logger.getLogger("albumserver");
    }
    
    /**
     * the location of a writable folder that is backed up.  
     * Arbitrary text is put here.  This might be a clone of a GitLab
     * repository.
     * @return
     */
    public static String getNotesRoot() {
        return notesRoot;
    }
    
    /**
     * null or the location of a remote store of notes, like a Gitlab server.
     * @return 
     */
    public static URL getNotesURL() {
        return notesURL;
    }
    
    /**
     * command to run to create a thumbnail from a video
     * @return 
     */
    public static String getVideoThumbnailer() {
        return videoThumbnailer;
    }
        
}
