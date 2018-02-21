package com.ing.ideal.connector.util;

import com.ing.ideal.connector.ErrorCodes;
import com.ing.ideal.connector.IdealException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class.
 */
public class Util {
    /**
     * Format for the date that is being sent.
     */
    public static final String INTERFACE_ISO8601_DATETIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss'.0Z'";
    private static final FastDateFormat iso8601DateFormat = FastDateFormat.getInstance(INTERFACE_ISO8601_DATETIMESTAMP, TimeZone.getTimeZone("UTC"));

    public static final Log log = LogFactory.getLog("IdealConnector");

    public Util() {
    }

    /**
     * <p>Deletes all whitespaces from a String as defined by
     * {@link Character#isWhitespace(char)}.</p>
     * <p/>
     * <pre>
     * StringUtils.deleteWhitespace(null)         = null
     * StringUtils.deleteWhitespace("")           = ""
     * StringUtils.deleteWhitespace("abc")        = "abc"
     * StringUtils.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param value the String to delete whitespace from, may be null
     * @return the String without whitespaces, {@code null} if null String input
     */
    public static String deleteWhitespace(String value) {
        if (log.isTraceEnabled())
            log.trace(">> deleteWhitespace(String) :: String");
        value = StringUtils.deleteWhitespace(value);
        if (log.isTraceEnabled())
            log.trace("<< deleteWhitespace(String) :: String");
        return value;
    }

    /**
     * Encodes current date according to ISO8601 and UTC timezone.
     *
     * @return string representation date
     */
    public static String createDateTimestamp() {
        if (log.isTraceEnabled())
            log.trace(">> createDateTimestamp() :: String");
        String dateTimeStamp = iso8601DateFormat.format(new Date());
        if (log.isTraceEnabled()) {
            log.trace("   formatPattern: " + iso8601DateFormat.getPattern());
            log.trace("   dateTimeStamp: " + dateTimeStamp);
        }
        if (log.isTraceEnabled())
            log.trace("<< createDateTimestamp() :: String");
        return dateTimeStamp;
    }

    /**
     * Encodes the given string as UTF-8 and removes all \0 .
     *
     * @param param string
     * @return encoded string
     * @throws IdealException in case of {@link CharacterCodingException}
     */
    public static String encodeString(String param) throws IdealException {
        if (log.isTraceEnabled())
            log.trace(">> encodeString(String) :: String");
        String dump;
        try {
            CharsetEncoder enc = Charset.forName("UTF-8").newEncoder();
            ByteBuffer bytes = enc.encode(CharBuffer.wrap(param.toCharArray()));
            dump = new String(bytes.array());
            dump = dump.replaceAll("\0", "");
        } catch (CharacterCodingException e) {
            throw ExceptionHelper.createException(ErrorCodes.IMEXEN01, e, "UTF-8");
        }
        if (log.isTraceEnabled())
            log.trace("<< encodeString(String) :: String");
        return dump;
    }

}