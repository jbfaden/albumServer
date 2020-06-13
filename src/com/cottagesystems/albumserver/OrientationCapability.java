/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbf
 */
public class OrientationCapability implements Capability {

    public String getHtmlPresentor(Media media, AccessBean access) {
        String s= load(media);
        if ( s==null ) return "Exif N/A";
        return s;
    }

    /**
     * @see https://github.com/drewnoakes/metadata-extractor/wiki/GettingStarted
     * @param media
     * @return 
     */
    public String load( Media media ) {
        try {
            File f = new File(Configuration.getImageDatabaseRoot(), media.id);
            Metadata metadata = ImageMetadataReader.readMetadata(f);
            ExifSubIFDDirectory directory= metadata.getFirstDirectoryOfType( ExifSubIFDDirectory.class); 
            if ( directory!=null ) {
                Directory exifDirectory = directory;
                if ( exifDirectory.containsTag( ExifDirectoryBase.TAG_ORIENTATION ) ) {
                    int s= exifDirectory.getInt( ExifDirectoryBase.TAG_ORIENTATION );
                    switch (s) {
                        case 1: return "";
                        case 2: return "flipH";
                        case 3: return "rot180";
                        case 4: return "flipV";
                        case 5: return "flipH,flipV";
                        case 6: return "rot90";
                        case 7: return "???";
                        case 8: return "rot270";
                        default: throw new IllegalArgumentException("bad exif orientation value: "+s);
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch ( MetadataException ex ) {
            Logger.getLogger(OrientationCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch ( IOException ex ) {
            Logger.getLogger(OrientationCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JpegProcessingException ex) {
            Logger.getLogger(OrientationCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ImageProcessingException ex) {
            Logger.getLogger(OrientationCapability.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
        return null;
        //return null;
    }


}
