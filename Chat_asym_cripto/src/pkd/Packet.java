package pkd;

/**
 * PKD protocol
 * 
 * Packet 
 * 
 * byte [0]: 1 encrypted, 0 open
 * byte [1..4] the size of the Message, might be open or encrypted
 * byte [5 ... size-1] encrypted or open Message
 * 
*/

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import pkd.Utilities;

public class Packet { 
	
	protected int _size = 1024;
	protected int _port;
	protected byte[] _data = null;
	protected Message _message = null;
	protected DatagramPacket _udp;
		
	static public boolean checkIsEncrypted(DatagramPacket udp) 
	{
		return udp.getData()[0] == 1;
	}
	
	
	//extracts the command and data from encrypted packet
	public Packet(DatagramPacket udp, Decryptor decryptor) throws Exception
	{
		_data = udp.getData();
		
		//is encrypted
		boolean isencrypted = (_data[0] == 1);
		
		//get size
		byte[] sizeBytes = Arrays.copyOfRange(_data, 1, 5);
		int size = Utilities.byteArrayToInt(sizeBytes);
		
		byte[] data = null;
		if(size > 0)
		{
			data =  Arrays.copyOfRange(_data, 5, 5+size);
		}
		
		if(isencrypted) {
			//encrypted
			
			if(decryptor == null)
				throw new Exception("Decryptor is needed");
			
			byte[] decrypted = decryptor.decrypt(data);
			_message = new Message(decrypted);
			
		} else {
			//open
			_message = new Message(data);
		}
	}
	
	//packs the message
	//if encryrptor is null message goes unencrypted
	public Packet(Message msg, String ip, int port, Encryptor encryptor) throws UnknownHostException, IllegalBlockSizeException, BadPaddingException
	{
		InetAddress addr = InetAddress.getByName(ip);
		
		//encryption flag
		byte[] flagBytes = new byte[1];
		
		
		if(encryptor != null) {
			
			flagBytes[0] = 1;
			
			byte[] encrypted = encryptor.encrypt(msg.getBytes());
			
			byte[] sizeBytes = Utilities.intToByteArray(encrypted.length);
			
			byte[] dataBytes = Utilities.join(sizeBytes, encrypted);
			
			_data = Utilities.join(flagBytes, dataBytes);
			_udp = new DatagramPacket(_data, _data.length, addr, port);
			
		} else {
			
			flagBytes[0] = 0;
			
			byte[] sizeBytes = Utilities.intToByteArray(msg.getSize());
			
			byte[] dataBytes = Utilities.join(sizeBytes, msg.getBytes());
			
			_data = Utilities.join(flagBytes, dataBytes);
			_udp = new DatagramPacket(_data, _data.length, addr, port);
		}
		
	}
	
	
	public Message getMessage() {
		return _message;
	}
	
	public int getExactPort(){
		
		return _port;
	}
	
	public DatagramPacket getDatagram() {
		return _udp;
	}
	
}