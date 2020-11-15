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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbf
 */
public class Configuration {
    
    static Boolean isXP= new Boolean( System.getProperty("os.name").equals("Windows XP") );
    
    private static String imageDatabaseRoot;
    
    /**
     * soon-to-be read only folder of "negatives"
     * @return
     */
    public static String getImageDatabaseRoot() {
        if (imageDatabaseRoot == null) {
            if (System.getProperty("imageDatabaseRoot") != null) {
                imageDatabaseRoot = System.getProperty("imageDatabaseRoot");
            } else if (isXP) {
                imageDatabaseRoot = "l:/imageDatabase/";
            } else {
                imageDatabaseRoot = "/Users/jbf/imageDatabase/";
                //return "/media/mini/documents/pictures/";
            }
        }
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
        File propFile= new File( Configuration.getCacheRoot() + "attr/" + id );
        propFile.getParentFile().mkdirs();
        if ( propFile.exists() ) {
            try ( Reader r=new FileReader(propFile) ) {
                p.load(r);
            } catch (IOException ex) {
                Logger.getLogger(HideCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return p;
    }
    
    private static String imageDatabaseCacheRoot=null;
    
    /**
     * Return the writable folder that is not necessarily backed up, used to
     * store cached resources.  This contains subdirectories:<ul>
     * <li>half: images reduced to half resolution
     * <li>icons: thumbnail icons for resources
     * <li>attr: properties file for any image.
     * </ul>
     * 
     * @return the writable folder for cache items.
     */
    public static String getCacheRoot() {
        if (imageDatabaseCacheRoot == null) {
            if (System.getProperty("imageDatabaseCacheRoot") != null) {
                imageDatabaseCacheRoot = System.getProperty("imageDatabaseCacheRoot");
            } else if (isXP) {
                imageDatabaseCacheRoot = "l:/imageDatabase/cache/";
            } else {
                imageDatabaseCacheRoot = "/Users/jbf/imageDatabaseCache/";
                //return "/media/mini/documents/pictures/cache/";
            }
        }
        return imageDatabaseCacheRoot;
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
        if ( isXP ) {
            return "l:/imageDatabase/";
        } else {
            return "/home/jbf/imageDatabase/";
            //return "/media/mini/documents/pictures/cache";
        }
    }
    
        
}
