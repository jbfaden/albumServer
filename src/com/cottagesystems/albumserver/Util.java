/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cottagesystems.albumserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author jbf
 */
public class Util {

    public static void copy(InputStream in, OutputStream out) throws IOException {
        final int BUFF_SIZE = 100000;
        final byte[] buffer = new byte[BUFF_SIZE];

        int bytesRead = in.read(buffer, 0, BUFF_SIZE);
        while (bytesRead != -1) {
            out.write(buffer, 0, bytesRead);
            bytesRead = in.read(buffer, 0, BUFF_SIZE);
        }

    }

    /**
     * return the part of the string after the last dot.
     * @param name, foo.dat
     * @return "dat"
     */
    public static String getExt( String name ) {
        int i= name.lastIndexOf(".");
        return name.substring(i+1);
    }
}
