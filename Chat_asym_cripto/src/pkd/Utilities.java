package pkd;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilities {
	
	public static int byteArrayToInt(byte[] b) {
		
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}

	public static byte[] intToByteArray(int a) {
		
	    return new byte[] {
	        (byte) ((a >> 24) & 0xFF),
	        (byte) ((a >> 16) & 0xFF),   
	        (byte) ((a >> 8) & 0xFF),   
	        (byte) (a & 0xFF)
	    };
	}
	
	public static byte[] join(byte[] part1, byte[] part2) {
		
	    byte[] all = new byte[part1.length + part2.length];

	    System.arraycopy(part1, 0, all, 0, part1.length);
	    System.arraycopy(part2, 0, all, part1.length, part2.length);

	    return all;
	}
	
	public static byte[] host2md5(String ip, int port) throws NoSuchAlgorithmException {
		
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update((ip + Integer.toString(port)).getBytes());
		return md5.digest();
	}
	
	public static String host2md5hex(String ip, int port) throws NoSuchAlgorithmException {
		
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update((ip + Integer.toString(port)).getBytes());
		return bytes2hex(md5.digest());
	}
	
	public static String bytes2hex(byte[] digest) {
		
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}
	
	public static byte[] hex2bytes(String s) {
		int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

}
