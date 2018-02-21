package com.ing.ideal.connector.crypto;

import com.ing.ideal.connector.crypto.impl.Base64;
import com.ing.ideal.connector.crypto.impl.XOR;


/**
 * Class used to get the encrypted password that sits in the config files.
 */
public class PasswordUtility {
    /**
     * Prefix that gets added to the encrypted password.
     */
    private static final String PREFIX = "{xor}";

    private PasswordUtility() {
    }

    /**
     * Encrypts a password string.
     *
     * @param plainText plain text password
     * @return encrypted password
     */
    public static String encode(String plainText) {
        byte xor[] = XOR.encipher(plainText.getBytes());
        String result = Base64.encodeBytes(xor);
        return PREFIX + result;
    }

    /**
     * Decrypts an encoded password.
     *
     * @param encoded encoded password
     * @return password as plain text
     */
    public static String decode(String encoded) {
        encoded = encoded.substring(PREFIX.length());
        byte xor[] = Base64.decode(encoded);
        byte text[] = XOR.decipher(xor);
        return new String(text);
    }

    public static void main(String args[]) {
        if (args.length == 1) {  //encrypt password
            String input = args[0];
            System.out.println("Generating new encoded password value:");
            String encoded = encode(input);
            System.out.println("    output : " + encoded);
        } else {    //print usage
            System.out.println(
                    "Encrypts a password tobe used with iDEAL library. \n"
                            + "Usage: <java> "
                            + (com.ing.ideal.connector.crypto.PasswordUtility.class).getName()
                            + " <password>");
        }
    }
}