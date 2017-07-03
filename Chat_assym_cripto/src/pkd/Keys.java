package pkd;

import java.util.*;
import java.security.*;
import java.security.spec.*;


public class Keys {
	
	protected KeyPair _kpair;
	
	public Keys() throws NoSuchAlgorithmException {
		
		 KeyPairGenerator kpairg = KeyPairGenerator.getInstance("RSA");
         kpairg.initialize(1024);
         _kpair = kpairg.genKeyPair();
         
         //Key publicKey = kpair.getPublic();
         //Key privateKey = kpair.getPrivate();
	}
	
	public Key getPrivate() {
		return _kpair.getPrivate();
	}
	
	public Key getPublic() {
		return _kpair.getPublic();
	}
	
	//get the public key encoded to byte array
	//which can be send via sockets
	public byte[] getPublicEncoded() throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		Key publicKey = getPublic();
		
		 //Key factory, for key-key specification transformations
        KeyFactory kfac = KeyFactory.getInstance("RSA");
        //Generate plain-text key specification
        RSAPublicKeySpec keyspec = (RSAPublicKeySpec)kfac.getKeySpec(publicKey, RSAPublicKeySpec.class);
        
		System.out.println("Public key, RSA modulus: " +
			keyspec.getModulus() + "\nexponent: " +
			keyspec.getPublicExponent() + "\n");
		
		//Building public key from the plain-text specification
		//Key recoveredPublicFromSpec = kfac.generatePublic(keyspec);
		
		//Encode a version of the public key in a byte-array
	
		System.out.print("Public key encoded in " +
		_kpair.getPublic().getFormat() + " format: ");
		byte[] encodedPublicKey = _kpair.getPublic().getEncoded();
		
		System.out.println(Arrays.toString(encodedPublicKey) + "\n");
		
		System.out.println("Length: " + encodedPublicKey.length);
		return encodedPublicKey;
	}
	
	//decodes the public key form byte array
	public static Key decodePublicKey(byte[] encodedPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		//Key factory, for key-key specification transformations
        KeyFactory kfac = KeyFactory.getInstance("RSA");
        
		//Building public key from the byte-array
		X509EncodedKeySpec ksp = new X509EncodedKeySpec(encodedPublicKey);
		Key recoveredPublicFromArray = kfac.generatePublic(ksp);
		
		return recoveredPublicFromArray;
	}

}
