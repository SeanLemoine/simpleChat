import java.io.IOException;
import java.util.Scanner;

import client.ChatClient;
import common.ChatIF;

public class ServerConsole implements ChatIF {
	
	/**
	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;
	
	/**
	* Scanner to read from the console
	*/
	Scanner fromConsole;
	
	/*
	 * Server object
	 */
	EchoServer server;
	
	public ServerConsole(int port) {
		
		try {
	      server = new EchoServer(port, this);
	      server.listen(); //Start listening for connections
	      
	    } catch(IOException exception) {
	      System.out.println("Error: Can't setup connection!"
	                + " Terminating.");
	      System.exit(1);
	    } catch (Exception ex) {
	      System.out.println("ERROR - Could not listen for clients!");
	      ex.printStackTrace();
	    }
		
	    // Create scanner object to read from console
	    fromConsole = new Scanner(System.in); 
	}
	
	//Instance methods ************************************************
	  
	  /**
	   * This method waits for input from the console.  Once it is 
	   * received, it sends it to the client's message handler.
	   */
	  public void accept() 
	  {
	    try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }
	
	@Override
	public void display(String message) {
		System.out.println("> " + message);
	}
	
	//Class methods ***************************************************
	  
	  /**
	   * This method is responsible for the creation of the Server UI.
	   *
	   * @param args[0] The host to connect to.
	   */
	  public static void main(String[] args) 
	  {
		  
	    int port = 0; //Port to listen on

	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole chat= new ServerConsole(port);
	    chat.accept();  //Wait for console data*/
	    
	    
	    
	  }

}
