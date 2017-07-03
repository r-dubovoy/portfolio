package network;

import java.io.IOException;
import java.net.DatagramPacket; 
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public abstract class UdpClient implements Runnable, UdpCommand {
	
	 protected String _ip = "127.0.0.1";
	 protected int _port = -1; 
	 protected int _size = 1024;
	 protected byte[] _data = null;
	 protected DatagramPacket _packet = null;
	 protected boolean _justnotify = false;
	 
	 public UdpClient(String ip, int port, byte[] data, boolean justnotify)  { 
		 _ip = ip;
		 _port = port; 
		 _data = data;
		 _packet = null;
		 _justnotify = justnotify;
	 } 
	 
	 public void run() {
		
		try {
			DatagramSocket clientSocket = new DatagramSocket();;
			if(_packet != null){
				//send existing packet
				clientSocket.send(_packet);
			}else{
				//make packet from data
				InetAddress IPAddress = InetAddress.getByName(_ip);
				DatagramPacket sendPacket = new DatagramPacket(_data, _data.length, IPAddress, _port);
				
				clientSocket.send(sendPacket);
			}
			if(!_justnotify)
			{
				byte[] receiveData = new byte[_size];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
			
				try {
					process(receivePacket);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clientSocket.close();
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}