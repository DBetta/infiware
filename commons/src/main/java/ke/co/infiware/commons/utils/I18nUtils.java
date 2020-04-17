package ke.co.infiware.commons.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author Denis Gitonga
 */
public class I18nUtils {
    private static MessageSource messageSource;

    public I18nUtils(MessageSource messageSource) {
        I18nUtils.messageSource = messageSource;
    }

    /**
     *  Get passed message from messages.properties
     * @param messageKey the key for the message
     * @param args the list of arguments
     * @return passed message
     */
    public static String getMessage(final String messageKey, final Object... args) {
        if (messageSource == null)
            return "ApplicationContext unavailable, probably unit test going on";

        // http://stackoverflow.com/questions/10792551/how-to-obtain-a-current-user-locale-from-spring-without-passing-it-as-a-paramete
        return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
    }
}
