/*
 * Capability.java
 *
 * Created on October 31, 2007, 7:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 * Dummy interface to store capabilities.  Instances of this will just be
 * used to mark capabilities for now.
 * @author jbf
 */
public interface Capability {
    public Capability PRINT = new PrintCapability();
    
    public Capability ROTATE = new RotateCapability();    
    
    public Capability DOWNLOAD_RAW = new DownloadRawCapability();
    
    public Capability DOWNLOAD_MODIFIED = new DownloadModifiedCapability();
    
    public Capability COPY_TO_CLIPBOARD = new CopyToClipboardCapability();

    public Capability ORIENTATION= new OrientationCapability();
    
    public Capability HIDE= new HideCapability();

    public Capability LIKE_DISLIKE= new LikeDislikeCapability();

    /**
     * returns the html string that presents the capability
     * @param media
     * @return
     */
    public String getHtmlPresentor( Media media, AccessBean access );
}



