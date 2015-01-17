package jaw.Core;

import com.sun.crypto.provider.SunJCE;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Savonin on 2014-11-08
 */
public class PasswordEncryptor {

	static {
		Security.addProvider(new SunJCE());
	}

	/**
	 * Generate session's identifier via PRNG (20 bytes)
	 * @return - String in hex format with random 20 bytes
	 * @throws Exception
	 */
	public static String generateSessionID() throws Exception {

		// Initialize secure random with default PRGN
		SecureRandom secureRandom = new SecureRandom();

		// Create 20 bytes block
		byte[] bytes = new byte[20];

		// Engine this bytes
		secureRandom.nextBytes(bytes);

		// Generate seed
		byte[] seed = secureRandom.generateSeed(20);

		// Encode and return as string
		return HexBin.encode(seed);
	}

	/**
	 * Encrypt user's password with it's name as salt
	 * @param userName - User's name
	 * @param userPassword - User's password
	 * @return - Crypted password
	 * @throws Exception
	 */
    public static String crypt(String userName, String userPassword) throws Exception {

        Cipher cipher;

		// clone password
		char[] passwordArray = new char[userPassword.length()];

		// copy char array
		userPassword.getChars(0, userPassword.length(), passwordArray, 0);

		// generate special key with password
		PBEKeySpec keySpec
			= new PBEKeySpec(passwordArray);

		// get secret key factory based on PDE with MD5 and DES
		SecretKeyFactory secretKeyFactory
				= null;
		try {
			secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("PasswordEncryptor/crypt() : \"" + e.getMessage() + "\"");
		}

		// generate new secret key
		SecretKey secretKey = null;
		try {
			secretKey = secretKeyFactory.generateSecret(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new Exception("PasswordEncryptor/crypt() : \"" + e.getMessage() + "\"");
		}

		byte[] salt = SALT.clone();

		for (int i = 0; i < userName.length() && i < 8; i++) {
			salt[i] ^= userName.charAt(i);
		}

		// create parameter specific
		PBEParameterSpec parameterSpec
			= new PBEParameterSpec(salt, ITERATIONS);

		// get cipher
		try {
			cipher = Cipher.getInstance("PBEWithMD5AndDES");
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("PasswordEncryptor/crypt() : \"" + e.getMessage() + "\"");
		} catch (NoSuchPaddingException e) {
			throw new Exception("PasswordEncryptor/crypt() : \"" + e.getMessage() + "\"");
		}

		// initialize cipher
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
		} catch (InvalidKeyException e) {
			throw new Exception("PasswordEncryptor/crypt() : \"" + e.getMessage() + "\"");
		} catch (InvalidAlgorithmParameterException e) {
			throw new Exception("PasswordEncryptor/crypt() : \"" + e.getMessage() + "\"");
		}

		byte[] bytes;
		try {
			bytes = cipher.doFinal();
		} catch (IllegalBlockSizeException e) {
			throw new Exception("PasswordEncryptor/crypt() : \"" + e.getMessage() + "\"");
		} catch (BadPaddingException e) {
			throw new Exception("PasswordEncryptor/crypt() : \"" + e.getMessage() + "\"");
		}

		StringBuilder stringBuffer =
                new StringBuilder(bytes.length * 2);

		for (byte aByte : bytes) {
			int value = aByte & 0xff;
			if (value < 16) {
				stringBuffer.append('0');
			} else {
				stringBuffer.append(Integer.toHexString(value));
			}
		}

        return stringBuffer.toString().toLowerCase();
    }

	/**
	 * DES3 Salt
	 */
	private static final byte[] SALT = {
		(byte) 0xf5, (byte) 0x33, (byte) 0x01, (byte) 0x2a,
		(byte) 0xb2, (byte) 0xcc, (byte) 0xe4, (byte) 0x7f
	};

	/**
	 * Count of iterations for DES3
	 */
	private static final int ITERATIONS = 10;
}
