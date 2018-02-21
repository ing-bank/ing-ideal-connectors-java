package com.ing.ideal.connector.util;

import com.ing.ideal.connector.ErrorCodes;
import com.ing.ideal.connector.IdealException;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class used to get message strings.
 * Uses a bundle named strings from the same java package.
 */
public class Strings {

    /**
     * Resource bundle.
     */
    private static ResourceBundle stringBundle;

    private Strings() {
    }

    /**
     * Internal method used to initialize the resource bundle;
     *
     * @return loaded resource bundle
     * @throws IdealException
     */
    private static synchronized ResourceBundle getBundle() throws IdealException {
        if (stringBundle == null) {
            if (Util.log.isDebugEnabled())
                Util.log.debug("  Initialising Strings");
            try {
                String bundleName = Strings.class.getPackage().getName().replace('.', '/') + "/strings";
                stringBundle = ResourceBundle.getBundle(bundleName);
            } catch (Exception e) {
                IdealException ex = new IdealException(ErrorCodes.IMEXME01, "Unable to load string catalog.", e);
                ex.setSuggestedAction("Distribution is corrupt, redownload from the idealdesk. ");
                throw ex;
            }
        }
        return stringBundle;
    }

    /**
     * Creates the message with the given key and given parameters.
     *
     * @param key    message key
     * @param params optional parameters to be used in the message
     * @return formatted message or the key if no message was found
     * @throws IdealException if the resource bundle could not be initialized
     */
    public static String getString(String key, Object... params) throws IdealException {
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< getString(String, Object[]) :: String");
        if (Util.log.isTraceEnabled())
            Util.log.trace(String.format("   key: %s  ;   params: %s", key, Arrays.toString(params)));
        ResourceBundle stringBundle = getBundle();
        String value = null;
        if (StringUtils.trimToNull(key) != null) {
            try {
                value = stringBundle.getString(key);
                if (params != null && params.length > 0)
                    value = MessageFormat.format(value, params);
            } catch (MissingResourceException mre) {
                Util.log.debug(getString(ErrorCodes.IMEXME02, key));
                value = key;
            }
        }
        if (Util.log.isTraceEnabled())
            Util.log.trace("   value: " + value);
        if (Util.log.isTraceEnabled())
            Util.log.trace("<< getString(String, Object[]) :: String");
        return value;
    }

}