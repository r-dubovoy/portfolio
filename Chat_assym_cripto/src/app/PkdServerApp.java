package app;

import network.PkdServer;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

/**
 * 
 * 	Chat application
 * 
 */

public class PkdServerApp {
	
	 public static void main(String[] args){
		 
		 if(args == null || args.length < 1){
			System.out.println("PkdServerApp: you need specify the port");
			System.exit(-1);
		}
		try {
			int port = Integer.parseInt(args[0]);
			System.out.println("port = " + port);
			 
			new PkdServer(port).run();
			
		} catch (NumberFormatException e) {
			java.util.logging.Logger.getLogger(PkdServerApp.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
		} catch (NoSuchAlgorithmException e) {		
			java.util.logging.Logger.getLogger(PkdServerApp.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
		} catch (InvalidKeyException e) {
			java.util.logging.Logger.getLogger(PkdServerApp.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
		} catch (NoSuchPaddingException e) {
			java.util.logging.Logger.getLogger(PkdServerApp.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
		}
		
	 }
}
