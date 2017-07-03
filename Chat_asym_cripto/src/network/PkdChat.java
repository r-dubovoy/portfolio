package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pkd.Keys;
import pkd.Database;
import pkd.Encryptor;
import pkd.Decryptor;
import pkd.Message;
import pkd.Packet;
import pkd.Utilities;
import network.UdpServer;
import gui.WritableGUI;

public class PkdChat extends UdpServer {
	
	protected WritableGUI _gui = null;
	protected String _pkdServerIP = null;
	protected int    _pkdServerPort = -1;
	protected Key    _pkdServerPubKey = null;
	protected Keys 	 _keys = null;
	protected Decryptor _decryptor = null;
	//contacts pulled from the server
	protected Database _database = null;
	//database to simulate repetition attack
	protected Database _hacked = null;
	 
	 
	 public PkdChat(WritableGUI gui, int port, String pkdServerIP, int pkdServerPort) throws 
	 	IllegalBlockSizeException, BadPaddingException, Exception { 
		 
		super(port);
		 
		_keys = new Keys();
		_decryptor = new Decryptor(_keys.getPrivate());
		_database = new Database();
		 
		_gui = gui;
		_pkdServerIP = pkdServerIP;
		_pkdServerPort = pkdServerPort;
		 
		exchangeKeys();
		receiveContacts();
	 }

	@Override
	public DatagramPacket process(DatagramPacket udp) throws Exception {
		
		InetAddress addr = udp.getAddress();
		String ip = addr.getHostAddress();
		
		byte[] hash = null;
		
		Packet in = null;
		if(Packet.checkIsEncrypted(udp)){
			//packet is encrypted
			in = new Packet(udp, _decryptor);
		} else {
			//not encrypted
			in = new Packet(udp, null);
		}
		
		boolean encrypt = true;
		Message out_msg = null;
		Message in_msg = in.getMessage();
		
		switch(in_msg.getCommand()){
		
		case Message.cmdNotifyDatabaseChanged:
			
			try {
				Thread.sleep(100);
				receiveContacts();
				_gui.write("< databse updated >");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		case Message.cmdSendMessage:
			_gui.write(in_msg.getTelegram());
			break;

		}
		return null;
	}

	@Override
	public boolean maybeStop() {
		// TODO Auto-generated method stub
		return false;
	} 
	
	/**
	 Exchange public keys with pkd server
	 * @throws Exception 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public void exchangeKeys() throws IllegalBlockSizeException, BadPaddingException, Exception {
		
		byte[] md5 = getHash();
		String ip = InetAddress.getLocalHost().getHostAddress(); 
		
		PkdClient pkdClient = new PkdClient(_pkdServerIP, _pkdServerPort, 
				new Message(Message.cmdSendMyClientKey, md5, ip, _port, _keys.getPublicEncoded()), 
				null, null,false);
		pkdClient.run();
		_pkdServerPubKey = pkdClient.getServerKey();
	}
	
	public void receiveContacts() throws 
		UnknownHostException, NoSuchAlgorithmException, InvalidKeyException, 
		IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		
		_gui.clearList();
	
		byte[] md5 = getHash();

		PkdClient pkdClient = new PkdClient(_pkdServerIP, _pkdServerPort, 
				new Message(Message.cmdGetNumClients, md5), 
				new Encryptor(_pkdServerPubKey), new Decryptor(_keys.getPrivate()), false);
		pkdClient.run();
		int numUsers = pkdClient.getNumUsers();
		
		//grab the old database for reptetion attack simulation
		_hacked = _database;
		_database = new Database();
		
		for(int i = 0 ; i < numUsers; i++){
			pkdClient = new PkdClient(_pkdServerIP, _pkdServerPort, 
					new Message(Message.cmdGetClientInfo, md5, i),
					new Encryptor(_pkdServerPubKey), new Decryptor(_keys.getPrivate()),false);
			pkdClient.run();
			
			byte[] user_md5 = pkdClient.getUserHash();
			String user_ip = pkdClient.getUserIp();
			int user_port = pkdClient.getUserPort();
			byte [] user_key = pkdClient.getUserKey();
			
			if(!Arrays.equals(md5, user_md5))
			{
				_database.add(user_md5, user_ip, user_port, user_key);
				_gui.addToList(user_ip, user_port);
			}
		}
	}
	
	public void sendMessage(int index, String msg) throws 
		UnknownHostException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, 
		BadPaddingException, NoSuchPaddingException, InvalidKeySpecException {
		
		
		int user_port = _database.getPort(index);
		String user_ip = _database.getIP(index);
		byte[] user_key = _database.getKey(index);
		
		//we do not want to add self to the list
		if(user_port != -1 && user_ip != null && user_key != null) {
			
			PkdClient pkdClient = new PkdClient(user_ip, user_port, 
					new Message(Message.cmdSendMessage, msg), 
					new Encryptor(user_key), new Decryptor(_keys.getPrivate()),true);
			pkdClient.run();
		}
	}
	
	protected byte[] getHash() throws UnknownHostException, NoSuchAlgorithmException {
		
		String ip = InetAddress.getLocalHost().getHostAddress(); 
		return Utilities.host2md5(ip, _port);
	}

	public void resetKeys() throws IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, Exception {
		
		_keys = new Keys();
		_decryptor = new Decryptor(_keys.getPrivate());
		
		
		byte[] md5 = getHash();
		String ip = InetAddress.getLocalHost().getHostAddress(); 
		
		PkdClient pkdClient = new PkdClient(_pkdServerIP, _pkdServerPort, 
				new Message(Message.cmdUpdateKey, md5, ip, _port, _keys.getPublicEncoded()), 
				null, null,false);
		pkdClient.run();
		
		_gui.write("<      keys reset    >");
	}
	
	public void attack(int index) throws UnknownHostException, IllegalBlockSizeException, BadPaddingException, Exception {
		
		if(_hacked == null) {
			_gui.write("<   no hacked database   >");
			_gui.write("<    nobody Reset yet    >");
			return;
		}
		
		if(index == -1) {
			_gui.write("<   select contact in the list   >");
			return;
		}
		
		byte[] hack_md5 = _database.getHash(index);
		if(hack_md5 == null) {
			_gui.write("<   selected client not found in database  >");
			return;
		}
		
		String hack_ip = _database.getIP(index);
		int hack_port = _database.getPort(index);
		
		byte[] valid_key = _database.getKey(index);
		byte[] hack_key = _hacked.getKey(hack_md5);
		if(hack_key == null) {
			_gui.write("<   key not found in hacked database  >");
			return;
		}
		if(Arrays.equals(valid_key, hack_key)) {
			_gui.write("<   keys are same, select another victim  >");
			return;
		}
		_gui.write("<  hacking key of " + hack_ip + ":" + Integer.toString(hack_port) + "  >");
		PkdClient pkdClient = new PkdClient(_pkdServerIP, _pkdServerPort, 
				new Message(Message.cmdUpdateKey, hack_md5, hack_ip, hack_port, hack_key), 
				null, null,true);
		pkdClient.run();	
	}	
}