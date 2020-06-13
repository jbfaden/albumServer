/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cottagesystems.albumserver;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Returns the Location as Lat,Lon
 * @author jbf
 */
public class LocationCapability implements Capability {

    @Override
    public String getHtmlPresentor(Media media, AccessBean access) {
        String s= load(media);
        if ( s==null ) {
            return "Location=?";
        } else {
            //<a href="https://www.google.com/maps/@41.6611055,-91.5111817,15z">s</a>
                    
            return s;
        }
    }
    

    /**
     * @see https://github.com/drewnoakes/metadata-extractor/wiki/GettingStarted
     * @param media
     * @return 
     */
    private String load( Media media ) {
        try {
            File f = new File(Configuration.getImageDatabaseRoot(), media.id);
            Metadata metadata = ImageMetadataReader.readMetadata(f);
            GpsDirectory directory= metadata.getFirstDirectoryOfType( GpsDirectory.class); 
            if ( directory!=null ) {
                GeoLocation geoLocation= directory.getGeoLocation();
                if ( geoLocation!=null && !geoLocation.isZero() ) {
                    return "<a target=\"_blank\" href=\"https://www.google.com/maps/@"+geoLocation.getLatitude()+","+geoLocation.getLongitude()+",15z\">" + geoLocation.toDMSString() + "</a>";
                } else {
                    return null;
                }
            } else {
                return null;
            }
            
        } catch (ImageProcessingException ex) {
            Logger.getLogger(LocationCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LocationCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        //return null;    
    }
}
