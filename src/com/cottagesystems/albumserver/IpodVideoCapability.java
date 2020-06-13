/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class IpodVideoCapability implements Capability {
    public static IpodVideoCapability INSTANCE= new IpodVideoCapability();
    public String getHtmlPresentor(Media media, AccessBean access) {
        int i= media.getId().lastIndexOf(".");
        String ipodmp4= media.getId().substring(0,i) + ".mp4";
        return "<a href=\"MediaServer?id="+ipodmp4+"\" target=_blank>ipod</a> ";
    }

}
