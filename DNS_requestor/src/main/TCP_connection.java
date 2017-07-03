package main;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.BitSet;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class TCP_connection {

	public static final String reqA = "A";

	public static final String reqAAAA = "AAAA";

	public static final String reqTXT = "TXT";

	public static final String reqMX = "MX";

	public static final String reqCNAME = "CNAME";

	public static void main(String args[]) throws IOException {

		InetAddress ipAddress = InetAddress.getByName(args[1]);

		Socket dns_connection = new Socket(ipAddress, 53);
		DataOutputStream dOut = new DataOutputStream(dns_connection.getOutputStream());

		DNS_message request = new DNS_message(args[2], args[0]);
		byte[] buffer = Utilities.intToByteArray(request.getMessage().length);
		buffer = Utilities.join(buffer, request.getMessage());

		dOut.write(buffer);

		byte[] input_buffer = new byte[1024];

		DataInputStream dIn = new DataInputStream(dns_connection.getInputStream());
		dIn.read(input_buffer);

		DataInputStream din = new DataInputStream(new ByteArrayInputStream(input_buffer));

		System.out.println("\n");

		byte[] flags = { input_buffer[4], input_buffer[5] };
		BitSet bs = BitSet.valueOf(flags);

		System.out.println(Integer.toBinaryString(flags[0] & 0xFF));
		System.out.println(Integer.toBinaryString(flags[1] & 0xFF));

		// check if truncated
		if (Integer.toBinaryString(flags[1] & 0xFF).equals("10000010")) {

			Interface.setOutPut("Server failure");

		}

		else {

			System.out.println("Transaction ID: 0x" + String.format("%x", din.readShort()));
			System.out.println("Flags: 0x" + String.format("%x", din.readShort()));
			System.out.println("Questions: 0x" + String.format("%x", din.readShort()));
			Short answers = din.readShort();
			System.out.println("Answers RRs: 0x" + String.format("%x", answers));
			Short authorities = din.readShort();
			System.out.println("Authority RRs: 0x" + String.format("%x", authorities));
			Short additional = din.readShort();
			System.out.println("Additional RRs: 0x" + String.format("%x", additional));

			if (answers == 0) {
				Interface.setOutPut("No answer for the given query");
			}

			for (int k = 0; k < answers; k++) {

				String rec = "";

				int recLen = 0;
				while ((recLen = din.readByte()) > 0) {
					byte[] record = new byte[recLen];

					for (int i = 0; i < recLen; i++) {
						record[i] = din.readByte();
					}
					rec += new String(record, "UTF-8") + ".";
				}

				System.out.println("Record: " + rec);

				System.out.println("Record Type: 0x" + String.format("%x", din.readShort()));
				System.out.println("Class: 0x" + String.format("%x", din.readShort()));

				System.out.println("Field: 0x" + String.format("%x", din.readShort()));
				System.out.println("Type: 0x" + String.format("%x", din.readShort()));
				System.out.println("Class: 0x" + String.format("%x", din.readShort()));
				System.out.println("TTL: 0x" + String.format("%x", din.readInt()));

				short addrLen = din.readShort();
				System.out.println("Len: 0x" + String.format("%x", addrLen));

				switch (args[2]) {
				case reqA:
					Interface.setOutPut(args[2] + ": ");
					for (int i = 0; i < 4; i++) {
						System.out.print("" + String.format("%d", (din.readByte() & 0xFF)) + ".");
					}
					
					break;
				case reqAAAA:
					Interface.setOutPut(args[2] + ": ");
					byte[] ipv6 = new byte[16];
					for (int i = 0; i < ipv6.length; i++) {
						ipv6[i] = din.readByte();
					}
					Interface.setOutPut(Utilities.bytesToHex(ipv6));

					break;
				case reqTXT:
					Interface.setOutPut(args[2] + ": ");
					String recTXT = "";
					
					int recLenTXT = 0;
					while ((recLenTXT = din.readByte()) > 0){
						byte[] record = new byte[recLenTXT];
						
						for (int i = 0; i < recLenTXT; i++){
							record[i] = din.readByte();
						}
						recTXT += new String(record, "UTF-8") + ".";
					}
					
					Interface.setOutPut(recTXT);
					break;

				case reqMX:
					Interface.setOutPut(args[2] + ": ");
					String recMX = "";

					int recLenMX = 0;
					while ((recLenMX = din.readByte()) > 0) {
						byte[] record = new byte[recLenMX];

						for (int i = 0; i < recLenMX; i++) {
							record[i] = din.readByte();
						}
						recMX += new String(record, "UTF-8") + ".";
					}

					Interface.setOutPut(recMX);
					break;

				case reqCNAME:
					Interface.setOutPut(args[2] + ": ");

					String recCNAME = "";

					int recLenCNAME = 0;
					while ((recLenCNAME = din.readByte()) > 0) {
						byte[] record = new byte[recLenCNAME];

						for (int i = 0; i < recLenCNAME; i++) {
							record[i] = din.readByte();
						}
						recCNAME += new String(record, "UTF-8") + ".";
					}

					Interface.setOutPut(recCNAME);

					break;

				}
			}
		}
	}
}
