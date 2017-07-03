package pkd;

import java.util.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Encryptor {
	
	protected Key _publicKey = null;
	protected Cipher _cipher = null;
	
	public Encryptor(Key publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{
		_publicKey = publicKey;
		
		_cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		_cipher.init(Cipher.ENCRYPT_MODE, _publicKey);
	}
	
	public Encryptor(byte[] publicKeyEncoded) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException
	{
		_publicKey = Keys.decodePublicKey(publicKeyEncoded);
		
		_cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		_cipher.init(Cipher.ENCRYPT_MODE, _publicKey);
	}
	
	public byte[] encrypt(byte[] message) throws IllegalBlockSizeException, BadPaddingException {
		
		byte[] out = null;
		int start = 0;
		int end = 0;
		//maximal size of block size can be passed to cipher is 117 bytes
		//so we need to split long messages
		//each iterations generates the block of 128 cryted bytes
		while (start < message.length) {
			
			end = (start + 117 < message.length) ? (start + 117) : message.length;
			
			byte[] block = new byte[end-start];
			System.arraycopy(message, start, block, 0, end-start);
			byte[] encrypted = _cipher.doFinal(block);
			
			out = (out == null) ? encrypted : Utilities.join(out, encrypted);
			start = end;
		}
		return out;
				
		//return  _cipher.doFinal(message);
	}

}
