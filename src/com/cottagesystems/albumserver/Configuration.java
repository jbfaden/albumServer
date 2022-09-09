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
            
    /**
     * return true if the configuration has not been loaded.
     * @return 
     */
    public static boolean isNotLoaded() {
        return imageDatabaseRoot==null;
    }
    
    public static void load() throws FileNotFoundException {
        try {
            
            if ( Configuration.home==null ) {
                String home=null;
                if ( home==null ) home= System.getProperty("ALBUM_SERVER_HOME");
                if ( home==null ) home= "/tmp/albumserver/";
                Configuration.home= home;
            }
            
            Properties prop= new Properties();
            File configFile= new File( home + "config.properies" );
            if ( !configFile.exists() ) {
                if ( !configFile.getParentFile().exists() ) {
                    if ( !configFile.getParentFile().mkdir() ) {
                        throw new IllegalArgumentException("can't make the config directory");
                    }
                }
                FileWriter fw= new FileWriter(configFile);
                fw.write("imageDatabaseRoot=/tmp/albumserver/imageDatabase/\n"
                        + "cacheRoot=/tmp/albumServer/imageCache/\n"
                        + "notesRoot=/tmp/albumServer/notes/\n"
                        + "notesURL="
                );
                fw.close();
            }
            prop.load( new InputStreamReader( new FileInputStream( configFile ) ) );
            imageDatabaseRoot = prop.getProperty("imageDatabaseRoot");
            cacheRoot = prop.getProperty("cacheRoot");
            notesRoot = prop.getProperty("notesRoot");
            String notesURLString = prop.getProperty("notesURL",null);
            if ( notesURLString==null ) {
                notesURL = null;
            } else {
                notesURL = new URL( notesURLString );
            }
            
            if ( !imageDatabaseRoot.startsWith("/") ) {
                imageDatabaseRoot= home + imageDatabaseRoot;
            }
            
            if ( !cacheRoot.startsWith("/") ) {
                cacheRoot = home + cacheRoot;
            }
            
            if ( !notesRoot.startsWith("http") ) {
                if ( !notesRoot.startsWith("/") ) {
                    notesRoot = home + notesRoot;
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
     * the location of a writeable folder that is backed up.  
     * Arbitrary text is put here.
     * @return
     */
    public static String getNotesRoot() {
        return notesRoot;
    }
    
    /**
     * null or the location of a remote store of notes, like a Gitlabs server.
     * @return 
     */
    public static URL getNotesURL() {
        return notesURL;
    }
        
}
