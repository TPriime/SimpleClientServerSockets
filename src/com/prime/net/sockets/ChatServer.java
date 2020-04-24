/*
   Created by prime on ‎April ‎15, ‎2018
*/


package com.prime.net.sockets;

import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.concurrent.*;
import java.awt.event.*;


public class ChatServer extends JFrame {
    private ServerSocket server;
    private Socket to_client;
    private ChatClient[] clients;
    private JTextArea field = new JTextArea("");
    private final int NO_CLIENTS;
    private JTextField chatBox;
    private ExecutorService executor;
    private JButton endButton, startButton;
    private JPanel buttons;
    
    ChatServer(int numberOfClients)throws NullPointerException{
        super("    Server");
        
        setSize(400, 400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        new Beautify(this, 1);
        
        field.setFont(new Font("serif", Font.ITALIC, 16));
        field.setEditable(false);
        field.setLineWrap(true);
        
        chatBox = new JTextField();
        chatBox.setEditable(false);
        chatBox.addActionListener(
            new ActionListener(){
               public void actionPerformed(ActionEvent event){
                   field.setFont(new Font("serif", Font.ITALIC, 16));
               
                   chatBox.setText("");
                   String s = event.getActionCommand();
                   field.append("\nMe: " + s);
                   try{
                       for(int x=0; x<NO_CLIENTS; x++){
                          if(clients[x] != null){
                              clients[x].output.writeObject("\nServer: "+s);
                              clients[x].output.flush();
                          }
                          
                       }
                   }catch(IOException io){}
               }   
            }
        );
        
        add(chatBox, BorderLayout.SOUTH);
        startButton = new JButton("Start Server");
        startButton.setEnabled(false);
        startButton.addActionListener(
            new ActionListener(){
                public void actionPerformed(ActionEvent event){
                    try{ createConnections(); } catch(IOException io){}
                }
            }
        );
                
        endButton = new JButton("Disconnect");
        endButton.setEnabled(false);
        endButton.addActionListener(
            new ActionListener(){
                public void actionPerformed(ActionEvent event){
                    try{ 
                       for(int x=0; x<NO_CLIENTS; x++)
                           terminate(); 
                    }  catch(IOException io){}
                }
            }
        );
        
        buttons = new JPanel(new GridLayout());
        buttons.add(startButton);
        buttons.add(endButton);
        
        add(new JScrollPane(field));  //create text area
        add(buttons, BorderLayout.NORTH);
        
        
        NO_CLIENTS = numberOfClients;
       
        clients = new ChatClient[NO_CLIENTS];
        
        try{
            server = new ServerSocket(60601, NO_CLIENTS);
            
        }catch(IOException e){ System.err.println(e); }
        
        executor = Executors.newCachedThreadPool();
        
        setLocation(HEIGHT, 30);
        setResizable(false);
    }
    
    void createConnections() throws IOException, NullPointerException{
        field.append("Starting Server...\nCreating connections...\nConnection of up to "+NO_CLIENTS+" created...\n");
        
        endButton.setEnabled(true);
        
        for(int x=0; x<NO_CLIENTS; x++){
            int others;
            if(x<NO_CLIENTS-1){others = (x+1);}
            else{others= clients.length-1 ;}
            
            clients[x] = new ChatClient( server.accept(), x);
            executor.execute(clients[x]);
            
            field.append("\nClient_"+(x+1)+" connected."); 
    
            chatBox.setEditable(true);
        }
    }
    
    void terminate() throws IOException{
        field.setFont(new Font("serif", Font.ITALIC, 16));
        field.append("\n\nClosing connections...");
        for(int x=0; x<NO_CLIENTS; x++)
        {    clients[x].output.writeObject("\ndisconnected from server.");
             clients[x].input.close();
             clients[x].output.close();}
      
        field.append("\nClosed.");     
        endButton.setEnabled(true);
    }
    
    private class ChatClient implements Runnable{
        private Socket connection;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private String message;
        private int y;
        
        public ChatClient( Socket connection, int x){
           y = x;
           
           this.connection = connection;
        }
        
        void connect() throws IOException{
           output = new ObjectOutputStream(connection.getOutputStream());
           input = new ObjectInputStream(connection.getInputStream());
        }
        
        public void run(){
            try{
                connect();
            
                do{
                    message = (String)input.readObject();
                    field.setFont(new Font("serif", Font.ITALIC, 16));
                    field.append("\nClient_"+(String.valueOf(y+1)+": "+message));
                    field.setFont(new Font("serif", Font.ITALIC, 16));
                    
                    for(int x=0; x<NO_CLIENTS; x++){
                       if(x==y){continue;}
                       else if(clients[x] != null)
                       {clients[x].output.writeObject("\nClient_"+(String.valueOf(y+1)+": "+message));
                       clients[x].output.flush(); }
                    }    
                }while( !message.equals("exit"));
            }catch(ClassNotFoundException ClassEx){}
             catch(IOException e){System.err.println(e);}
             { System.err.println("classex");}
        }
        
    }//end class ChatClient
    
    public static void main(String[] args){
        ChatServer app;
        try{
            if(args.length==0){
                app = new ChatServer(2);
            } else
                app = new ChatServer(Integer.parseInt(args[0]));  
                app.createConnections();
        }catch(IOException io){}
         catch(NullPointerException nullex) {System.err.println(); }
    }//end main method
   
}// end class ServerChat


