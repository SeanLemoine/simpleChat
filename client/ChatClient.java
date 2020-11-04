// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the server.
   */
  ChatIF clientUI;
  
  /**
   * An easy-access boolean indicating whether or not the client is currently
   * connected to a server.
   */
  protected boolean loggedIn;
  
  protected String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.loginID = loginID;
    this.clientUI = clientUI;
    this.loggedIn = true;
    
    try {
	      openConnection();
	      sendToServer("#login " + this.loginID);
    }
    catch(IOException e) {
    	clientUI.display("Cannot open connection. Awaiting command.");
	    this.loggedIn = false;
    }
    
    
  }

  
  //Instance methods ************************************************
  
  @Override
  /**
   * Hook method called after the connection has been closed. The default
   * implementation does nothing. The method may be overridden by subclasses to
   * perform special processing such as cleaning up and terminating, or
   * attempting to reconnect.
   */
  protected void connectionClosed() {
	  clientUI.display("Disconnected from server. Logging out");
	  this.loggedIn = false;
  }

  @Override
  /**
   * Hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server. The method may be
   * overridden by subclasses.
   * 
   * @param exception
   *            the exception raised.
   */
  protected void connectionException(Exception exception) {
	  clientUI.display("WARNING - The server has stopped listening for connections\n" + 
	  		"SERVER SHUTTING DOWN! DISCONNECTING!\n" + 
	  		"Abnormal termination of connection.");
	  System.exit(1);
  }
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	if(message.charAt(0) == '#') {
		//Console commands
		handleCommands(message);
	} else if(loggedIn) {
	    try
	    {
	      sendToServer(message);
	    }
	    catch(IOException e)
	    {
	      clientUI.display
	        ("Could not send message to server.  Terminating client.");
	      quit();
	    }
    } else clientUI.display("Not currently logged in to a server.\n"
    		+ "Set desired host/port with #sethost and #setport and login with #login");
  }
  
  private void handleCommands(String command) {
	  String[] inputs = command.split(" ");
	  switch(inputs[0]) {
		case "#quit": clientUI.display("Quitting...");
			quit();
			break;
		case "#logoff":
			if(loggedIn) {
				try{
					closeConnection();
					this.loggedIn = false;
				} catch(IOException e) {
					clientUI.display
			          ("Error disconnecting from server. Terminating client.");
					quit();
				}
				clientUI.display("Connection closed"
						+ "");
			} else clientUI.display("Not currently logged in");
			break;
		case "#login":
			if(!loggedIn) {
				try{
					openConnection();
					this.loggedIn = true;
					sendToServer("#login " + this.loginID);
				} catch(IOException e) {
					clientUI.display
			          ("Error connecting to server. Terminating client.");
					quit();
				}
				clientUI.display("Logged in successfully");
			} else clientUI.display("Already logged in");
			break;
		case "#gethost": clientUI.display(getHost());
			break;
		case "#getport": clientUI.display("" + getPort());
			break;
		case "#sethost":
			if(!loggedIn) {
				try {
					setHost(inputs[1]);
					clientUI.display("Host set to: " + inputs[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					clientUI.display("Argument necessary\nUsage: #sethost <host>");
				}
	  		} else clientUI.display("Cannot change host while logged in");
			break;
		case "#setport":
			if(!loggedIn) {
				try {
					setPort(Integer.parseInt(inputs[1]));
					clientUI.display("Port set to: " + inputs[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					clientUI.display("Argument necessary\nUsage: #setport <port>");
				} catch (IllegalArgumentException i) {
					clientUI.display("Must set port as a four-digit integer");
				}
			} else clientUI.display("Cannot change port while logged in");
			break;
		default: clientUI.display("Unknown or unsupported command");
		}
  }
  
  /**
   * The getter method for the client's loginID.
   * 
   * @return loginID The current value of the protected string loginID
   */
  public String getLoginID() {
	return loginID;
  }

  /**
   * The setter method for the client's loginID.
   * 
   * @param loginID The replacement value for the protected string loginID
   */
  public void setLoginID(String loginID) {
	this.loginID = loginID;
  }


  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
    	closeConnection();
    }
    catch(IOException e) {
    	connectionException(e);
    }
    System.exit(0);
  }
}
//End of ChatClient class
