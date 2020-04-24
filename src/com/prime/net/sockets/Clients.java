/*
   Created by prime on ‎April ‎15, ‎2018
*/


package com.prime.net.sockets;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;


class Clients extends JFrame{
    private Socket connection;
    private JTextField chatBox;
    private JTextArea field;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    
    Clients(){
        super("Client");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(400,400);
        setLocation(600, 30);
       
        new Beautify(this, 1);
        
        
        field = new JTextArea();
        field.setEditable(false);
        field.setLineWrap(true);
        field.setFont(new Font("serif", Font.ITALIC, 16));
        
        chatBox = new JTextField();
        
        chatBox.addActionListener(
            new ActionListener(){
                public void actionPerformed(ActionEvent event){
                    try{
                        field.setFont(new Font("serif", Font.ITALIC, 16));
                        
                        chatBox.setText("");
                        output.writeObject(event.getActionCommand());
                        output.flush();
                        field.append("\nMe :  "+event.getActionCommand());
                    }catch(IOException io){}
                }    
            }
        );
        
        add(chatBox, BorderLayout.SOUTH);
        add(new JScrollPane(field));
        
        setResizable(true);
                
        try{
            field.append("Connecting to Server...");
            connection = new Socket("127.0.0.1", 60601);
            startChat();
            endChat();
        }catch(UnknownHostException e){System.out.println(e);}
         catch(IOException io){System.out.println(io);}
         catch(ClassNotFoundException classEx){}
         
         
    }
    
    void startChat() throws IOException, ClassNotFoundException{
        output = new ObjectOutputStream(connection.getOutputStream());
        input = new ObjectInputStream(connection.getInputStream());
        field.append("\nConnected.\n");
        String message; 
        
        do{
            message = (String)input.readObject();
            field.setFont(new Font("serif", Font.ITALIC, 16));
            field.append(message);
            field.setFont(new Font("serif", Font.ITALIC, 16));
        }while( !message.equals("Server: exit"));
    }
    
    void endChat() throws IOException{
        field.setFont(new Font("serif", Font.ITALIC, 16));
        field.append("\n\nClosing connection...");
      
        output.close();
        input.close();
      
        field.append("\nConnection Closed.");
    }
    
    public static void main(String[] args){
        new Clients();
    }
}