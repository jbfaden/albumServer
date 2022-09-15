/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbf
 */
public class OrientationCapability implements Capability {

    @Override
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
            FileInputStream ins= new FileInputStream(f);
            Map<String,Object> m;
            try {
                m= Negative.getJpegExifMetaData(ins);
                if ( m.containsKey("Orientation") ) {
                    String d= (String) m.get("Orientation");
                    if ( d.startsWith("Bottom") ) {
                        if ( d.substring(8).startsWith("right") ) {
                            return "rot180";
                        } else {
                            return "";
                        }
                    } else if ( d.startsWith("Right side, ") ) {
                        if ( d.substring(12).startsWith("top") ) {
                            return "rot90";
                        } else {
                            return "";
                        }
                    } else if ( d.startsWith("Left side, ") ) {
                        if ( d.substring(11).startsWith("bottom") ) {
                            return "rot270";
                        } else {
                            return "";
                        }
                    } else if ( d.startsWith("Top, ") ) {
                        if ( d.substring(4).startsWith("right") ) {
                            return "rot90";
                        } else {
                            return "";
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OrientationCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
            Collection<ExifSubIFDDirectory> ss= metadata.getDirectoriesOfType( ExifSubIFDDirectory.class) ;
            for ( ExifSubIFDDirectory s : ss ) {
                if ( s.containsTag( ExifSubIFDDirectory.TAG_ORIENTATION ) ) {
                    int ii= s.getInt( ExifSubIFDDirectory.TAG_ORIENTATION );
                    switch (ii) {
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
                }
            }
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
        } catch ( MetadataException | IOException | ImageProcessingException ex ) {
            Logger.getLogger(OrientationCapability.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
        return null;
    }


}
