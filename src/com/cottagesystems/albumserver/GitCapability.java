
package com.cottagesystems.albumserver;

import java.net.URL;

/**
 * A git server which contains notes history is known.
 * @author jbf
 */
public class GitCapability implements Capability {
    public static GitCapability INSTANCE= new GitCapability();
    @Override
    public String getHtmlPresentor(Media media, AccessBean access) {
        URL s= Configuration.getNotesURL();
        if ( s==null ) {
            return null;
        } else {
            String metaFileStr= Configuration.getNotesURL().toString() + media.getId() + ".md";
            return "<a href=\"" + metaFileStr + "\" target='_blank'>notes</a>";
        }
    }

}
