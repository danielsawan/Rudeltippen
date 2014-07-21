package dtos;


/**
 * 
 * @author svenkubiak
 *
 */
public class PasswordDTO {
    private String token;
    private String userpass;
    private String userpassConfirmation;
    
    public PasswordDTO() {
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUserpass() {
        return userpass;
    }
    
    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }
    
    public String getUserpassConfirmation() {
        return userpassConfirmation;
    }
    
    public void setUserpassConfirmation(String userpassConfirmation) {
        this.userpassConfirmation = userpassConfirmation;
    }
}