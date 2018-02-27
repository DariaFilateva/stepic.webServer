package net.kiranatos.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyServerSocket implements Runnable {
    
    private static int port;

    public MyServerSocket(int port) { this.port = port; }

    @Override
    public void run() {
        try {
            ServerSocket serverSock = new ServerSocket(port);
            
            while (true) {
                Socket sock = serverSock.accept();                
                System.out.println("We got socket");
                ClientReader cReader = new ClientReader(sock);
                Thread r = new Thread (cReader);
                r.start();
                
                ClientWriter cWriter = new ClientWriter(sock);
                Thread w = new Thread (cWriter);
                cReader.setWriter( cWriter );
                while (!r.isAlive()) {}
                w.start();
            }   
        } catch (IOException ex) { Logger.getLogger(MyServerSocket.class.getName()).log(Level.SEVERE, null, ex); }
    }
}

class ClientReader implements Runnable {
    private BufferedReader reader;
    private Socket socket;
    private ClientWriter cWriter;

    public ClientReader(Socket socket) throws IOException {
        this.socket = socket;
        InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(isReader);
    }       
    
    public void setWriter(ClientWriter cWriter) { this.cWriter = cWriter;  }
    
    @Override
    public void run() {
        System.out.println("Server: ClientReader started");
        String message;
        int i = 0;
        try {
            while (true) {
                if ((message = reader.readLine()) != null) {
                    message = message;// + i++;                    
                    cWriter.setMessage(message, true);                    
                    System.out.println("MyServer: read: " +  message + " flag: " + cWriter.isFlag());
                    if (message.equals("Bue.")) { return;}
                }
            }
        } catch (IOException ex) { Logger.getLogger(ClientReader.class.getName()).log(Level.SEVERE, null, ex); }
    }                
}

class ClientWriter implements Runnable {
    private PrintWriter writer;
    private Socket socket;
    private AtomicBoolean flag;
    private String message;
    
    public ClientWriter(Socket socket) throws IOException {
            this.socket = socket;
            writer = new PrintWriter(socket.getOutputStream());            
            flag = new AtomicBoolean(false);
    }       
    
    @Override
    public void run() {     
        System.out.println("Server: ClientWriter started");
        while (true) {            
            if (flag.get()) {  
                //System.out.println("WE SEND MESSAGE");
                writer.println(message);
                writer.flush();
                
                if (message.equals("Bue.")) { 
                    System.out.println("Exit?");
                    try {
                        socket.close();
                    } catch (IOException ex) { Logger.getLogger(ClientWriter.class.getName()).log(Level.SEVERE, null, ex);  }
                    return;
                }
                flag.set(false);
            }            
        }
    }                

    public boolean isFlag() {
        return flag.get();
    }
    
    public void setMessage(String message, boolean flag){
        this.message = message;
        this.flag.set(flag);
    }
}
