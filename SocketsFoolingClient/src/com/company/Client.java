package com.company;

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;

public class Client extends JFrame{
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    public Client(String host){
        super("Client DAYO!");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(event -> {
            sendMessage(event.getActionCommand());
            userText.setText("");
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
    }

    //connect to server
    public void start(){
        try {
            connectToServer();
            setupStreams();
            whileChatting();
        } catch (EOFException eofException){
            showMessage("\n Client terminated connection");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            close();
        }
    }

    //connect to server
    private void connectToServer() throws IOException{
        showMessage("Attempting connection...\n");
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Connected to: " + connection.getInetAddress().getHostName());
    }

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are set up!");
    }

    private void whileChatting() throws IOException{
        ableToType(true);
        do {
            try {
                message = (String)input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException classNotFoundException){
                showMessage("\n Invalid Object sent!");
            }
        } while(!message.equals("SERVER - END"));
    }

    private void close(){
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
            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("\nCLIENT - " + message);
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