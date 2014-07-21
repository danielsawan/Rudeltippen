package dtos;

/**
 * 
 * @author svenkubiak
 *
 */
public class UserDTO {
    private String username;
    private String userpass;
    private String email;
    private String userpassConfirmation;
    private String emailConfirmation;
    
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUserpassConfirmation() {
        return userpassConfirmation;
    }
    
    public void setUserpassConfirmation(String userpassConfirmation) {
        this.userpassConfirmation = userpassConfirmation;
    }
    
    public String getEmailConfirmation() {
        return emailConfirmation;
    }
    
    public void setEmailConfirmation(String emailConfirmation) {
        this.emailConfirmation = emailConfirmation;
    }
}