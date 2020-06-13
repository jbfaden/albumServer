/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbf
 */
public class HideCapability implements Capability {

    public String getHtmlPresentor(Media media, AccessBean access) {
        return "<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=hidden&value=true\">hide</a>";
    }

    public boolean isHidden( Media media ) {
        Properties p= new Properties();
        File propFile= new File( Configuration.getCacheRoot() + "attr/" + media.id );
        propFile.getParentFile().mkdirs();
        if ( propFile.exists() ) {
            try {
                p.load(new FileReader(propFile));
            } catch (IOException ex) {
                Logger.getLogger(HideCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "true".equals( p.getProperty( "hidden" ) );
        
    }
}
