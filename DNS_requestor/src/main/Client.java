package main;

import java.io.IOException;

public class Client {
	public static void main(String[] args) throws IOException{
		
		if (args.length != 3)
        {
            System.err.println("Specify in parameters: domainName or domainAddress");
			UDP_connection.main(args);

		}
	
		else {
			UDP_connection.main(args);
			//TCP_connection.main(args);
		}
	}
}
