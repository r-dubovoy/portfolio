package network;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;


public abstract class UdpServer implements Runnable, UdpCommand {
	
	 protected int _port = -1; 
	 protected int _size = 1024;
	 
	 public UdpServer(int port)  { 
		 _port = port; 
	 } 
	 
	public void run() {
													
		try {
			DatagramSocket serverSocket = new DatagramSocket(_port);
		
		    byte[] receiveData = new byte[_size];
		   
		    while(true)
		    {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		
				try {
					serverSocket.receive(receivePacket);	
					
					DatagramPacket sendPacket = process(receivePacket);
					if(sendPacket != null)
					{
						serverSocket.send(sendPacket);
					}
					if(maybeStop())
						break;
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    serverSocket.close();
		    
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}