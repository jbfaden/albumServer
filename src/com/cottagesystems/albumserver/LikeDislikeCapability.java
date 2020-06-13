/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class LikeDislikeCapability implements Capability {

    public String getHtmlPresentor(Media media, AccessBean access) {
        return
        "<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=like&value=-2\">-2</a>" +
        "<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=like&value=-1\">-1</a>" +
        "<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=like&value=0\">0</a>" +
        "<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=like&value=1\">+1</a>" +
        "<a href=\"attributes.jsp?id="+media.getId()+"&action=set&name=like&value=2\">+2</a>";
    }

}
