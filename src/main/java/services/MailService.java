package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Extra;
import models.Game;
import models.Settings;
import models.User;
import models.enums.ConfirmationType;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.utils.NinjaProperties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class MailService {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);
    private static final String MESSAGE = "message";
    private static final String SETTINGS = "settings";
    private static final String GAMES = "games";
    private static final String I18N = "i18n";

    @Inject
    private Provider<Mail> mailProvider;

    @Inject
    private Postoffice postoffice;

    @Inject
    private NinjaProperties ninjaProperties;

    @Inject
    private I18nService i18nService;

    @Inject
    private DataService dataService;

    @Inject
    private ValidationService validationService;

    @Inject
    private CommonService commonService;

    public void reminder(final User user, final List<Game> games, final List<Extra> extras) {
        final Settings settings = dataService.findSettings();
        final String recipient = user.getEmail();

        if (validationService.isValidEmail(recipient)) {
            Mail mail = getMailInstance(settings, recipient, StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + i18nService.get("mails.subject.reminder")));

            Map<String, Object> content = new HashMap<String, Object>();
            content.put("user", user);
            content.put(GAMES, games);
            content.put(I18N, i18nService);
            content.put(SETTINGS, settings);
            content.put("extras", extras);
            mail.setBodyHtml(commonService.getProcessedTemplate("reminder.ftl", content));

            try {
                postoffice.send(mail);
            } catch (Exception e) {
                LOG.error("Failed to send reminder e-mail", e);
            }
        } else {
            LOG.error("Tryed to sent reminder, but recipient was invalid.");
        }
    }

    public void confirm(final User user, final String token, final ConfirmationType confirmationType) {
        final Settings settings = dataService.findSettings();

        if ((user != null) && validationService.isValidEmail(user.getEmail()) && StringUtils.isNotBlank(token) && (confirmationType != null)) {
            String subject = "";
            String message = "";

            if (ConfirmationType.ACTIVATION.equals(confirmationType)) {
                subject = i18nService.get("mails.subject.activate");
                message = i18nService.get("mails.message.activate");
            } else if (ConfirmationType.CHANGEUSERNAME.equals(confirmationType)) {
                subject = i18nService.get("mails.subject.changeusername");
                message = i18nService.get("mails.message.changeusername");
            } else if (ConfirmationType.CHANGEUSERPASS.equals(confirmationType)) {
                subject = i18nService.get("mails.subject.changeuserpass");
                message = i18nService.get("mails.message.changeuserpass");
            } else if (ConfirmationType.NEWUSERPASS.equals(confirmationType)) {
                subject = i18nService.get("mails.subject.forgotuserpass");
                message = i18nService.get("mails.message.forgotuserpass");
            }
            message = StringEscapeUtils.unescapeHtml(message);
            Mail mail = getMailInstance(settings, user.getEmail(), StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + subject));

            Map<String, Object> content = new HashMap<String, Object>();
            content.put(I18N, i18nService);
            content.put("appUrl", ninjaProperties.get("rudeltippen.url"));
            content.put("user", user);
            content.put("token", token);
            content.put(MESSAGE, message);
            mail.setBodyText(commonService.getProcessedTemplate("confirm.ftl", content));

            try {
                postoffice.send(mail);
            } catch (Exception e) {
                LOG.error("Failed to send confirm e-mail", e);
            }
        } else {
            LOG.error("Tryed to sent confirmation e-mail, but user or confirmType was null or recipient e-mail was invalid.");
        }
    }

    public void newuser(final User user, final User admin) {
        final Settings settings = dataService.findSettings();
        if (validationService.isValidEmail(admin.getEmail()) && (user != null)) {
            Mail mail = getMailInstance(settings, user.getEmail(), StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + i18nService.get("mails.subject.newuser")));

            Map<String, Object> content = new HashMap<String, Object>();
            content.put("user", user);
            content.put(I18N, i18nService);
            content.put(SETTINGS, settings);
            mail.setBodyText(commonService.getProcessedTemplate("newuser.ftl", content));

            try {
                postoffice.send(mail);
            } catch (Exception e) {
                LOG.error("Failed to send Reminder e-mail", e);
            }
        } else {
            LOG.error("Tryed to sent new user e-mail to admin, but recipient was invalid or user was null.");
        }
    }

    public void error(final String response, final String recipient) {
        final Settings settings = dataService.findSettings();

        if (validationService.isValidEmail(recipient) && StringUtils.isNotBlank(response)) {
            Mail mail = getMailInstance(settings, recipient, StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + i18nService.get("mails.subject.updatefailed")));

            Map<String, Object> content = new HashMap<String, Object>();
            content.put("response", response);
            content.put(I18N, i18nService);
            mail.setBodyText(commonService.getProcessedTemplate("error.ftl", content));

            try {
                postoffice.send(mail);
            } catch (Exception e) {
                LOG.error("Failed to send error e-mail", e);
            }
        } else {
            LOG.error("Tryed to sent info on webservice, but recipient was invalid or response was null.");
        }
    }

    public void notifications(final String subject, String notification, final User user) {
        final Settings settings = dataService.findSettings();
        if (validationService.isValidEmail(user.getEmail()) && StringUtils.isNotEmpty(notification)) {
            Mail mail = getMailInstance(settings, user.getEmail(), StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + subject));

            Map<String, Object> content = new HashMap<String, Object>();
            content.put("notification", notification);
            content.put(I18N, i18nService);
            mail.setBodyText(commonService.getProcessedTemplate("notification.ftl", content));

            try {
                postoffice.send(mail);
            } catch (Exception e) {
                LOG.error("Failed to send notifications e-mail", e);
            }
        } else {
            LOG.error("Tryed to sent result notification, but recipient was invalid or notification was null.");
        }
    }

    public void gametips(final User user, final List<Game> games) {
        final Settings settings = dataService.findSettings();

        if (validationService.isValidEmail(user.getEmail()) && !games.isEmpty()) {
            Mail mail = getMailInstance(settings, user.getEmail(), StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + i18nService.get("overview")));

            Map<String, Object> content = new HashMap<String, Object>();
            content.put(I18N, i18nService);
            content.put(GAMES, games);
            content.put("user", user);
            mail.setBodyHtml(commonService.getProcessedTemplate("gametips.ftl", content));

            try {
                postoffice.send(mail);
            } catch (Exception e) {
                LOG.error("Failed to send gametips e-mail", e);
            }
        } else {
            LOG.error("Tryed to sent gametips mail, but recipient was invalid or games list was empty.");
        }
    }

    public void rudelmail(final String subject, final String message, final String [] bbcRecipients, String recipient) {
        final Settings settings = dataService.findSettings();

        if (StringUtils.isNotBlank(subject) && StringUtils.isNotBlank(message) && (bbcRecipients != null)) {
            Mail mail = getMailInstance(settings, recipient, StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + subject));

            Map<String, Object> content = new HashMap<String, Object>();
            content.put(MESSAGE, message);
            mail.setBodyText(commonService.getProcessedTemplate("rudelmail.ftl", content));
            mail.addBcc(bbcRecipients);

            try {
                postoffice.send(mail);
            } catch (Exception e) {
                LOG.error("Failed to send rudelmail e-mail", e);
            }
        } else {
            LOG.error("Tryed to sent rudelmail, but subject, messages or recipients was empty");
        }
    }

    private Mail getMailInstance(final Settings settings, final String recipient, String subject) {
        Mail mail = mailProvider.get();
        mail.setCharset("UTF-8");
        mail.setFrom(ninjaProperties.get("rudeltippen.mail.from"));
        mail.addReplyTo(ninjaProperties.get("rudeltippen.mail.replyto"));
        mail.addTo(recipient);
        mail.setSubject(subject);

        return mail;
    }
}