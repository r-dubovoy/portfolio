package pkd;

/**
 * 
 * PKD Message
 * 
 * byte [0..3] (4 bytes) integer command id
 * byte [4..17] (4 bytes) the size of data, might be 0
 * byte [16 .... size-1] the actual data
 * 
 * Data
 * 
 * for cmdStoreClientKey
 * 
 * data[0..3] (4 bytes) the IP
 * data[4..7] (4 bytes) the Port
 * data[8...size-1-8] the Key
 * 
 * PKD client-server communication:
 * 
 * setting up communication
 * 0) client->server: cmdGetServerKey
 * 1) server->client: cmdSendServerKey + key
 * 
 * 2) client->server: cmdSendMyCleintKey + key
 * 3) server->client: cmdDone;
 * 
 * retrieving the address book
 * 4) client->server: cmdGetNumClients
 * 5) server->client: cmdSentNumClients + num
 * 
 * [in a loop for index in 0..num-1 at client side]
 * 6) client->server: cmdGetClientIP + index
 * 7) server->client: cmdSendClientIP + ip
 * 
 * 8) client->server: cmdGetClientPort + index
 * 9) server->client: cmdSendClientPort + port
 * 
 * 6) client->server: cmdGetClientKey + index
 * 7) server->client: cmdSendClientKey + key
 * [end of loop]
 *  
 */

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import pkd.Utilities;

public class Message {
	
	//the default response of the server
	public static final int cmdDone = 0;
		
	//contains encoded public key in data
	public static final int cmdSendServerKey = 1;
	
	//contains hash + port + key
	public static final int cmdSendMyClientKey = 2;
	
	//has 16 bytes of md5 hash
	public static final int cmdGetNumClients = 3;
	
	//has 4 byte data for number of clients
	public static final int cmdSendNumClients = 4;
	
	//has 16 bytes of md5 hash and 4 bytes of data for client index
	public static final int cmdGetClientInfo = 5;
	
	//contains database hash + ip + port + key
	public static final int cmdSendClientInfo = 6;
	
	//no data
	public static final int cmdNotifyDatabaseChanged = 7;
	
	//contains hash+text in data
	public static final int cmdSendMessage = 8;
	
	public static final int cmdUpdateKey = 9;
	
	protected String _telegram = null;
	protected byte[] _md5;
	protected byte[] _msg;
	protected byte[] _key;
	protected int    _cmd = -1;
	protected int    _port = -1;
	protected int    _num = -1;
	protected int    _idx = -1;
	protected String _ip;
	
	//parse received bytes 
	public Message(byte[] msg) throws Exception, UnknownHostException {
		
		if(msg == null)
			throw new Exception("null message");
		
		_msg = msg;
		
		//get command
		byte[] cmdBytes = Arrays.copyOfRange(msg, 0, 4);
		_cmd = Utilities.byteArrayToInt(cmdBytes);
		
		//get size
		byte[] sizeBytes = Arrays.copyOfRange(msg, 4, 8);
		int size = Utilities.byteArrayToInt(sizeBytes);
		
		//get the rest
		byte[] restBytes = null;
		if(size > 0)
			restBytes = Arrays.copyOfRange(msg, 8, 8 + size);
		
		switch(_cmd){
			
		case cmdDone:
		case cmdNotifyDatabaseChanged:
			break;
			
		case cmdSendMessage:
			if(restBytes != null){
				_telegram = new String(restBytes,"UTF-8");
			}	
			break;
			
		case cmdGetNumClients:
			if(restBytes != null){
				_md5 = Arrays.copyOfRange(restBytes, 0, 16);
			}
			break;
		/*
		case cmdSendMyClientKey: 
			if(restBytes != null){
				_md5 = Arrays.copyOfRange(restBytes, 0, 16);
				
				byte[] portBytes = Arrays.copyOfRange(restBytes, 16, 16+4);
				_port = Utilities.byteArrayToInt(portBytes);
				
				_key = Arrays.copyOfRange(restBytes, 4+16, restBytes.length);	
			}
			break;
		*/	
		case cmdSendServerKey:
			_key = restBytes;
			break;

		case cmdSendNumClients:
			if(restBytes != null){
				byte[] intBytes = Arrays.copyOfRange(restBytes, 0, 4);
				_num = Utilities.byteArrayToInt(intBytes);
			}
			break;
			
		case cmdGetClientInfo:
			if(restBytes != null)
			{
				_md5 = Arrays.copyOfRange(restBytes, 0, 16);
				
				byte[] intBytes = Arrays.copyOfRange(restBytes, 16, 16+4);
				_idx = Utilities.byteArrayToInt(intBytes);
			}
			break;
	
		case cmdUpdateKey:		
		case cmdSendMyClientKey: 
		case cmdSendClientInfo: 
			if(restBytes != null){
				
				_md5 = Arrays.copyOfRange(restBytes, 0, 16);
				
				byte[] ipBytes = Arrays.copyOfRange(restBytes, 16, 16+4);
				InetAddress addr = InetAddress.getByAddress(ipBytes);
				_ip = addr.getHostAddress();
				
				byte[] portBytes = Arrays.copyOfRange(restBytes, 16+4, 16+4+4);
				_port = Utilities.byteArrayToInt(portBytes);
				
				_key = Arrays.copyOfRange(restBytes, 16+4+4, restBytes.length);	
			}
			break;
		}
	}
	/*
	public Message(int cmd, byte[] md5, int port, byte[] pubKey) throws Exception {
		
		if(md5.length != 16)
			throw new Exception("illegal md5 size " + Integer.toString(md5.length));
		
		_cmd = cmd;
		byte[] cmdBytes = Utilities.intToByteArray(cmd);
		
		//we know md5 is 16 bytes
		int size = 4 + 16 + pubKey.length;
		byte[] sizeBytes = Utilities.intToByteArray(size);
		
		byte[] portBytes = Utilities.intToByteArray(port);
		
		_msg = Utilities.join(cmdBytes, sizeBytes);
		_msg = Utilities.join(_msg, md5);
		_msg = Utilities.join(_msg, portBytes);
		_msg = Utilities.join(_msg, pubKey);
	}
	*/
	public Message(int cmd, byte[] md5, String ip, int port, byte[] pubKey) throws Exception {
		
		if(md5.length != 16)
			throw new Exception("illegal md5 size " + Integer.toString(md5.length));
		
		_cmd = cmd;
		byte[] cmdBytes = Utilities.intToByteArray(cmd);
		
		//we know md5 is 16 bytes
		int size = 4 + 4 + 16 + pubKey.length;
		byte[] sizeBytes = Utilities.intToByteArray(size);
		
		byte[] portBytes = Utilities.intToByteArray(port);
		
		InetAddress addr = InetAddress.getByName(ip);		
		byte[] ipBytes = addr.getAddress();
		
		_msg = Utilities.join(cmdBytes, sizeBytes);
		_msg = Utilities.join(_msg, md5);
		_msg = Utilities.join(_msg, ipBytes);
		_msg = Utilities.join(_msg, portBytes);
		_msg = Utilities.join(_msg, pubKey);
	}
	
	//constructor to pack a command without parameters: 
	//cmdDone, cmdGetServerKey, cmdGetNumClients
	public Message(int cmd)
	{
		_cmd = cmd;
		_msg = Utilities.intToByteArray(cmd);
	}
	
	//cmdSendNumClients
	public Message(int cmd, int param)
	{
		_cmd = cmd;
		byte[] cmdBytes = Utilities.intToByteArray(cmd);
		byte[] sizeBytes = Utilities.intToByteArray(4);
		byte[] paramBytes = Utilities.intToByteArray(param);
	
		_msg = Utilities.join(cmdBytes, sizeBytes);
		_msg = Utilities.join(_msg, paramBytes);
	}
	
	//constructor to pack command with 1 integer parameter and hash for database lookup: 
	//cmdGetNumClients, cmdGetClientInfo
	public Message(int cmd, byte[] md5, int param)
	{
		_cmd = cmd;
		byte[] cmdBytes = Utilities.intToByteArray(cmd);
		byte[] sizeBytes = Utilities.intToByteArray(4+md5.length);
		byte[] paramBytes = Utilities.intToByteArray(param);
	
		_msg = Utilities.join(cmdBytes, sizeBytes);
		_msg = Utilities.join(_msg, md5);
		_msg = Utilities.join(_msg, paramBytes);
	}
	
	//constructor to pack command with 1 byte array (key) parameter: 
	//cmdSendServerKey
	
	
	public Message(int cmd, byte[] param)
	{
		_cmd = cmd;
		byte[] cmdBytes = Utilities.intToByteArray(cmd);
		byte[] sizeBytes = Utilities.intToByteArray(param.length);
		
		_msg = Utilities.join(cmdBytes, sizeBytes);
		_msg = Utilities.join(_msg, param);
	}
	

	public Message(int cmd, String text) throws UnknownHostException
	{
		_cmd = cmd;
		byte[] cmdBytes = Utilities.intToByteArray(cmd);
		byte[] textBytes = null;
		try {
			textBytes = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] sizeBytes = Utilities.intToByteArray(textBytes.length);
		
		_msg = Utilities.join(cmdBytes, sizeBytes);
		_msg = Utilities.join(_msg, textBytes);
	}
	

	public int getSize() {
		
		if(_msg != null)
			return _msg.length;
		
		return 0;
	}
	
	public byte[] getBytes(){
		return _msg;
	}
	
	public int getCommand() {
		return _cmd;
	}
	
	public int getPort() {
		return _port;
	}
	
	public String getIP() {
		return _ip;
	}
	
	public byte[] getKeyEncoded() {
		return _key;
	}
	
	public int getNumClients(){
		return _num;
	}
	
	public int getIndex() {
		return _idx;
	}
	
	public byte[] getMD5(){
		
		return _md5;
	}

	public String getTelegram() {
		
		return _telegram;
	}

}
