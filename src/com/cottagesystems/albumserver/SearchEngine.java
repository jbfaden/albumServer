/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cottagesystems.albumserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbf
 */
public class SearchEngine {

    List<String> keywords = new ArrayList<String>();

    int score(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        int score = 0;

        String s = reader.readLine();
        while (s != null) {
            for (String key : keywords) {
                if (s.toLowerCase().contains(key)) {
                    score = score + 1;
                }
            }
            s = reader.readLine();
        }

        reader.close();
        
        return score;

    }

    public int doSearch(File file, Album album, int count, int limit) {


        if (count >= limit) {
            album.setListed(true);
            return count;
        }

        File[] kidFolders = file.listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        File[] metaFiles = file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });

        try {
            int n = Configuration.getImageDatabaseRoot().length();

            if (count >= limit) {
                album.setListed(true);
                return count;
            }

            if ( metaFiles!=null ) {
                for (int i = 0; i < metaFiles.length; i++) {
                    if (score(metaFiles[i]) > 0) {
                        String id = metaFiles[i].toString().substring(n);
                        id = id.replaceAll("\\\\", "/");
                        id = id.substring(0, id.length() - 4);
                        Media m = Media.createMedia(id);

                        if ( m==null ) continue; // kludge, we're getting hits on access files, etc.

                        count++;
                        if (count == limit) {
                            break;
                        }

                        album.add(m);
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if ( kidFolders!=null ) {
            for (int i = 0; i < kidFolders.length; i++) {
                System.err.println(kidFolders[i]);
                count = doSearch(kidFolders[i], album, count, limit );
            }
        }

        album.setListed(true);
        return count;

    }

    public void addKeyword(String keyword) {
        keywords.add(keyword.toLowerCase());
    }
}
