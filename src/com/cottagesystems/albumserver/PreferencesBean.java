/*
 * PreferencesBean.java
 *
 * Created on February 15, 2007, 7:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class PreferencesBean {

    public static final String INDEX_SHUFFLE="shuffle";
    public static final String INDEX_STRAIGHT="straight";
            
    /** Creates a new instance of PreferencesBean */
    public PreferencesBean() {
    }

    /**
     * Holds value of property indexType.
     */
    private String indexType= INDEX_SHUFFLE;

    /**
     * Getter for property indexType.
     * @return Value of property indexType.
     */
    public String getIndexType() {
        return this.indexType;
    }

    /**
     * Setter for property indexType.
     * @param indexType New value of property indexType.
     */
    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }
    
}
