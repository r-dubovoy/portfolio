package network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import pkd.Message;
import pkd.Packet;
import pkd.Encryptor;
import pkd.Keys;
import pkd.Decryptor;

public class PkdClient extends UdpClient {
	
	protected byte[] _user_hash = null;
	protected String _user_ip = null;
	protected int _user_port = 0; 
	protected byte[] _user_key = null; 
	protected int _numUsers = 0;
	protected byte[] _serverKey = null;
	protected Encryptor _encryptor = null;
	protected Decryptor _decryptor = null;

	public PkdClient(String ip, int port, Message msg, Encryptor encryptor, Decryptor decryptor, boolean justnotify)
			throws UnknownHostException, IllegalBlockSizeException, BadPaddingException {

		super(ip, port, null,justnotify);

		_encryptor = encryptor;
		_decryptor = decryptor;
		
		Packet packet = new Packet(msg, _ip, _port, _encryptor);
		_packet = packet.getDatagram();
	}
	
	@Override
	public DatagramPacket process(DatagramPacket packet) throws Exception {

		Packet in = null;
		if (Packet.checkIsEncrypted(packet)) {

			in = new Packet(packet, _decryptor);
		} else {

			in = new Packet(packet, null);
		}

		Message in_msg = in.getMessage();
		switch (in_msg.getCommand()) {
		

		case Message.cmdSendServerKey:
			_serverKey = in_msg.getKeyEncoded();
			break;
			
		case Message.cmdSendNumClients:
			_numUsers = in_msg.getNumClients();
			break;
			
		case Message.cmdSendClientInfo:
			_user_hash = in_msg.getMD5();
			_user_ip = in_msg.getIP();
			_user_port = in_msg.getPort();
			_user_key = in_msg.getKeyEncoded();
			break;	
		}
		return null;
	}
	
	public Key getServerKey() throws NoSuchAlgorithmException, InvalidKeySpecException{
		
		return Keys.decodePublicKey(_serverKey);
	}
	
	public int getNumUsers(){
		
		return _numUsers;
	}

	@Override
	public boolean maybeStop() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public byte[] getUserHash(){
		return _user_hash;
	}

	public String getUserIp() {		
		return _user_ip;
	}

	public int getUserPort() {
		return _user_port;
	}

	public byte[] getUserKey() {
		return _user_key;
	}

}
