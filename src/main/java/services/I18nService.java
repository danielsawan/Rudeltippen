package services;

import org.apache.commons.lang.StringUtils;

import models.enums.Constants;
import ninja.i18n.Messages;
import ninja.utils.NinjaProperties;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class I18nService {

    @Inject
    private Messages messages;

    @Inject
    private NinjaProperties ninjaProperties;

    public String get(String key, Object [] paramters) {
        Optional<String> language = Optional.of(getDefaultLanguage());
        return messages.getWithDefault(key, "Language: " + language + " - Missing translation for key: " + key, language, paramters);
    }

    public String get(String key) {
        return get(key, new Object[]{});
    }

    /**
     * Returns a Message with the difference to place 1
     * 
     * @param pointsDiff The difference in points
     * @return Message with difference or empty string
     */
    public String getDiffToTop(final int pointsDiff) {
        String message = "";
        if (pointsDiff == 1) {
            message = get("points.to.top.one", new Object[]{pointsDiff});
        } else if (pointsDiff > 1) {
            message = get("points.to.top.many", new Object[]{pointsDiff});
        }

        return message;
    }

    /**
     * Returns the current timezone configured in application.conf or
     * the default timezone defined in IAppConstants
     * 
     * @return The String value of the timezone
     */
    public String getCurrentTimeZone() {
        String timezone = ninjaProperties.get("rudeltippen.timezone");
        if (StringUtils.isBlank(timezone)) {
            timezone = Constants.DEFAULT_TIMEZONE.get();
        }
        return timezone;
    }

    public String getDefaultLanguage() {
        String language = ninjaProperties.get("rudeltippen.language");
        if (StringUtils.isNotBlank(language)) {
            if (("en").equalsIgnoreCase(language)) {
                language = "en";
            } else if (("de").equalsIgnoreCase(language)) {
                language = "de";
            }
        } else {
            language = "en";
        }

        return language;
    }
}