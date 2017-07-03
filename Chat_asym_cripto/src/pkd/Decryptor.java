package pkd;

import java.security.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Decryptor {
	
	protected Key _privateKey = null;
	protected Cipher _cipher = null;
	
	public Decryptor(Key privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{
		_privateKey = privateKey;
		
		_cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		_cipher.init(Cipher.DECRYPT_MODE, _privateKey);
	}
	
	public byte[] decrypt(byte[] encodedMessage) throws IllegalBlockSizeException, BadPaddingException {
		
		byte[] out = null;
		int start = 0;
		int end = 0;
		//cipher decripts blocks of 128 bytes
		while (start < encodedMessage.length) {
			
			end = (start + 128 < encodedMessage.length) ? (start + 128) : encodedMessage.length;
			
			byte[] block = new byte[end-start];
			System.arraycopy(encodedMessage, start, block, 0, end-start);
			byte[] decrypted = _cipher.doFinal(block);
			
			out = (out == null) ? decrypted : Utilities.join(out, decrypted);
			start = end;
		}
		return out;
		
		//return _cipher.doFinal(encodedMessage);
	}

}
