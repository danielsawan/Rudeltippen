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
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.DataService;
import services.I18nService;
import services.MailService;
import services.ValidationService;

import com.google.inject.Inject;

import de.svenkubiak.ninja.auth.services.Authentications;
import de.svenkubiak.ninja.validation.NinjaValidator;
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
    private ValidationService validationService;

    @Inject
    private MailService mailService;

    @Inject
    private Authentications authentications;

    @Inject
    private I18nService i18nService;
    
    @Inject
    private NinjaValidator validations;

    public Result password(@PathParam("token") String token, FlashScope flashScope) {
        final Confirmation confirmation = dataService.findConfirmationByToken(token);
        if (confirmation == null) {
            flashScope.put(Constants.FLASHWARNING.asString(), i18nService.get(INVALIDTOKEN));
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
            confirmation.setCreated(new Date());
            dataService.save(confirmation);

            mailService.confirm(user, token, confirmType);
            flashScope.success(i18nService.get("confirm.message"));

            return Results.redirect(AUTH_LOGIN);
        }
    }

    public Result confirm(@PathParam("token") String token, FlashScope flashScope, Session session) {
        Confirmation confirmation = null;

        if (!validationService.isValidConfirmationToken(token)) {
            flashScope.put(Constants.FLASHWARNING.asString(), i18nService.get(INVALIDTOKEN));
        } else {
            confirmation = dataService.findConfirmationByToken(token);
        }

        if (confirmation != null) {
            final User user = confirmation.getUser();
            final ConfirmationType confirmationType = confirmation.getConfirmationType();
            if (ConfirmationType.NEWUSERPASS.equals(confirmationType)) {
                return Results.redirect("/auth/password/" + token);
            } else {
                if ((ConfirmationType.ACTIVATION).equals(confirmationType)) {
                    user.setActive(true);
                    dataService.save(user);
                    dataService.delete(confirmation);
                    
                    flashScope.success(i18nService.get("controller.users.accountactivated"));
                    LOG.info("User activated: " + user.getEmail());
                } else if ((ConfirmationType.CHANGEUSERNAME).equals(confirmationType)) {
                    final String oldusername = user.getEmail();
                    final String newusername = confirmation.getConfirmValue();
                    user.setEmail(newusername);
                    dataService.save(user);
                    session.remove(Constants.USERNAME.asString());
                    dataService.delete(confirmation);

                    flashScope.success(i18nService.get("controller.users.changedusername"));
                    LOG.info("User changed username... old username: " + oldusername + " - " + "new username: " + newusername);
                } else if ((ConfirmationType.CHANGEUSERPASS).equals(confirmationType)) {
                    user.setUserpass(confirmation.getConfirmValue());
                    dataService.save(user);
                    session.remove("username");
                    dataService.delete(confirmation);

                    flashScope.success(i18nService.get("controller.users.changeduserpass"));
                    LOG.info(user.getEmail() + " changed his password");
                }
            }
        } else {
            flashScope.put(Constants.FLASHWARNING.asString(), i18nService.get(INVALIDTOKEN));
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
            user.setUserpass(authentications.getHashedPassword(userDTO.getUserpass()));
            user.setPoints(0);
            user.setPicture(DigestUtils.md5Hex(userDTO.getEmail()));
            dataService.save(user);

            final String token = UUID.randomUUID().toString();
            final ConfirmationType confirmationType = ConfirmationType.ACTIVATION;
            final Confirmation confirmation = new Confirmation();
            confirmation.setConfirmationType(confirmationType);
            confirmation.setCreated(new Date());
            confirmation.setToken(token);
            confirmation.setUser(user);
            dataService.save(confirmation);

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
            flashScope.put(Constants.FLASHWARNING.asString(), i18nService.get(INVALIDTOKEN));
            return Results.redirect(AUTH_LOGIN);
        }

        final User user = confirmation.getUser();
        user.setUserpass(authentications.getHashedPassword(passwordDTO.getUserpass()));
        dataService.save(user);

        dataService.delete(confirmation);
        flashScope.success(i18nService.get("controller.auth.passwordreset"));

        return Results.redirect(AUTH_LOGIN);
    }

    public Result authenticate(Context context, LoginDTO login, FlashScope flashScope) {
        validations.required("username", login.getUsername());
        validations.required("userpass", login.getUserpass());
        
        if (validations.hasErrors()) {
            return Results.html().render(VALIDATION, validations).render("settings", dataService.findSettings()).template("/views/AuthController/login.ftl.html");
        } else {
            User user = dataService.findUserByUsernameOrEmail(login.getUsername());
            if (user != null && authentications.authenticate(login.getUserpass(), user.getUserpass())) {
                authentications.login(context, user.getUsername(), false);
                return Results.redirect("/");
            }
        }

        return Results.redirect(AUTH_LOGIN);
    }

    public Result logout(Context context, FlashScope flashScope){
        authentications.logout(context);
        flashScope.success(i18nService.get("controller.auth.logout"));

        return Results.redirect(AUTH_LOGIN);
    }
}