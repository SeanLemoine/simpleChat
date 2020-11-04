import java.util.Scanner;

import common.ChatIF;

public class ServerConsole implements ChatIF {
	
	/**
	* Scanner to read from the console
	*/
	Scanner fromConsole; 

	@Override
	public void display(String message) {
		System.out.println("> " + message);
	}

}
