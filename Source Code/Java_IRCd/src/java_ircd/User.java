package java_ircd;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* Hostmask format:     nick!ident@hostname
 * User info syntax:    nick!ident@hostname (description)
 *
 * Where:
 * nick                 -> Nickname used in the Chat by the user of a specific Client.
 * ident                -> Identity/Username of a specific Client on the specified host (hostname).
 * hostname             -> Hostname/IP-Address. The real name of the host that a specific Client is running on.
 * description          -> A description set by the user of a specific Client.
 *
 */
public class User {

    //================================================================================
    // Fields
    //================================================================================
    private String tempEntryNick;
    private String nick;
    private String ident;
    private String hostname; // Has a maximum length of 63 characters.
    private String description;
    //private String connectedTo; // The server to which the client is connected.
    private static final String ALGORITHM = "AES"; // AES 128 (maybe try 196-bit or 256-bit AES)
    //private static final byte[] keySeed = new byte[]{'T', 0x77, 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'}; // 128 bits key
    private static final byte[] KEY_SEED = Config.getKeySeed().getBytes(); // 128 bits key

    //================================================================================
    // Constructors
    //================================================================================
    public User() {
    }

    //================================================================================
    // Accessors and Mutators (Getters and Setters)
    //================================================================================
    public String getTempEntryNick() {
        return tempEntryNick;
    }

    public String getNick() {
        return nick;
    }

    public String getIdent() {
        return ident;
    }

    public String getHostname() {
        return hostname;
    }

    public String getDescription() {
        return description;
    }

    public void setTempEntryNick(String tempEntryNick) {
        this.tempEntryNick = tempEntryNick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //================================================================================
    // User Methods
    //================================================================================

    public String getHostmask() throws Exception {
        return nick + "!" + ident + "@" + encryptHostname(hostname).toUpperCase(); // Returns a hostmask.
    }

    public static String encryptHostname(String hostname) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(hostname.getBytes());

        /* Base64 character set: [A-Za-z0-9+/] */

        String encryptedValue = Base64.encode(encVal);  // Encodes a binary array as text using Base64.

        /* Format the 24-bits encrypted hostname output */

        String s1 = encryptedValue.substring(0, 6); // Perhaps scramble chars in the substrings?
        String s2 = encryptedValue.substring(6, 12);
        String s3 = encryptedValue.substring(12, 18);
        String s4 = encryptedValue.substring(18, 24);
        String output = s1 + "." + s2 + "." + s3 + "." + s4 + ".IP";

        return output;
    }

    public static String decryptHostname(String encryptedData) throws Exception {

        String[] ggw = encryptedData.split("\\."); // Regex for "."
        encryptedData = ggw[0] + ggw[1] + ggw[2] + ggw[3];

        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);

        byte[] decordedValue = Base64.decode(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(KEY_SEED, ALGORITHM);
        return key;
    }

    public static String generateTemporaryUserID() {

        int min = 11111;
        int max = 99999;

        String id = "ID" + (min + (int) (Math.random() * ((max - min) + 1)));

        synchronized (Connection.MUTEX) {

            while (Connection.temporaryIDs.contains(id)) {
                id = "ID" + (min + (int) (Math.random() * ((max - min) + 1)));
            }

            Connection.temporaryIDs.add(id);
        }

        return id;
    }
}
