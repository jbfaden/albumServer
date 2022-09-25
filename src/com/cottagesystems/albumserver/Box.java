
package com.cottagesystems.albumserver;

/**
 * a Box is a special type of album which contains other albums.
 * @author jbf
 */
public class Box extends Album {
    
    public static Box ROOT= new Box("");
    
    public Box(String id) {
        super(id);
    }
    
}
