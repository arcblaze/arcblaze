package com.arcblaze.arccore.common.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Perform testing on the password class.
 */
public class PasswordTest {
    /**
     * Test the {@link Password#hash(String, String)} method.
     */
    @Test
    public void testHash() {
        final String password = "pass";
        final String salt = "salt";
        final String hashedPass = new Password().hash(password, salt);
        final String correct = "4ab3490b9dd9fbcd6eb9ec6e2078a99c8de5d4d0ae"
                + "1371faad97fdc83774dbeeec52c971c41971f71b131587c1becb170" + "7435b24771d392631298647ba04e37d";

        assertEquals(correct, hashedPass);
    }
}
