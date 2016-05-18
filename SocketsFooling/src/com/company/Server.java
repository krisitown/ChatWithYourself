package com.company;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
    private JTextField userText;
    private JTextArea chatWindow;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    private ServerSocket server;

    private Socket connection;

    public Server(){
        super("Instant Messages DAYO");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(event -> {
            sendMessage(event.getActionCommand());
            userText.setText("");
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300, 150);
        setVisible(true);
    }

    //set up and run the server
    public void start(){
        try{
            server = new ServerSocket(6789, 100);
            while(true){
                try{
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }
                catch (EOFException eofException){
                    showMessage("\n Server ended the connection!");
                }
                finally {
                    closeCrap();
                }
            }
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    //wait for connection, then display connection information
    private void waitForConnection() throws IOException{
        showMessage(" Waiting for someone to connect...\n");
        connection = server.accept();
        showMessage(" Now connected to " + connection.getInetAddress()
            .getHostAddress());
    }

    //get stream to send and receive data
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are now setup! \n");
    }

    //during the chat conversation
    private void whileChatting() throws IOException{
        String message = " You are new connected! ";
        sendMessage(message);
        ableToType(true);
        do {
            try{
                message = (String)input.readObject();
                showMessage("\n" + message);
            }
            catch (ClassNotFoundException classNotFoundException){
                showMessage("\n idk wtf that user sent!");
            }
        }
        while (!message.equals("CLIENT - END"));
    }

    //closes the streams and sockets
    private void closeCrap(){
        showMessage("\n Closing connections...\n");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }
        catch (IOException ioException){

        }
    }

    //send a message to client
    private void sendMessage(String message){
        try{
            output.writeObject("SERVER - " + message);
            output.flush();
            showMessage("\nSERVER - " + message);
        } catch (IOException ioException){
            chatWindow.append("Error: Message could not be sent!");
        }
    }

    //updates chatWindow
    private void showMessage(final String text){
        SwingUtilities.invokeLater(() -> chatWindow.append(text));
    }

    //give permission to the user to type into their chatbox
    private void ableToType(final boolean abilityToType){
        SwingUtilities.invokeLater(() -> userText.setEditable(abilityToType));
    }
}
