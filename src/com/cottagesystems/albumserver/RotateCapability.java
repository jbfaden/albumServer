/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 * rotating images.
 * @author jbf
 */
public class RotateCapability implements Capability {

    int rotate=0;
    
    public void setRotate( int rotate ) {
        this.rotate= rotate;
    }

    boolean ccw= false;
    public void setCCW( boolean ccw ) {
        this.ccw= ccw;
    }
    
    Album album;
    public void setAlbum( Album album ) {
        this.album= album;
    }
    
    // taken from Java8.
    private static int floorDiv(int x, int y) {
        int r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }
    
    // taken from Java8.
    private static int floorMod(int x, int y) {
        int r = x - floorDiv(x, y) * y;
        return r;
    }
    
    @Override
    public String getHtmlPresentor(Media media, AccessBean access) {
        int angle = ccw ? 90 : -90;
        
        String img= String.format( ccw ? "counterClockwise%03d.png" : "clockwise%03d.png", floorMod( rotate, 360 ) );
        return "<a href=\"AlbumServerContent0.jsp?id="+media.getId()+"&album="+album.getId()+"&rotate="+(rotate+angle)+"\"><img src=\"userInterface/"+img+"\"></a>";
    }

}
