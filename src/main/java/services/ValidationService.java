package services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Confirmation;
import models.User;
import models.enums.Constants;
import ninja.validation.ConstraintViolation;
import ninja.validation.FieldViolation;
import ninja.validation.Validation;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import dtos.SettingsDTO;
import dtos.UserDTO;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class ValidationService {
    private static final Pattern PATTERN = Pattern.compile(Constants.EMAILPATTERN.asString());
    private static final String USERPASS = "userpass";
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";

    @Inject
    private DataService dataService;

    @Inject
    private I18nService i18nService;

    /**
     * Checks in the database and pending confirmations if a given email already exists
     * 
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(final String email) {
        boolean exists = false;
        final List<Confirmation> confirmations = dataService.findAllConfirmation();
        for (final Confirmation confirmation : confirmations) {
            String value = confirmation.getConfirmValue();
            if (value.equalsIgnoreCase(email)) {
                exists = true;
            }
        }

        if (!exists) {
            final User user = dataService.findUserByEmail(email);
            if (user != null) {
                exists = true;
            }
        }

        return exists;
    }

    /**
     * Checks in the database if a username already exists
     * 
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(final String username) {
        final User user = dataService.findUserByUsername(username);
        return user != null;
    }

    public void validateUserDTO(UserDTO userDTO, Validation validation) {
        if (userDTO != null) {
            if (!isValidUsername(userDTO.getUsername())) {
                validation.addBeanViolation(createBeanValidation(USERNAME, i18nService.get("validation.username.size")));
            }

            if (usernameExists(userDTO.getUsername())) {
                validation.addBeanViolation(createBeanValidation(USERNAME, i18nService.get("validation.username.exists")));
            }

            if (!isValidEmail(userDTO.getEmail())) {
                validation.addBeanViolation(createBeanValidation(EMAIL, i18nService.get("validation.email.invalid")));
            }

            if (emailExists(userDTO.getEmail())) {
                validation.addBeanViolation(createBeanValidation(EMAIL, i18nService.get("validation.email.exsits")));
            }

            if (!match(userDTO.getEmail(), userDTO.getEmailConfirmation())) {
                validation.addBeanViolation(createBeanValidation(EMAIL, i18nService.get("validation.email.notmatch")));
            }

            if (!isValidPassword(userDTO.getUserpass())) {
                validation.addBeanViolation(createBeanValidation(USERPASS, i18nService.get("validation.password.invalid")));
            }

            if (!match(userDTO.getUserpass(), userDTO.getUserpassConfirmation())) {
                validation.addBeanViolation(createBeanValidation(USERPASS, i18nService.get("validation.password.notmatch")));
            }
        } else {
            validation.addBeanViolation(createBeanValidation(USERNAME, i18nService.get("validation.username.size")));
        }
    }

    public void validateSettingsDTO(SettingsDTO settingsDTO, Validation validation) {
        if (settingsDTO != null) {
            if (StringUtils.isEmpty(settingsDTO.getName()) || settingsDTO.getName().length() <= 3 || settingsDTO.getName().length() >= 256) {
                validation.addBeanViolation(createBeanValidation("name", i18nService.get("validation.settings.invalidname")));
            }
            
            if (settingsDTO.getPointsTip() <= 0 || settingsDTO.getPointsTip() >= 100) {
                validation.addBeanViolation(createBeanValidation("pointsTip", i18nService.get("validation.settings.invalidpointstip")));
            }
            
            if (settingsDTO.getPointsTipDiff() <= 0 || settingsDTO.getPointsTipDiff() >= 100) {
                validation.addBeanViolation(createBeanValidation("pointsTipDiff", i18nService.get("validation.settings.invalidpointstipdiff")));
            }
            
            if (settingsDTO.getPointsTipTrend() <= 0 || settingsDTO.getPointsTipTrend() >= 100) {
                validation.addBeanViolation(createBeanValidation("pointsTipTrend", i18nService.get("validation.settings.invalidpointstiptrend")));
            }
            
            if (settingsDTO.getMinutesBeforeTip() <= 0 || settingsDTO.getMinutesBeforeTip() > 1440) {
                validation.addBeanViolation(createBeanValidation("minutesBeforeTip", i18nService.get("validation.settings.invalidminutes")));
            }
        } else {
            validation.addBeanViolation(createBeanValidation("name", i18nService.get("validation.settings.invalidname")));
        }
    }

    /**
     * Checks if the given filesize is lower or equal than configured in application.conf
     * 
     * @param filesize The filesize to check
     * @return true if filesiize is lower or equal given filesize, false otherwise
     */
    public boolean checkFileLength(final Long filesize) {
        boolean check = false;
        if ((filesize > 0) && (filesize <= 102400)) {
            check = true;
        }

        return check;
    }

    /**
     * Checks if given homeScore and awayScore is casteble to string and between 0 and 99
     * 
     * @param homeScore The homeScore to check
     * @param awayScore The awayScore to check
     * @return true if score is valid, false otherwise
     */
    public boolean isValidScore(String homeScore, String awayScore) {
        boolean valid = false;
        
        if (StringUtils.isNotBlank(homeScore) && StringUtils.isNotBlank(awayScore) && StringUtils.isNumeric(homeScore) && StringUtils.isNumeric(awayScore)) {
            int home = Integer.parseInt(homeScore);
            int away = Integer.parseInt(awayScore);

            if ((home >= 0 && home <= 99) && (away >= 0 && away <= 99)) {
                valid = true;
            }
        }

        return valid;
    }

    /**
     * Checks if a given email is matching defined EMAILPATTERN
     * 
     * @param email The email to check
     * @return true if email is valid, false otherwise
     */
    public boolean isValidEmail(final String email) {
        boolean valid = false;
        if (StringUtils.isNotBlank(email)) {
            final Matcher m = PATTERN.matcher(email);
            if (m.matches()) {
                valid = true;
            }
        }

        return valid;
    }

    /**
     * Checks if a given username is matching defined USERNAMEPATTERN
     * 
     * @param username The username to check
     * @return true if username is valid, false otherwise
     */
    public boolean isValidUsername(final String username) {
        boolean valid = false;
        final Pattern p = Pattern.compile(Constants.USERNAMEPATTERN.asString());
        final Matcher m = p.matcher(username);

        if (StringUtils.isNotBlank(username) && username.length() >= 3 && username.length() <= 32 && m.matches()) {
            valid = true;
        }

        return valid;
    }

    public boolean isValidConfirmationToken(String token) {
        final Pattern p = Pattern.compile(Constants.CONFIRMATIONPATTERN.asString());
        final Matcher m = p.matcher(token);

        return m.matches();
    }
    
    public boolean isValidObjectId(String id) {
        final Pattern p = Pattern.compile(Constants.OBJECTIDPATTERN.asString());
        final Matcher m = p.matcher(id);

        return m.matches();
    }

    public boolean match(String string1, String string2) {
        boolean valid = true;
        if (StringUtils.isNotBlank(string1) && !string1.equals(string2)) {
            valid = false;
        }

        return valid;
    }

    public FieldViolation createBeanValidation(String field, String message) {
        ConstraintViolation constraintViolation = new ConstraintViolation(message,"","");
        return new FieldViolation(field, constraintViolation);
    }

    public boolean isValidPassword(String userpass) {
        return StringUtils.isNotBlank(userpass) && userpass.length() >= 8 && userpass.length() <= 32;
    }
}