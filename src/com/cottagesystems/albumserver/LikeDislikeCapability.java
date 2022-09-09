
package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class LikeDislikeCapability implements Capability {

    @Override
    public String getHtmlPresentor(Media media, AccessBean access) {
        return
        "[<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=like&value=-2\">&#x1F44E;</a>" +
        "<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=like&value=0\">0</a> " +
        "<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=like&value=2\">&#x1F44D;</a>]";
    }

}
