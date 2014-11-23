package Core;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Security;
import com.sun.crypto.provider.SunJCE;

/**
 * Created by Savonin on 2014-11-08
 */
public class PasswordEncryptor extends UserSecurity {

	static {
		Security.addProvider(new SunJCE());
	}

	/**
	 * @param user - Reference to security's user
	 */
	public PasswordEncryptor(User user) {
		super(user);
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
				= SecretKeyFactory.getInstance("PBEWithMD5AndDES");

		// generate new secret key
		SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);

		byte[] salt = SALT.clone();

		for (int i = 0; i < userName.length() && i < 8; i++) {
			salt[i] ^= userName.charAt(i);
		}

		// create parameter specific
		PBEParameterSpec parameterSpec
			= new PBEParameterSpec(salt, ITERATIONS);

		// get cipher
		cipher = Cipher.getInstance("PBEWithMD5AndDES");

		// initialize cipher
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

		byte[] bytes = cipher.doFinal();

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
