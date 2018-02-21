package com.ing.ideal.connector.util;

import com.ing.ideal.connector.IdealException;

/**
 * Utility class that handles exception creation.
 */
public class ExceptionHelper {

    public static IdealException createException(String errorCode, Object... params) throws IdealException {
        IdealException ex = new IdealException(errorCode, Strings.getString(errorCode, params));
        setSuggestedAction(ex);
        return ex;
    }

    public static IdealException createException(String errorCode, Throwable cause, Object... params) throws IdealException {
        IdealException ex = new IdealException(errorCode, Strings.getString(errorCode, params), cause);
        setSuggestedAction(ex);
        return ex;
    }

    public static IdealException createException(String errorCode, String message, Throwable cause) {
        return new IdealException(errorCode, message, cause);
    }

    public static IdealException createException(String errorCode, String message, String action, Throwable cause) {
        IdealException ex = new IdealException(errorCode, message, cause);
        ex.setSuggestedAction(action);
        return ex;
    }

    private static void setSuggestedAction(IdealException exception) throws IdealException {
        String actionKey = exception.getErrorCode() + ".action";
        String action = Strings.getString(actionKey);
        if (!actionKey.equals(action)) {
            exception.setSuggestedAction(action);
        }
    }
}
