package com.trifidearth.networkgame;

/* Server */
//Steven Werner
//Networks
//SUMMER 2013

//script before run
//do your interaction
//all will be saved as it is displayed
//exit will exit and output all console i/o into a file.. 

import java.io.*;
import java.net.*;
import java.util.Random;

public class Server 
{

	public static void main(String argv[]) throws Exception 
	{
		String clientSentence;
		ServerSocket welcomeSocket = new ServerSocket(10000);
		
		
		while(true)
		{	
			System.out.println("Listening on " + welcomeSocket.getInetAddress().getCanonicalHostName() + "/" + welcomeSocket.getLocalPort() + "...");
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
			PrintStream outToClient = new PrintStream(connectionSocket.getOutputStream());
			System.out.println("Connected to " + connectionSocket.getInetAddress().getCanonicalHostName() + "/" + connectionSocket.getPort());
			Game game = new Game(0,1023);
			game.guess();
			System.out.print("To:\tHi there! Let's begin:\t('exit' to quit)\nTo:\t" + game.message + "\n");
			outToClient.print("Hi there! Let's begin:\t('exit' to quit)\n" + game.message + "\n");
			
			
			while(connectionSocket.isConnected())
			{
				clientSentence = inFromClient.readLine();
				System.out.print("From:\t" + clientSentence + "\n");
				if(clientSentence!=null && clientSentence.toLowerCase().contains("exit"))
						break;
				if(game.interpret(clientSentence))
					game.guess();
				System.out.print("To:\t" + game.message + "\n");
				outToClient.print(game.message + "\n");
				
			}
			System.out.print("To:\tThank you for playing :) bye.\n");
			outToClient.print("Thank you for playing :) bye.\n");
			System.out.println("Disconnected from " + connectionSocket.getPort());
			connectionSocket.close();
		}
	}
}

class Game
{
	int rangeMin;
	int rangeMax;
	int rangeGuess;
	int turn;
	int [] guesses;
	boolean [] answers;
	public String message;
	Random rand;
	
	String [] grammarCorrect = {"correct","yes","yep", "yeah", "right", "y"};
	String [] grammarHigh = {"higher", "high", "more", "h","bigger", "greater", "too low"};
	String [] grammarLow = {"lower", "low", "less", "l", "smaller", "lesser", "too high"};
	String [] grammarWhat = {"say too high or too low","I didn't get that", "What? Was i right?", "You need to say low or high" , "Say it a different way"};
	String [] grammarGuess = {"Is it ","How about ", "It's probably ", "For Sure it's ", "Umm.. ", "Ok... ", "I bet it's "};
	
	
	Game(int rangeMin, int rangeMax)
	{
		rand = new Random();
		this.rangeMin=rangeMin;
		this.rangeMax=rangeMax;
		turn=0;
		int rangeGuess = (int)(Math.log(this.rangeMax)/Math.log(2))+1;
		guesses = new int[rangeGuess];
		answers = new boolean[rangeGuess];
	}
	
	public boolean interpret(String clientSentence) 
	{
	
		try
		{
			answers[turn-1] = decode(clientSentence);
		}
		catch(NullPointerException e)
		{
			if(turn<0)
				message = "I know I know, " + guesses[guesses.length-1] + "...  This is what i do...\t('exit' and try again..)";
			else
			{
				message = "Yep " + guesses[turn-1] + ". I knew it!! Only " + turn + (turn>1 ? " guesses." : " guess!") + "\t('exit' and try again..)";
				guesses[guesses.length-1]=guesses[turn-1];
				turn = -1;
			}
			return true;
		}
		catch(Exception e)
		{
			if(turn<0)
			{	
				message = "The answer is " + guesses[guesses.length-1] + " or else you lied to me!!  ('exit' and try again..)";
				return true;
			}
			message = grammarWhat[rand.nextInt(grammarWhat.length)];
			return false;
		}
		
		if(answers[turn-1])
			rangeMin=guesses[turn-1];
		else
			rangeMax=guesses[turn-1];
		return true;
		
	}

	private boolean decode(String clientSentence) throws NullPointerException, Exception
	{
		for(String word : grammarHigh)
			if(clientSentence.toLowerCase().contains(word))
				return true;
		for(String word : grammarLow)
			if(clientSentence.toLowerCase().contains(word))
				return false;
		for(String word : grammarCorrect)
			if(clientSentence.toLowerCase().contains(word))
				throw new NullPointerException();
		throw new Exception();
	}

	public void guess()
	{
		if(turn>=guesses.length)
		{
			message = "The answer is " + guesses[guesses.length-1] + " or else you lied to me!!  ('exit' and try again..)";
			return;
		}
		else if(turn <0)
			return;
		
		int unpredictable= 0;
		if(turn==0)
			unpredictable = rand.nextInt(41)-20;  //(0to40)>>(0to40)-20>>(-20to20)
		guesses[turn] = getMid() + unpredictable;
		message = grammarGuess[rand.nextInt(grammarGuess.length)] + guesses[turn] + "?";
		this.turn++;
	}
	
	public int getMid()
	{
		return (rangeMin+rangeMax)/2;
	}
}
