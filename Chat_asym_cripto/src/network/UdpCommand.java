package network;

import java.net.DatagramPacket;

public interface UdpCommand {

	//might return null
	DatagramPacket process(DatagramPacket packet) throws Exception;
	boolean maybeStop();
}
