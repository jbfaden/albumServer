/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class PrintCapability implements Capability {

    int rotate=0;
    Album album;
    
    public void setRotate( int rotate ) {
        this.rotate= rotate;
    }
    
    public void setAlbum( Album album ) {
        this.album= album;
    }
    
    public String getHtmlPresentor(Media media, AccessBean access) {
        return "<a align=left href=\"AlbumServerContent0.jsp?id="+media.getId()+"&album="+album.getId()+"&rotate="+rotate+"&print=1\" target=_blank>print</a> ";
    }

}
