
package com.cottagesystems.albumserver;

import java.util.Properties;

/**
 *
 * @author jbf
 */
public class HideCapability implements Capability {

    @Override
    public String getHtmlPresentor(Media media, AccessBean access) {
        if ( isHidden(media) ) {
            return "<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=hidden&value=false\">unhide</a>";
        } else {
            return "<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=hidden&value=true\">hide</a>";
        }
    }

    /**
     * return true if the media should be hidden.
     * @param media
     * @return 
     */
    public boolean isHidden( Media media ) {
        Properties p= Configuration.getAttr(media.id);
        return "true".equals( p.getProperty( "hidden" ) );        
    }
}
