package com.trifidearth.networkgame;

/* Client */
//Steven Werner
//Networks
//SUMMER 2013

import java.io.*;
import java.net.*;

class Client 
{

	public static void main(String argv[]) 
	{
		String host = "localhost";
		int port = 10000;
		
		try
		{
			if (argv.length > 0)
				if(argv[0].toLowerCase().equals("-h") || argv[0].toLowerCase().equals("--help"))
				{
					System.out.println("Call with: 'java Client server_ip_address/name port_number");
					System.exit(0);
				}
				else
				{
					host = argv[0];
					if(argv.length > 1)
						port = Integer.parseInt(argv[1]);
				}
			String sentence = "";
			String recieve;
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			Socket clientSocket = new Socket(host, port); 
			System.out.println("Connected to " + clientSocket.getInetAddress().getCanonicalHostName().toString() + "/" + clientSocket.getPort());
			PrintStream outToServer = new PrintStream(clientSocket.getOutputStream()); 
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
			recieve = inFromServer.readLine(); 
			System.out.println("From:\t" + recieve);
			
			while (clientSocket.isConnected() && !(sentence.contains("exit")))
			{
				recieve = inFromServer.readLine(); 
				System.out.println("From:\t" + recieve);
				
				System.out.print("To:\t");
				sentence = inFromUser.readLine(); 
				outToServer.print(sentence + '\n'); 	
			}
			recieve = inFromServer.readLine(); 
			System.out.println("From:\t" + recieve);
			
			System.out.println("Done..");
			clientSocket.close();
			
		}
		catch (Exception e)
		{
			System.out.println("Exception Error: " + e.getClass().getCanonicalName());
		}
	} 
}
