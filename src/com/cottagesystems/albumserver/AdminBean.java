/*
 * AdminBean.java
 *
 * Created on February 11, 2007, 11:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.albumserver;

/**
 *
 * @author jbf
 */
public class AdminBean {
    
    /** Creates a new instance of AdminBean */
    public AdminBean() {

    }

    /**
     * Holds value of property Id.
     */
    private String id;

    /**
     * Getter for property Id.
     * @return Value of property Id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter for property Id.
     * @param Id New value of property Id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Holds value of property notes.
     */
    private String notes;

    /**
     * Getter for property notes.
     * @return Value of property notes.
     */
    public String getNotes() {
        return this.notes;
    }

    /**
     * Setter for property notes.
     * @param notes New value of property notes.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Holds value of property action.
     */
    private String action;

    /**
     * Getter for property action.
     * @return Value of property action.
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Holds value of property returnUrl.
     */
    private String returnUrl;

    /**
     * Getter for property returnUrl.
     * @return Value of property returnUrl.
     */
    public String getReturnUrl() {
        return this.returnUrl;
    }

    /**
     * Setter for property returnUrl.
     * @param returnUrl New value of property returnUrl.
     */
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
    
}
