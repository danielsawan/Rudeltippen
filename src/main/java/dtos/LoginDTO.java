package dtos;

/**
 * 
 * @author svenkubiak
 *
 */
public class LoginDTO {
    private String username;
    private String userpass;
    private boolean remember;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserpass() {
        return userpass;
    }
    
    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }
    
    public boolean isRemember() {
        return remember;
    }
    
    public void setRemember(boolean remember) {
        this.remember = remember;
    }
}