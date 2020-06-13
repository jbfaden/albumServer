/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class CopyToClipboardCapability implements Capability {

    String surl;

    public void setUrl(String url) {
        this.surl = url;
    }

    public String getHtmlPresentor(Media media, AccessBean access) {
        String urlModified = "" + surl;
        String urlRaw = "MediaServer?id=" + media.getId();

        StringBuffer result = new StringBuffer();
        result.append("<applet archive='AlbumServerClient.jar' code='albumserverclient.ClipboardApplet' height='20' width='100'>\n");
        result.append("<param name='initialMessage' value='right click' >\n");
        int i = 0;

        result.append("<param name='URL_0' value='" + urlModified + "'>\n");
        result.append("<param name='LABEL_0' value='copy to clipboard'>\n");
        i += 1;

        result.append("<param name='URL_" + i + "' value='" + urlRaw + "'>\n");
        result.append("<param name='LABEL_" + i + "' value='copy raw to clipboard'>\n");
        result.append("</applet> \n");
        
        return result.toString();

    }
}
