package main;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilities {

	public static int byteArrayToInt(byte[] b){
		
		return b[0] & 0xFF | (b[1] & 0xFF) << 8;
		
	}

	public static byte[] intToByteArray(int a) {

		return new byte[] { (byte) ((a >> 8) & 0xFF), (byte) (a & 0xFF) };
	}
	
	public static byte[] join(byte[] part1, byte[] part2) {

		byte[] all = new byte[part1.length + part2.length];

		System.arraycopy(part1, 0, all, 0, part1.length);
		System.arraycopy(part2, 0, all, part1.length, part2.length);

		return all;
	}
	
	public static String bytesToHex(byte[] in) {
	    final StringBuilder builder = new StringBuilder();
	    for(byte b : in) {
	        builder.append(String.format("%02x", b));
	        builder.append(":");
	    }
	    return builder.toString();
	}
}