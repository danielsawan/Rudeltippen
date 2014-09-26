package services;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import models.User;
import models.enums.Constants;
import ninja.utils.NinjaProperties;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mchange.v1.util.UnexpectedException;

/**
 * 
 * @author svenkubiak
 * Mainly based on the encryption class of the play framework
 *
 */
@Singleton
public class AuthService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String AES = "AES";
    private static final String APPLICATION_SECRET = "application.secret";

    @Inject
    private NinjaProperties ninjaProperties;

    @Inject
    private DataService dataService;

    @Inject
    private AuthService authService;

    /**
     * Encrypt a String with the AES encryption standard using the application secret
     * @param value The String to encrypt
     * @return An hexadecimal encrypted string
     */
    public String encryptAES(String value) {
        return encryptAES(value, ninjaProperties.get(APPLICATION_SECRET).substring(0, 16));
    }

    /**
     * Encrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     * @param value The String to encrypt
     * @param privateKey The key used to encrypt
     * @return An hexadecimal encrypted string
     */
    public String encryptAES(String value, String privateKey) {
        try {
            byte[] raw = privateKey.getBytes(Constants.ENCODING.asString());
            SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            return byteToHexString(cipher.doFinal(value.getBytes(Constants.ENCODING.asString())));
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Decrypt a String with the AES encryption standard using the application secret
     * @param value An hexadecimal encrypted string
     * @return The decrypted String
     */
    public String decryptAES(String value) {
        return decryptAES(value, ninjaProperties.get(APPLICATION_SECRET).substring(0, 16));
    }

    /**
     * Decrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     * @param value An hexadecimal encrypted string
     * @param privateKey The key used to encrypt
     * @return The decrypted String
     */
    public String decryptAES(String value, String privateKey) {
        try {
            byte[] raw = privateKey.getBytes(Constants.ENCODING.asString());
            SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            return new String(cipher.doFinal(hexStringToByte(value)), Charsets.UTF_8);
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    public byte[] hexStringToByte(String hexString) {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException e) {
            throw new UnexpectedException(e);
        }
    }

    public String byteToHexString(byte[] bytes) {
        return String.valueOf(Hex.encodeHex(bytes));
    }

    public boolean authenticate(String username, String userpass) {
        boolean authenticated = false;
        User user = dataService.findUserByUsernameOrEmail(username);
        if (user != null && user.getUserpass().equals(authService.hashPassword(userpass, user.getSalt()))) {
            authenticated = true;
        }

        return authenticated;
    }

    /**
     * Sign a message using the application secret key (HMAC-SHA1)
     * @throws UnsupportedEncodingException
     */
    public String sign(String message) {
        try {
            return sign(message, ninjaProperties.get(APPLICATION_SECRET).getBytes(Constants.ENCODING.asString()));
        } catch (UnsupportedEncodingException e) {
            LOG.error("Failed to sign message", e);
        }

        return null;
    }

    /**
     * Sign a message with a key
     * @param message The message to sign
     * @param key The key to use
     * @return The signed message (in hexadecimal)
     */
    public String sign(String message, byte[] key) {
        if (key.length == 0) {
            return message;
        }

        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1);
            mac.init(signingKey);
            byte[] messageBytes = message.getBytes(Constants.ENCODING.asString());
            byte[] result = mac.doFinal(messageBytes);
            int len = result.length;
            char[] hexChars = new char[len * 2];


            for (int charIndex = 0, startIndex = 0; charIndex < hexChars.length;) {
                int bite = result[startIndex++] & 0xff;
                hexChars[charIndex++] = HEX_CHARS[bite >> 4];
                hexChars[charIndex++] = HEX_CHARS[bite & 0xf];
            }
            return new String(hexChars);
        } catch (Exception e) {
            LOG.error("Failed to sign message with key", e);
        }

        return null;
    }

    /**
     * Hashes a given clear-text password with a given salt using 100000 rounds
     *
     * @param userpass The password
     * @param usersalt The salt
     * @return SHA512 hashed string
     */
    public String hashPassword(final String userpass, final String usersalt) {
        String hash = "";
        for (int i = 1; i <= 100000; i++) {
            hash = DigestUtils.sha512Hex(hash + userpass + usersalt);
        }

        return hash;
    }
}