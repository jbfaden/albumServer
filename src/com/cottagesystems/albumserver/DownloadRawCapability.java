
package com.cottagesystems.albumserver;

/**
 * Allow the resource to be downloaded directly.
 * @author jbf
 */
public class DownloadRawCapability implements Capability {
    @Override
    public String getHtmlPresentor(Media media, AccessBean access) {
        return "<a href=\"MediaServer?id="+media.getId()+"\" target=\"_blank\">download</a> ";
    }

}
