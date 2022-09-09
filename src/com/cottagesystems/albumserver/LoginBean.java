/*
 * LoginBean.java
 *
 * Created on February 11, 2007, 10:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.cottagesystems.albumserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jbf
 */
public class LoginBean {

    private static final Logger logger= Logger.getLogger("albumServer");
            
    private String userName;
    private String password1;
    private final HashMap<String,String> errors;

    /**
     * limit the number of reloads to once per 10 seconds, because of a
     * lack of other security.
     */
    private static long lastReload= 0;
    
    /**
     * contains list of username/password combinations.
     */
    private static Properties passwords;
    
    /**
     * contains list of IP addresses known to be safe.
     */
    private static Properties ipaccess;

    static {
        reload();
    }
    
    public static void reload() {
        if ( System.currentTimeMillis()-lastReload > 5000 ) {
            readPasswords();
            readIpAccess();          
            lastReload= System.currentTimeMillis();
        } else {
            throw new IllegalArgumentException("Unable to reload, because it was done recently.");
        }
    }
    
    public boolean validate() {
        boolean allOk = true;
        if (userName.equals("")) {
            errors.put("userName", "Please enter a username");
            userName = "";
            allOk = false;
        }
        if (password1.equals("")) {
            errors.put("password1", "Please enter a valid password");
            password1 = "";
            allOk = false;
        }
        return allOk;
    }

    public String getErrorMsg(String s) {
        String errorMsg = (String) errors.get(s.trim());
        return (errorMsg == null) ? "" : errorMsg;
    }

    public LoginBean() {
        userName = "";
        password1 = "";
        errors = new HashMap<>();
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password1;
    }

    public void setUsername(String u) {
        userName = u;
    }

    public void setPassword(String p1) {
        password1 = p1;
    }

    private static void readPasswords() {
        File cacheRoot= new File( Configuration.getCacheRoot() );
        if ( !cacheRoot.exists() ) {
            if ( !cacheRoot.mkdirs() ) {
                throw new IllegalArgumentException("unable to make cacheRoot");
            }
        }
        File passwordFile=  new File( Configuration.getCacheRoot() + "/passwords.txt" );
        if ( !passwordFile.exists() ) {
            try ( FileWriter fw= new FileWriter(passwordFile) ) {
                fw.append("# This list contains username password combinations.");
                fw.append("# username=password\n");
                fw.append("# grandmaHome=x\n");
            } catch (IOException ex) {
                throw new IllegalArgumentException("Unable to write limitedPublic file at "+passwordFile);
            }
            passwordFile.setReadable( false,false );
            passwordFile.setReadable( true,true );
        }
        passwords= new Properties();
        try ( InputStream in= new FileInputStream(passwordFile) ) {
            passwords.load(in);
        } catch ( IOException ex) {
            throw new IllegalArgumentException("Expected to find password file at "+passwordFile);
        } 
        
    }
    
    private static void readIpAccess() {
        File limitedPublicFile=  new File( Configuration.getCacheRoot() + "/ipaccess.txt" );
        if ( !limitedPublicFile.exists() ) {
            try ( FileWriter fw= new FileWriter(limitedPublicFile) ) {
                fw.append("# This list contains IP addresses that will resolve to x or a username.\n");
                fw.append("# When a username is found for the IP, then grant user-level access.\n");
                fw.append("# This allows family members to access without having to remember passwords.\n");
                fw.append("# 0.0.0.0=x\n");
                fw.append("# 0.0.0.0=grandmaHome\n");
            } catch (IOException ex) {
                throw new IllegalArgumentException("Unable to write ipaccess file at "+limitedPublicFile);
            }
            limitedPublicFile.setReadable( false,false );
            limitedPublicFile.setReadable( true,true );
        }
        ipaccess= new Properties();
        try ( InputStream in= new FileInputStream(limitedPublicFile) ) {
            ipaccess.load(in);
        } catch ( IOException ex) {
            throw new IllegalArgumentException("Expected to find limitedPublic file at "+limitedPublicFile);
        } 
        for ( Object o: ipaccess.keySet() ) {
            String k= (String)o;
            int i= k.split("\\.").length;
            if ( i<3 || i>4 ) {
                throw new IllegalArgumentException("ipaccess.txt file can only contain x.x.x or x.x.x.x for ips.");
            }
        }
    }
    
    /**
     * return the access associated with this request.
     * @param request
     * @return 
     */
    public AccessBean getAccessBean(HttpServletRequest request) {
        AccessBean bean;

        // test access by ip table.
        String addressKey= request.getRemoteAddr();
        int i= addressKey.lastIndexOf('.');
        if ( i==-1 && addressKey.equals("0:0:0:0:0:0:0:1") ) {
            addressKey= "localhost";
            i= addressKey.length();
        }
        if ( ( ipaccess.containsKey( addressKey ) || ipaccess.containsKey(addressKey.substring(0,i) ) ) ) {
            String testUser= ipaccess.getProperty(request.getRemoteAddr() );
            // allow automatic log-in by IP address.
            if ( testUser!=null && !testUser.equals("x") && passwords.contains(testUser) ) {
                bean= new AccessBean(testUser);
                bean.addRole(AccessBean.ROLE_AUTHOR);
            } else {
                bean = AccessBean.LIMITED_PUBLIC;
                bean.addRole(AccessBean.ROLE_LIMITED_PUBLIC);
            }
        } else {
            bean = AccessBean.PUBLIC;
        }
        
        String accessKey= request.getParameter("access");
        if ( accessKey!=null && accessKey.length()>0 && Character.isDigit(accessKey.charAt(0) ) ) {
            bean= new AccessBean(accessKey);
        }

        String passNeeded= passwords.getProperty(getUsername());
        if ( passNeeded!=null && !passNeeded.equals("x") ) {
            if ( passNeeded.equals(getPassword()) ) {
                bean = new AccessBean(getUsername());
                bean.addRole(AccessBean.ROLE_AUTHOR);
            }
        }

        return bean;

    }
}
