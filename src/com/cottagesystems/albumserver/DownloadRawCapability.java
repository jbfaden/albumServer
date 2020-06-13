/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class DownloadRawCapability implements Capability {
    public String getHtmlPresentor(Media media, AccessBean access) {
        return "<a href=\"MediaServer?id="+media.getId()+"\" target=_blank>download</a> ";
    }

}
