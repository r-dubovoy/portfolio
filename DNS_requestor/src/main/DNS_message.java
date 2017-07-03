package main;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.BitSet;

public class DNS_message {

	public static final String reqTXT = "TXT";
	
	public static final String reqA = "A";

	public static final String reqAAAA = "AAAA";
	
	public static final String reqMX = "MX";

	public static final String reqCNAME = "CNAME";


	private static byte[] _buffer = null;

	public DNS_message(String query, String domain)
			throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		// *** Build a DNS Request Frame ****

		// Identifier
		dos.writeShort(0x1234);
		
		// Write Query Flags
		BitSet bs = new BitSet(16);
		bs.set(8);
		byte [] flags = bs.toByteArray();
		
		dos.write(flags);

		// Question Count
		dos.writeShort(0x0001);

		// Answer Record Count
		dos.writeShort(0x0000);

		// Authority Record Count
		dos.writeShort(0x0000);

		// Additional Record Count
		dos.writeShort(0x0000);

		// Domain
		System.out.println(domain);
		String[] domainParts = domain.split("\\.");
		System.out.println(domain + " has " + domainParts.length + " parts");

		for (int i = 0; i < domainParts.length; i++) {
			System.out.println("Writing: " + domainParts[i]);
			byte[] domainBytes = domainParts[i].getBytes("UTF-8");
			dos.writeByte(domainBytes.length);
			dos.write(domainBytes);
		}

		// No more parts
		dos.writeByte(0x00);

		// Query type
		switch (query) {
		case reqAAAA:
			
			dos.writeShort(0x001C);
			break;
			
		case reqA:
			dos.writeShort(0x0001);
			break;
			
		case reqTXT:
			dos.writeShort(0x0010);
			break;
		
		case reqMX:
			dos.writeShort(0x0015);
			break;
		
		case reqCNAME:
			dos.writeShort(0x0050);
			break;
			
			
			
		}
		// Class 0x01 = IN
		dos.writeShort(0x0001);

		_buffer = baos.toByteArray();

        System.out.println("Sending: " + _buffer.length + " bytes");
        for (int i =0; i< _buffer.length; i++) {
            System.out.print("0x" + String.format("%x", _buffer[i]) + " " );
        }
	}
	

	public byte[] getMessage(){
		
		return _buffer;
	}
}
