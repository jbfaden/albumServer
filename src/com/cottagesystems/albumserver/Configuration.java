/*
 * Configuration.java
 *
 * Created on January 21, 2007, 10:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class Configuration {
    
    static Boolean isXP= new Boolean( System.getProperty("os.name").equals("Windows XP") );
    
    /**
     * soon-to-be read only folder of "negatives"
     * @return
     */
    public static String getImageDatabaseRoot() {
        if (  isXP ) {
            return "l:/imageDatabase/";
        } else {
            return "/home/jbf/imageDatabase/";
            //return "/media/mini/documents/pictures/";
        }
    }
    
    /**
     * this is a writeable folder that is not necessarily backed up.
     */
    public static String getCacheRoot() {
        if (  isXP ) {
            return "l:/imageDatabase/cache/";
        } else {
            return "/home/jbf/imageDatabaseCache/";
            //return "/media/mini/documents/pictures/cache/";
        }
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
