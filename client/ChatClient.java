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
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
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
	  clientUI.display("Disconnected from server. Closing application.");
	  System.exit(0);
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
	  clientUI.display("Error: Abruptly lost connection with server. Exiting...");
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
	} else {
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
    }
  }
  
  private void handleCommands(String command) {
	  String[] inputs = command.split(" ");
	  switch(inputs[0]) {
		case "#quit": clientUI.display("Quitting...");
			quit();
			break;
		case "#logoff":
			try{
				closeConnection();
			} catch(IOException e) {
				clientUI.display
		          ("Error disconnecting from server. Terminating client.");
				quit();
			}
			break;
		case "#login":
			try{
				openConnection();
			} catch(IOException e) {
				clientUI.display
		          ("Error connecting to server. Terminating client.");
				quit();
			}
			break;
		case "#gethost": clientUI.display(getHost());
			break;
		case "#getport": clientUI.display("" + getPort());
			break;
		case "#sethost":
			//TODO: Only allow host setting if client is logged out
			try {
				setHost(inputs[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				clientUI.display("Argument necessary\nUsage: #sethost <host>");
			}
			break;
		case "#setport":
			//TODO: Only allow port setting if client is logged out
			try {
				setPort(Integer.parseInt(inputs[1]));
			} catch (ArrayIndexOutOfBoundsException e) {
				clientUI.display("Argument necessary\nUsage: #setport <port>");
			} catch (IllegalArgumentException i) {
				clientUI.display("Must set port as a four-digit integer");
			}
			break;
		default: clientUI.display("Unknown or unsupported command");
		}
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
