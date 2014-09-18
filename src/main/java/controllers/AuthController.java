package controllers;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import models.Confirmation;
import models.Settings;
import models.User;
import models.enums.ConfirmationType;
import models.enums.Constants;
import ninja.Context;
import ninja.Cookie;
import ninja.FilterWith;
import ninja.NinjaValidator;
import ninja.Result;
import ninja.Results;
import ninja.mongodb.MongoDB;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.AuthService;
import services.DataService;
import services.I18nService;
import services.MailService;
import services.ValidationService;

import com.google.inject.Inject;

import dtos.LoginDTO;
import dtos.PasswordDTO;
import dtos.UserDTO;
import filters.AppFilter;

/**
 * 
 * @author svenkubiak
 *
 */
@FilterWith(AppFilter.class)
public class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private static final String VALIDATION = "validations";
    private static final String AUTH_LOGIN = "/auth/login";
    private static final String INVALIDTOKEN = "controller.users.invalidtoken";

    @Inject
    private DataService dataService;
    
    @Inject
    private MongoDB mongoDB;

    @Inject
    private ValidationService validationService;

    @Inject
    private MailService mailService;

    @Inject
    private AuthService authService;

    @Inject
    private I18nService i18nService;
    
    @Inject
    private NinjaValidator validations;

    public Result password(@PathParam("token") String token, FlashScope flashScope) {
        final Confirmation confirmation = dataService.findConfirmationByToken(token);
        if (confirmation == null) {
            flashScope.put(Constants.FLASHWARNING.get(), i18nService.get(INVALIDTOKEN));
            return Results.redirect(AUTH_LOGIN);
        }

        return Results.html().render("token", token);
    }

    public Result login() {
        Settings settings = dataService.findSettings();
        return Results.html().render(settings);
    }

    public Result reset(Context context, FlashScope flashScope) {
        final String email = context.getParameter("email");
        if (!validationService.isValidEmail(email)) {
            flashScope.error(i18nService.get("controller.auth.resenderror"));

            return Results.redirect("/auth/forgotten");
        } 
        final User user = dataService.findUserByEmailAndActive(email);
        
        if (user == null) {
            flashScope.error(i18nService.get("controller.auth.resenderror"));

            return Results.redirect("/auth/forgotten");
        } else {
            final String token = UUID.randomUUID().toString();
            final ConfirmationType confirmType = ConfirmationType.NEWUSERPASS;
            final Confirmation confirmation = new Confirmation();
            confirmation.setUser(user);
            confirmation.setToken(token);
            confirmation.setConfirmationType(confirmType);
            confirmation.setConfirmValue(authService.encryptAES(UUID.randomUUID().toString()));
            confirmation.setCreated(new Date());
            mongoDB.save(confirmation);

            mailService.confirm(user, token, confirmType);
            flashScope.success(i18nService.get("confirm.message"));

            return Results.redirect(AUTH_LOGIN);
        }
    }

    public Result confirm(@PathParam("token") String token, FlashScope flashScope, Session session) {
        Confirmation confirmation = null;

        if (!validationService.isValidConfirmationToken(token)) {
            flashScope.put(Constants.FLASHWARNING.get(), i18nService.get(INVALIDTOKEN));
        } else {
            confirmation = dataService.findConfirmationByToken(token);
        }

        if (confirmation != null) {
            final User user = confirmation.getUser();
            if (user != null) {
                final ConfirmationType confirmationType = confirmation.getConfirmationType();
                if (ConfirmationType.NEWUSERPASS.equals(confirmationType)) {
                    return Results.redirect("/auth/password/" + token);
                } else {
                    if ((ConfirmationType.ACTIVATION).equals(confirmationType)) {
                        user.setActive(true);
                        mongoDB.save(user);
                        mongoDB.delete(confirmation);
                        
                        flashScope.success(i18nService.get("controller.users.accountactivated"));
                        LOG.info("User activated: " + user.getEmail());
                    } else if ((ConfirmationType.CHANGEUSERNAME).equals(confirmationType)) {
                        final String oldusername = user.getEmail();
                        final String newusername = authService.decryptAES(confirmation.getConfirmValue());
                        user.setEmail(newusername);
                        mongoDB.save(user);
                        session.remove(Constants.USERNAME.get());
                        flashScope.success(i18nService.get("controller.users.changedusername"));
                        mongoDB.delete(confirmation);

                        LOG.info("User changed username... old username: " + oldusername + " - " + "new username: " + newusername);
                    } else if ((ConfirmationType.CHANGEUSERPASS).equals(confirmationType)) {
                        user.setUserpass(authService.decryptAES(confirmation.getConfirmValue()));
                        mongoDB.save(user);
                        session.remove("username");
                        flashScope.success(i18nService.get("controller.users.changeduserpass"));
                        mongoDB.delete(confirmation);

                        LOG.info(user.getEmail() + " changed his password");
                    }
                }
            } else {
                flashScope.put(Constants.FLASHWARNING.get(), i18nService.get(INVALIDTOKEN));
            }
        } else {
            flashScope.put(Constants.FLASHWARNING.get(), i18nService.get(INVALIDTOKEN));
        }

        return Results.redirect(AUTH_LOGIN);
    }

    public Result register() {
        final Settings settings = dataService.findSettings();
        if (!settings.isEnableRegistration()) {
            return Results.redirect("/");
        }

        return Results.html();
    }

    public Result create(@JSR303Validation UserDTO userDTO, Validation validation) {
        final Settings settings = dataService.findSettings();
        if (!settings.isEnableRegistration()) {
            return Results.redirect("/");
        }

        validationService.validateUserDTO(userDTO, validation);

        if (validation.hasBeanViolations()) {
            return Results.html().render("user", userDTO).render(VALIDATION, validation).template("/views/AuthController/register.ftl.html");
        } else {
            final String salt = DigestUtils.sha512Hex(UUID.randomUUID().toString());
            final User user = new User();
            user.setRegistered(new Date());
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setActive(false);
            user.setReminder(true);
            user.setSendStandings(true);
            user.setSendGameTips(true);
            user.setNotification(true);
            user.setAdmin(false);
            user.setSalt(salt);
            user.setUserpass(authService.hashPassword(userDTO.getUserpass(), salt));
            user.setPoints(0);
            user.setPicture(DigestUtils.md5Hex(userDTO.getEmail()));
            mongoDB.save(user);

            final String token = UUID.randomUUID().toString();
            final ConfirmationType confirmationType = ConfirmationType.ACTIVATION;
            final Confirmation confirmation = new Confirmation();
            confirmation.setConfirmationType(confirmationType);
            confirmation.setConfirmValue(authService.encryptAES(UUID.randomUUID().toString()));
            confirmation.setCreated(new Date());
            confirmation.setToken(token);
            confirmation.setUser(user);
            mongoDB.save(confirmation);

            mailService.confirm(user, token, confirmationType);
            if (settings.isInformOnNewTipper()) {
                final List<User> admins = dataService.findAllAdmins();
                for (final User admin : admins) {
                    mailService.newuser(user, admin);
                }
            }
            LOG.info("User registered: " + user.getEmail());
        }

        return Results.html().render(settings);
    }

    public Result forgotten() {
        return Results.html();
    }

    public Result renew(@JSR303Validation PasswordDTO passwordDTO, Validation validation, FlashScope flashScope) {
        if (validation.hasBeanViolations()) {
            return Results.html().render("passwordDTO", passwordDTO).render(VALIDATION, validation).template("/views/AuthController/rendew.ftl.html");
        }

        final Confirmation confirmation = dataService.findConfirmationByToken(passwordDTO.getToken());
        if (confirmation == null) {
            flashScope.put(Constants.FLASHWARNING.get(), i18nService.get(INVALIDTOKEN));
            return Results.redirect(AUTH_LOGIN);
        }

        final User user = confirmation.getUser();
        final String password = authService.hashPassword(passwordDTO.getUserpass(), user.getSalt());
        user.setUserpass(password);
        mongoDB.save(user);

        mongoDB.delete(confirmation);
        flashScope.success(i18nService.get("controller.auth.passwordreset"));

        return Results.redirect(AUTH_LOGIN);
    }

    public Result authenticate(Session session, LoginDTO login, FlashScope flashScope) {
        validations.required("username", login.getUsername());
        validations.required("userpass", login.getUserpass());
        
        if (validations.hasErrors()) {
            return Results.html().render(VALIDATION, validations).render("settings", dataService.findSettings()).template("/views/AuthController/login.ftl.html");
        } else {
            if (authService.authenticate(login.getUsername(), login.getUserpass())) {
                session.put(Constants.USERNAME.get(), login.getUsername());
                if (login.isRemember()) {
                    String signedUsername = authService.sign(login.getUsername()) + "-" + login.getUsername();
                    Cookie.builder(Constants.COOKIENAME.get(), signedUsername).setSecure(true).setHttpOnly(true).build();
                }

                return Results.redirect("/");
            }
        }

        return Results.redirect(AUTH_LOGIN);
    }

    public Result logout(Session session, FlashScope flashScope){
        session.clear();
        flashScope.success(i18nService.get("controller.auth.logout"));

        return Results.redirect(AUTH_LOGIN);
    }
}