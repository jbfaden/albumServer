/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class SwfVideoCapability implements Capability {
    public static SwfVideoCapability INSTANCE= new SwfVideoCapability();
    public String getHtmlPresentor(Media media, AccessBean access) {
        int i= media.getId().lastIndexOf(".");
        String ipodmp4= media.getId().substring(0,i) + ".swf";
        return "<a href=\"MediaServer?id="+ipodmp4+"\" target=_blank>swf</a> ";
    }

}
