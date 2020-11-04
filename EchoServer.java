// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  /*
   * Indicates whether or not the server is open
   */
  protected boolean isOpen;
  
  /*
   * Indicates whether or not the server is stopped
   */
  protected boolean isStopped;
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the server.
   */
  ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) {
	  super(port);
	  this.serverUI = serverUI;
	  this.isOpen = false;
	  this.isStopped = false;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
	  serverUI.display("Message received: " + msg + " from " + client.getInfo("logID"));
	  
	  if(msg.toString().startsWith("#login")) {
		  if(client.getInfo("logID") == null) {
			  //substring is used here to eliminate the first 7 characters ("#login ")
			  client.setInfo("logID", msg.toString().substring(7));
			  serverUI.display(client.getInfo("logID") + " has logged on");
			  this.sendToAllClients(client.getInfo("logID") + " has logged on");
		  } else {
			  try{
				  client.sendToClient("Error: login prompt has already been received. "
			  		+ "Disconnecting.");
				  client.close();
			  } catch(IOException e) {
				  serverUI.display("Unable to notify client of error");
			  }
		  }
	  } else {
		  this.sendToAllClients(client.getInfo("logID") + ": " + msg);
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted() {
	  this.isOpen = true;
	  this.isStopped = false;
	  serverUI.display("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped() {
	  this.isStopped = true;
	  serverUI.display("Server has stopped listening for connections.");
  }
  
  @Override
  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
	  //The connection has already been accepted so this doesn't actually make sense.
	  //This is largely just to match the test case.
	  serverUI.display("A new client is attempting to connect to the server: " + client);
  }

  @Override
  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {
	  serverUI.display("Client disconnected: " + client + ". We'll miss you!");
  }
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message)
  {
	if(message.charAt(0) == '#') {
		//Console commands
		handleCommands(message);
	} else if(isOpen) {
	    this.sendToAllClients("SERVER MSG> " + message);
    } else serverUI.display("Server not currently online");
  }
  
  private void handleCommands(String command) {
	  String[] inputs = command.split(" ");
	  switch(inputs[0]) {
		case "#quit": serverUI.display("Quitting...");
			System.exit(0);
			break;
		case "#stop":
			if(isOpen) {
				stopListening();
				isStopped = true;
			}
			break;
		case "#close":
			if(isOpen) {
				stopListening();
				isStopped = true;
				isOpen = false;
				try{
					close();
				} catch(IOException e) {
					serverUI.display("Error closing: " + e.getMessage());
				}
			}
			break;
		case "#start":
			if(!(isOpen || isStopped)) {
				try{
					listen();
				} catch(IOException e) {
					serverUI.display("ERROR - Could not listen for clients!");
				}
			}
		case "#getport": serverUI.display("" + getPort());
			break;
		case "#setport":
			if(!isOpen) {
				try {
					setPort(Integer.parseInt(inputs[1]));
					serverUI.display("Port set to: " + inputs[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					serverUI.display("Argument necessary\nUsage: #setport <port>");
				} catch (IllegalArgumentException i) {
					serverUI.display("Must set port as a four-digit integer");
				}
			} else serverUI.display("Cannot change port while server is active");
			break;
		default: serverUI.display("Unknown or unsupported command");
		}
  }
  
}
//End of EchoServer class
