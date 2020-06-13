/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class DownloadModifiedCapability implements Capability {

    int rotate=0;
    
    public void setRotate( int rotate ) {
        this.rotate= rotate;
    }
    
    public String getHtmlPresentor(Media media, AccessBean access) {
        return "<a href=\"PhotoServer.jpg?id="+media.getId()+"&rotate="+rotate+"\" target=_blank>full resolution</a> ";
    }

}
