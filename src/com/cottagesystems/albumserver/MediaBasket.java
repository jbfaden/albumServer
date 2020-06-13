/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbf
 */
public class MediaBasket {
    List<Media> medias= new ArrayList<Media>();
    
    public void addMedia( Media media ) {
        medias.add( media );
    }
    
    
}
