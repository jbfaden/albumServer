/*
 * AccessBean.java
 *
 * Created on February 15, 2007, 2:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

import java.util.ArrayList;

/**
 *
 * @author jbf
 */
public class AccessBean {
    
    public static AccessBean PUBLIC= new AccessBean( "anonymous" );
    public static AccessBean LIMITED_PUBLIC = new AccessBean( "limitedPublic" );
    public static AccessBean CFUPUBLIC= new AccessBean( "cfu" );
    public static AccessBean UIPUBLIC= new AccessBean( "uiowa" );
    public static AccessBean INTRANET= new AccessBean( "intranet" );

    String username;
    ArrayList<String> roles;
    
    public static final String ROLE_BROWSE_PUBLIC= "browsePublic";

    /**
     * user may read restricted content, but may not make modifications to records
     */
    public static final String ROLE_LIMITED_PUBLIC= "limitedPublic";

    /**
     * user may modify albums
     */
    public static final String ROLE_AUTHOR= "albumAuthor";
    
    /** Creates a new instance of AccessBean */
    public AccessBean( String username ) {
        this.username= username;
        roles= new ArrayList<String>();
        roles.add( ROLE_BROWSE_PUBLIC );
    }
    
    public String getUsername() {
        return username;
    }
    
    public void addRole( String role ) {
        this.roles.add( role );
    }
    
    public boolean hasRole( String role ) {
        return roles.contains(role);
    }

    public String toString() {
        return username+" "+roles;
    }
}
