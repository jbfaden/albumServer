/*
 * Album.java
 *
 * Created on January 22, 2007, 9:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An album is a collection of Media, for example pictures.  It is 
 * presented in HTML on the left as a list of thumbnails.
 * @author jbf
 */
public class Album {
    
    private final static Logger logger= Logger.getLogger("albumServer");
    
    static HashMap<String,Album> albums= new HashMap<>();
    
    ArrayList<Media> negatives;
    
    boolean isListed;
    String folderName;
    String id;
    
    public Album( String id ) {
        negatives= new ArrayList<>();
        this.id= id;
        this.folderName= id;
        albums.put( id,this );
    }
    
    /**
     * adds the negative to the album, a photo with no cropping.
     */
    public void add( Media n ) {
        negatives.add( n );
    }
    
    /**
     *  set this to true if all the contents have been added
     * @param listed
     */
    public void setListed( boolean listed ) {
        this.isListed= listed;
    }
    
    /**
     * identifier for the album
     */
    public String getId() {
        return id;
    }
    
    /**
     * human-consumable label for the album
     */
    public String getLabel() {
        return folderName;
    }
    
    /**
     * set or clear the notes.
     * @param meta null or the notes.
     */
    public void setNotes( String[] meta ) {
        String metaFileStr= Configuration.getNotesRoot() + id + "/album.txt";
        File metaFile= new File( metaFileStr );
        File tFile= new File( metaFileStr+".temp" );

        if ( meta!=null ) {
            boolean containsData= false;
            for (String meta1 : meta) {
                if (meta1.trim().length() > 0) {
                    containsData= true;
                }
            }
            if ( !containsData ) {
                meta= null;
            }
        }
        
        if ( meta==null ) {
            if ( !metaFile.delete() ) {
                logger.log( Level.WARNING, null, "unable to delete" );
            } else {
                return;
            }
        }
        
        try {
            try (BufferedWriter writer = new BufferedWriter( new FileWriter( tFile ) )) {
                for ( String meta1 : meta ) {
                    writer.write(meta1);
                }
            }
            
            if ( ! tFile.renameTo(metaFile) ) {
                metaFile.renameTo( new File( metaFileStr+".t" ) );
                tFile.renameTo(metaFile);
                new File( metaFileStr+".t" ).delete();
            }
            
        } catch (FileNotFoundException ex) {
            logger.log( Level.WARNING, null, ex );
        } catch ( IOException ex ) {
            logger.log( Level.WARNING, null, ex );
        }
        
    }

    
    public String[] getNotes() {
        String metaFileStr= Configuration.getNotesRoot() + id + "/album.txt";
        File metaFile= new File( metaFileStr );
        if ( metaFile.exists() ) {
            ArrayList list= new ArrayList();
            try ( BufferedReader reader= new BufferedReader(new FileReader(metaFile)) ) {
                
                String line;
                
                line = reader.readLine();
                while ( line!=null ) {
                    list.add(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
            return (String[]) list.toArray( new String[list.size()] );
        } else {
            return new String[0];
        }
        
    }
            
    public synchronized List<Media> getContents() {
        if(! isListed ) {
            File f= new File( Configuration.getImageDatabaseRoot(), folderName );
            String[] list= f.list();
            boolean constantLength= true;
            int nameLength=0;
            int counterOffset=0;
            Pattern p= Pattern.compile("(\\d+).*");
            // album/IMG_0383.JPG album/IMG_0384.MOV album/IMG_0385.JPG 
            for (String list1 : list) {
                String lid = new File(f, list1).toString().substring(Configuration.getImageDatabaseRoot().length());
                
                lid= lid.replaceAll("\\\\", "/" );
                Media m=  Media.createMedia( lid );
                int ifileName= lid.indexOf("/")+1;
                if ( m!=null ) { // for example, IMG_0532.JPG.txt returns null...
                    this.add( m );
                    Matcher matcher;
                    if ( nameLength==0 ) {
                        nameLength= lid.length();
                        matcher= p.matcher(lid);
                        if ( matcher.find(ifileName) ) {
                            counterOffset= matcher.start(1);
                        } else {
                            counterOffset= 0;
                        }
                    } else {
                        if ( lid.length()!=nameLength ) {
                            constantLength= false;
                        } else {
                            matcher= p.matcher(lid);
                            if ( matcher.find(ifileName) ) {
                                if ( counterOffset>0 ) {
                                    if ( matcher.start(1)!=counterOffset ) {
                                        constantLength= false;
                                    }
                                }
                            } else {
                                constantLength= false;
                            }
                        }
                    }
                }
            }
            Comparator c;
            if ( constantLength ) {
                final int fcounterOffset= counterOffset;
                c= (Comparator) (Object o1, Object o2) -> ((Media)o1).getId().substring(fcounterOffset).compareTo(((Media)o2).getId().substring(fcounterOffset));
            } else {
                c= (Comparator) (Object o1, Object o2) -> ((Media)o1).getTimeStamp().compareTo(((Media)o2).getTimeStamp());
            }
            Collections.sort( negatives, c );
            isListed= true;
        }
        return negatives;
    }

    public synchronized void refresh() {
        this.negatives.removeAll(this.negatives);
        this.isListed= false;
    }
    
    public static Album createFromDirectory( File dir ) {
        Album result= albums.get(dir.getName());
        if ( result==null ) {
            result= new Album(dir.getName());
            result.isListed= false;
            result.folderName= dir.getName();
        }
        return result;
    }
    
    /**
     * return the album for this id, or null.
     * @param id, string id, often the directory name.
     * @return null if the album doesn't exist, the album otherwise.
     */
    public static Album getAlbum( String id ) {
	return albums.get(id);
    }
    
    public static List<Album> getAlbums( AccessBean accessBean ) {
        File f= new File( Configuration.getImageDatabaseRoot() );
        String[] list= f.list();
        List<Album> result= new ArrayList<>();
        for (String list1 : list) {
            if (list1.equals("cache")) {
                continue;
            }
            File dir = new File(Configuration.getImageDatabaseRoot(), list1);
            if ( !dir.isDirectory() ) continue;
            File faccess= new File( dir, "access.txt" );
            if ( faccess.exists() ) {
                if ( !faccess.canRead() ) {
                    logger.info("! unable to read access.txt !");
                } else {
                    String username= accessBean.getUsername();
                    BufferedReader r;
                    try {
                        r = new BufferedReader(new FileReader(faccess));
                        String users= r.readLine();
                        r.close();
                        Pattern p= Pattern.compile("[0-9]+");
                        if ( p.matcher(users).matches() && accessBean.hasRole( AccessBean.ROLE_AUTHOR ) ) {
                            result.add( createFromDirectory( dir ) );
                        } else if ( users.contains(username) ) {
                            result.add( createFromDirectory( dir ) );
                        }
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    } catch ( IOException ex ) {
                        throw new RuntimeException(ex);
                    }
                }
            } else {
                result.add( createFromDirectory( dir ) );
            }
        }
        Collections.sort(result, (Object o1, Object o2) -> -1*((Album)o1).getLabel().compareTo(((Album)o2).getLabel()));
        return result;
    }
}
