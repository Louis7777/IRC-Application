package java_ircd;

import java.util.HashMap;

/* This class is used solely to store Internet Protocol (IP) addresses,
 * in order to prevent multiple connections to the server.
 */
public class IPTables {

    //================================================================================
    // Fields
    //================================================================================
    public static HashMap<String, Long> IPTable = new HashMap<String, Long>(100); // 100 addresses capacity.
    public static final long TIMEOUT = 1000; // 1 second.

    //================================================================================
    // Constructors
    //================================================================================
    public IPTables() {
    }

    //================================================================================
    // IPTables Methods
    //================================================================================

    /* Provide this method with an IP to see if the IP can continue. */
    public static boolean canContinue(String IP) {
        Long lastTime = IPTable.get(IP); // Get the duration that the IP has been stored.
        long now = System.currentTimeMillis();

        if (lastTime == null) {
            IPTable.put(IP, now); // Put the IP in the table.
            return true;
        }
        if (now - lastTime > TIMEOUT) {
            IPTable.put(IP, now); // Put the IP in the table.
            return true;
        }
        return false;
    }

    public static void printIPTable() {

        for (String con : IPTable.keySet()) {
            System.out.println(con);
        }
    }
}
