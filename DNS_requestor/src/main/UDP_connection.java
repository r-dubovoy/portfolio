package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class UDP_connection {

	public static final String reqA = "A";

	public static final String reqAAAA = "AAAA";

	public static final String reqTXT = "TXT";

	public static final String reqMX = "MX";

	public static final String reqCNAME = "CNAME";

	public static void main(String[] args) throws IOException {
		try {

			System.out.println(args[0] + "/" + args[1]);
			DatagramSocket socket = new DatagramSocket();

			int id = 1;

			DNS_message request = new DNS_message(args[2], args[0]);
			System.out.println("\n");
			InetAddress ipAddress = InetAddress.getByName(args[1]);

			// InetAddress ipAddress =
			// InetAddress.getByName(inetAddress.getHostName());
			System.out.println(ipAddress.getHostAddress());

			DatagramPacket send = new DatagramPacket(request.getMessage(), request.getMessage().length, ipAddress, 53);
			socket.send(send);
			System.out.println("packet sent");
			/*
			 * byte[] req_data = request.getMessage(); byte[] req_flags =
			 * {req_data[2], req_data[3]};
			 * 
			 * BitSet req_bs = BitSet.valueOf(req_flags);
			 * 
			 * System.out.println("Req flags: " + req_bs.toString());
			 */
			byte[] buffer = new byte[1024];
			DatagramPacket recieve = new DatagramPacket(buffer, buffer.length);
			socket.receive(recieve);
			System.out.println("\n\nReceived: " + recieve.getLength() + " bytes");

			for (int i = 0; i < recieve.getLength(); i++) {
				System.out.print(" 0x" + String.format("%x", buffer[i]) + " ");
			}

			byte[] data = recieve.getData();
			byte[] flags = { data[2], data[3] };

			BitSet bs = BitSet.valueOf(flags);

			System.out.println("\n");

			System.out.println(Integer.toBinaryString(flags[0] & 0xFF));
			System.out.println(Integer.toBinaryString(flags[1] & 0xFF));

			// check if truncated
			if (Integer.toBinaryString(flags[1] & 0xFF).equals("10000010")) {

				Interface.setOutPut("Server failure");

			}

			else if (Integer.toBinaryString(flags[0] & 0xFF).equals("10000010")) {
				System.out.println("\n!!!!!!!!!!!!");
				Interface.setOutPut("truncation flag is true, connecting using TCP");
				System.out.println("!!!!!!!!!!!!\n");

				TCP_connection.main(args);
			}

			else {

				DataInputStream din = new DataInputStream(new ByteArrayInputStream(buffer));
				System.out.println("Transaction ID: 0x" + String.format("%x", din.readShort()));
				System.out.println("Flags: 0x" + String.format("%x", din.readShort()));
				System.out.println("Questions: 0x" + String.format("%x", din.readShort()));
				Short answers = din.readShort();
				System.out.println("Answers RRs: 0x" + String.format("%x", answers));
				Short authorities = din.readShort();
				System.out.println("Authority RRs: 0x" + String.format("%x", authorities));
				Short additional = din.readShort();
				System.out.println("Additional RRs: 0x" + String.format("%x", additional));

				System.out.println("\n");
				
				
				if(answers == 0){
					Interface.setOutPut("No answer for the given query");
				}
				
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

				
				for (int k = 0; k < answers; k++) {

					System.out.println("\n");
					
					
					
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
						String output = "";
						Interface.setOutPut(args[2] + ": ");
						for (int i = 0; i < 4; i++) {
							output += ("" + String.format("%d", (din.readByte() & 0xFF)) + ".");
						}
						Interface.setOutPut(output);
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
						while ((recLenMX = din.readByte()) > 0){
							byte[] record = new byte[recLenMX];
							
							for (int i = 0; i < recLenMX; i++){
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

			socket.close();

		} catch (UnknownHostException exception) {
			System.err.println("ERROR: No Internet Address for '" + args[0] + "'");
		}
	}
}