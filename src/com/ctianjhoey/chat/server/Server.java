package com.ctianjhoey.chat.server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Server extends JFrame {

	private JTextField userText; //where user can chat
	private JTextArea chatWindow; //where chat messages are displayed
	//streams is how your computer communicate with other computer
	//OUTPUTSTREAM and INPUTSTREAM are two types of streams
	private ObjectOutputStream output; 
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection; //socket is basically a connection
	
	public Server() {
		 
		super("Jhoey's Instant Messenger");
		
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
		
	}
	
	protected void sendMessage(String message) {
		try {
			output.writeObject("SERVER - "+message);
			output.flush();
			showMessage("\nSERVER - "+message);
		} catch (IOException e) {
			chatWindow.append("\n Error: Dude! I can't send that message.");
		}
	}

	public void startRunning() {
		try {
			server = new ServerSocket(6789, 100);
			
			while(true) {
				try {
					
					waitForConnection();
					setupStreams();
					whileChatting();
					
				} catch (EOFException eofException) {
					showMessage("\nServer ended the connection!");
				} finally {
					closeCrap();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//housekeeping stuff
	private void closeCrap() {
		showMessage("\nClosing connections....\n");
		ableToType(false);
		try {
			
			output.close();
			input.hashCode();
			connection.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void ableToType(final boolean b) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(b);
			}
		});
	}

	private void showMessage(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(text);
			}
		});
	}

	private void whileChatting() throws IOException {
		String message = " You are now connected! ";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("The user has sent an unknown object!");
			}
		}while(!message.equals("CLIENT - END"));
	}

	private void setupStreams() {
		try {
			
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			showMessage("\nStreams are now setup!");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//wait for connection, then display connection information
	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect... \n");
		connection = server.accept();
		showMessage("Now connected to "+connection.getInetAddress().getHostName());
	}
	
	
}
