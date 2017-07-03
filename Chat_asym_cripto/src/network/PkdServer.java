package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.NoSuchPaddingException;

import pkd.Keys;
import pkd.Database;
import pkd.Encryptor;
import pkd.History;
import pkd.Decryptor;
import pkd.Message;
import pkd.Packet;
import pkd.Utilities;
import network.UdpServer;

public class PkdServer extends UdpServer {
	 
	 protected Keys _keys = null;
	 protected Decryptor _decryptor = null;
	 protected Database _database = null;
	 protected History _history = new History();
	 
	  
	 public PkdServer(int port) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException { 
		 
		 super(port);
		 
		 _keys = new Keys();
		 _decryptor = new Decryptor(_keys.getPrivate());
		 _database = new Database();
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

		case Message.cmdUpdateKey:
			encrypt = false;
			hash = in_msg.getMD5();
			if(_history.check(in_msg.getKeyEncoded())){
				
				_database.update(hash, in_msg.getIP(), in_msg.getPort(), in_msg.getKeyEncoded());	
				out_msg = new Message(Message.cmdSendServerKey, _keys.getPublicEncoded());
				_history.add(in_msg.getKeyEncoded());
				
				System.out.println("Key for " + in_msg.getIP() + ":" + Integer.toString(in_msg.getPort()) + " updated");
			
				for(int i = 0; i < _database.size(); i++) {
					
					byte[] user_hash = _database.getHash(i);
					if(!Arrays.equals(hash, user_hash)){
					
						String user_ip = _database.getIP(i);
						int user_port = _database.getPort(i);
						byte[] user_key = _database.getKey(i);
						
						new Thread( new PkdClient(user_ip, user_port, 
								new Message(Message.cmdNotifyDatabaseChanged), 
								new Encryptor(user_key), new Decryptor(_keys.getPrivate()),true)).start();
						//pkdClient.run();
					}
				}
			} else
				System.out.println("Repetition atack detected !!!!");
			break;

		case Message.cmdSendMyClientKey:
			// client sent it's key, store the key in database
			encrypt = false;
			hash = in_msg.getMD5();
			if(_history.check(in_msg.getKeyEncoded())){
				_database.add(hash, in_msg.getIP(), in_msg.getPort(), in_msg.getKeyEncoded());	
				out_msg = new Message(Message.cmdSendServerKey, _keys.getPublicEncoded());
				_history.add(in_msg.getKeyEncoded());
			}
			//notify others client that database changed
			for(int i = 0; i < _database.size(); i++) {
				
				byte[] user_hash = _database.getHash(i);
				if(!Arrays.equals(hash, user_hash)){
				
					String user_ip = _database.getIP(i);
					int user_port = _database.getPort(i);
					byte[] user_key = _database.getKey(i);
					
					new Thread( new PkdClient(user_ip, user_port, 
							new Message(Message.cmdNotifyDatabaseChanged), 
							new Encryptor(user_key), new Decryptor(_keys.getPrivate()),true)).start();
					//pkdClient.run();
				}
			}
			break;

		case Message.cmdGetNumClients:
			// client asks number of client records in database
			hash = in_msg.getMD5();
			out_msg = new Message(Message.cmdSendNumClients, _database.size());
			break;
			
		case Message.cmdGetClientInfo: {
			hash = in_msg.getMD5();
			int index = in_msg.getIndex();
			
			byte[] md5 = _database.getHash(index);
			String ip2 = _database.getIP(index);
			int port = _database.getPort(index);
			byte[] key = _database.getKey(index);
			out_msg = new Message(Message.cmdSendClientInfo, md5, ip2, port, key);
			}
			break;

		}
		if (out_msg != null) {
			if (encrypt) {
				
				if(hash == null)
					throw new Exception("Cant find publick key, null hash");
				
				byte[] pubKeyEncoded = _database.getKey(hash);
				if(pubKeyEncoded == null)
					throw new Exception("Public key not found for "+ Utilities.bytes2hex(hash));
				
				Packet out = new Packet(out_msg,ip,udp.getPort(),new Encryptor(pubKeyEncoded));
				return out.getDatagram();
			} else {
				Packet out = new Packet(out_msg,ip,udp.getPort(),null);
				return out.getDatagram();
			}
		}
		return null;
	}

	@Override
	public boolean maybeStop() {
		// TODO Auto-generated method stub
		return false;
	} 
}