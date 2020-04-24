package com.prime.net.sockets;

import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Beautify{
    private UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
    
    public Beautify(Component component, int index){
        try{
           UIManager.setLookAndFeel(looks[index].getClassName());
        }
        catch(Exception e){System.err.print("look and feel not found");};
        
        SwingUtilities.updateComponentTreeUI(component);
    }

}