/*
 * AlbumIndex.java
 *
 * Created on February 15, 2007, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Makes and manages the index images.  The idea was this would form a more
 * relaxed view of the album, but was never brought into production.
 * @author jbf
 */
public class AlbumIndex {
    
    Album album;
    List<Media> contents;
    List<GeneralPath> rectangles;
    static HashMap<Album,AlbumIndex> indeces= new HashMap<Album,AlbumIndex>();
    
    /** Creates a new instance of AlbumIndex */
    public AlbumIndex( Album album ) {
        this.album= album;
        
    }
    
    public static synchronized AlbumIndex getAlbumIndex( Album album ) {
        AlbumIndex index= indeces.get(album);
        if ( index==null ) {
            final AlbumIndex index1= new AlbumIndex(album);
            new Thread( new Runnable() { public void run() {
                try {index1.makeIndex();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } } } ).start();
                indeces.put( album, index1 );
                index= index1;
        }
        return index;
    }
    
    Media getMediaAt( Point p ) {
        for ( int i=0; i<rectangles.size(); i++ ) {
            GeneralPath r= rectangles.get(i);
            if ( r.contains(p) ) {
                return contents.get(i);
            }
        }
        return null;
    }
    
    public synchronized void makeIndex() throws IOException {
        List<Media> contents= album.getContents();
        List<GeneralPath> rectangles= new ArrayList<GeneralPath>();
        
        BufferedImage image= new BufferedImage( 200, 2000, BufferedImage.TYPE_INT_RGB );
        
        Graphics2D g= (Graphics2D)image.getGraphics();
        
        // g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        //g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g.setColor( Color.WHITE );
        
        g.fillRect(0,0,image.getWidth(),image.getHeight());
        
        AffineTransform at= new AffineTransform();
        
        at.translate(20,0);
        
        Media m= contents.get(0);
        Image im= m.getImageIcon();
        
        double size= Math.sqrt( Math.pow( im.getHeight(null),2 ) + Math.pow( im.getWidth(null), 2 ) );
        
        double scale= 200 / size;
        double oldScale= scale;
        
        at.scale(scale,scale);
        
        int length=0;
        int ll=0;
        
        BufferedImage out= new BufferedImage( 200, 1000, BufferedImage.TYPE_INT_RGB );
        
        Font baseFont= g.getFont();
        
        for ( int i=0; i<contents.size(); i++ ) {
            
            //for ( int i=0; i<20; i++ ) {
            m= contents.get(i);
            im= m.getImageIcon();
            
            size= Math.sqrt( Math.pow( im.getHeight(null),2 ) + Math.pow( im.getWidth(null), 2 ) );
            
            scale= 200/size;
            at.scale(scale/oldScale,scale/oldScale);
            
            int w=  im.getWidth(null);
            int h= im.getHeight(null);
            AffineTransform at0= new AffineTransform( at );
            
            double rot= Math.random()/2 - 0.25;
            at.rotate( rot, w/2, h/2 );
            int iscale= (int)(1/scale);
            
            AffineTransform at1= g.getTransform();
            g.setTransform(at);
            
            Rectangle rect= new Rectangle( -5*iscale, -5*iscale, w+10*iscale, h+10*iscale );
            g.setColor( Color.WHITE );
            g.fill( rect );
            
            g.setColor( Color.lightGray );
            //g.setStroke( new BasicStroke( (float)scale) );
            g.draw( rect );
            
            GeneralPath s= new GeneralPath( rect );
            s.transform( at );
            rectangles.add( s );
            
            g.setFont( baseFont.deriveFont((float)(40/scale)) );
            g.drawString( ""+i,0,(int)(40/scale) );
            
            g.setTransform(at1);
            
            //g.drawImage( im, at, null );
            at=at0;
            
            at.translate( 0, im.getHeight(null) );
            
            length+= im.getHeight(null) * scale ;
            
            
            if ( (length )>(ll+1000) || ( i+1==contents.size() ) ) {
                
                Graphics2D gg= (Graphics2D) out.getGraphics();
                gg.drawImage( image, 0, 0, null );
                
                File f= new File( Configuration.getCacheRoot(), album.getId()+"."+(ll+1000)+".JPG" );
                ImageIO.write( out, "jpg", f );
                
                ll+= 1000;
                
                g.fillRect(75,0,50,1000);
                
                BufferedImage im2= new BufferedImage( 200, 2000, BufferedImage.TYPE_INT_RGB );
                
                gg= (Graphics2D) im2.getGraphics();
                gg.setColor( Color.WHITE );
                gg.fillRect(0,0,im2.getWidth(),im2.getHeight());
                
                gg.drawImage( image, 0, -1000,  null );
                f= new File( Configuration.getCacheRoot(), album.getId()+".test.JPG" );
                ImageIO.write( im2, "jpg", f );
                
                image= im2;
                
                at.translate( 0, -1000/scale );                               
                
            }
            
            oldScale= scale;
        }
        
        // File f= new File( Configuration.getImageDatabaseRoot(), album.getId()+".JPG" );
        //  ImageIO.write( image, "jpg", f );
        File f;
        f= new File( Configuration.getCacheRoot(), album.getId()+".HTML" );
        BufferedWriter writer= new BufferedWriter( new FileWriter(f) );
        
        writer.write( "<style type=\"text/css\">  img { border: none; } </style>" );
        
        writer.write( "<a href=\"AlbumServer0.jsp\" target=\"_top\">");
        writer.write( "albums</a>&gt;<b>"+album.getLabel());
        writer.write( "</b>" );
        
        for ( int ill=0; ill<ll; ill+=1000 ) {
            writer.write( "<img src='MediaServer.jpg?id=cache/"+album.getId()+"."+(ill+1000)+".JPG' usemap='#index."+(ill+1000)+"' > \n" );
            writer.write(" <map name='index."+(ill+1000)+"'>\n " );
            
            for ( int i=contents.size(); i-- >0; ) {
                m= contents.get(i);
                GeneralPath p= rectangles.get(i);
                
                //<a href=\"AlbumServerContent0.jsp?id="+m.getId()+"&album="+album.getId()+"\" target=content>"
                String url= "AlbumServerContent0.jsp?id=" + m.getId() + "&album="+album.getId() ;
                
                StringBuffer coords= new StringBuffer();
                float[] fcoords= new float[2];
                for ( PathIterator it= p.getPathIterator( null ); ! it.isDone(); ) {
                    int x= it.currentSegment(fcoords);
                    coords.append( ""+(int)fcoords[0]+","+((int)fcoords[1]-ill ) ) ;
                    it.next();
                    if ( ! it.isDone() ) coords.append(", ");
                }
                writer.write("   <area shape='polygon' coords='"+coords+"' href='"+url+"' target='content' alt='"+m.getId()+"'>\n" );
                
            }
            writer.write(" </map>\n");
        }
        
        writer.write( "<a href=\"AlbumServer0.jsp\" target=\"_top\">\n");
        writer.write( "albums</a>&gt;<b>"+album.getLabel()+"\n");
        writer.write( "</b>\n" );
        writer.close();
        this.contents=contents;
        this.rectangles= rectangles;
    }
    
    public boolean hasIndexCache() {
        File f= new File( Configuration.getImageDatabaseRoot(), album.getId()+".JPG" );
        return f.exists();
    }
    
    public static void main( String[] args ) throws Exception {
        //Album a= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(), "20061007_hike2") );
        //Album a= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(), "20061005_dinner") );
        //Album a= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(), "20070202_frost" ) );
        //Album a= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(), "grandma_album1" ) );
        //Album a= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(), "20061204_hawaii7" ) );
        Album a= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(), "20070413_court_st_interior" ) );
        //Album a= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(), "bcVideoPam" ) );
        //Album a= Album.createFromDirectory( new File( Configuration.getImageDatabaseRoot(), "20070219_newContentTypes" ) );
        AlbumIndex index= new AlbumIndex(a);
        long t0= System.currentTimeMillis();
        index.makeIndex();
        System.err.println( System.currentTimeMillis()-t0 );
        
    }
}
