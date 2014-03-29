package com.arcblaze.arccore.common.model;

import static org.apache.commons.lang.Validate.notEmpty;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * A utility class used to perform password operations.
 */
public class Password {
	/** The hash algorithm. */
	public final static String HASH_ALGORITHM = "SHA-512";

	/**
	 * @param password
	 *            the new password for which a hash will be generated
	 * @param salt
	 *            the salt value to use when hashing the password
	 * 
	 * @return the hashed value in the form of a hex string
	 * 
	 * @throws IllegalArgumentException
	 *             if the parameters are invalid
	 */
	public String hash(final String password, final String salt) {
		notEmpty(password, "Invalid empty password");
		notEmpty(salt, "Invalid empty salt");

		try {
			final MessageDigest messageDigest = MessageDigest
					.getInstance(HASH_ALGORITHM);
			return toHexString(messageDigest.digest((salt + password)
					.getBytes()));
		} catch (NoSuchAlgorithmException badHashAlgorithm) {
			// Not expecting this to happen.
			return password;
		}
	}

	private String toHexString(final byte[] bytes) {
		final char[] hex = "0123456789abcdef".toCharArray();
		final StringBuilder sb = new StringBuilder(bytes.length << 1);

		for (final byte b : bytes)
			sb.append(hex[(b & 0xf0) >> 4]).append(hex[(b & 0x0f)]);

		return sb.toString();
	}

	/**
	 * @param length
	 *            the length of the password to generate
	 * 
	 * @return a randomly generated password of the specified length
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided length is not valid
	 */
	public String random(final int length) {
		if (length < 0 || length > 255)
			throw new IllegalArgumentException("Invalid length: " + length);

		final String chars = "aeuAEU23456789bdghjmnpqrstvzBDGHJLMNPQRSTVWXZ";

		final Random random = new Random();
		final StringBuilder password = new StringBuilder();
		for (int i = 0; i < length; i++)
			password.append(chars.charAt(random.nextInt(chars.length())));
		return password.toString();
	}

	/**
	 * @return a randomly generated password
	 */
	public String random() {
		return random(14);
	}
}
