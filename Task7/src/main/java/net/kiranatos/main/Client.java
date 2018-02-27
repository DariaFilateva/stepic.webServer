package net.kiranatos.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    
    public static void main(String[] args) {
        
        Thread client = new Thread (new MyClienteSocket(5050));
        client.start();
        
        System.out.println("Client started!");    
        
        try {
            
            client.join();
            
        } catch (InterruptedException ex) { Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);        }
    }    
}

class MyClienteSocket implements Runnable {
    private static int port;

    public MyClienteSocket(int port) { this.port = port; }

    @Override
    public void run() {
        try {            
            Socket sock = new Socket("127.0.0.1", port);
            
            Thread w = new Thread (new ServerWriter(sock));
            w.start();
            
            Thread r = new Thread (new ServerReader(sock));                
            r.start();

        } catch (IOException ex) { Logger.getLogger(MyClienteSocket.class.getName()).log(Level.SEVERE, null, ex); }
    }
}

class ServerWriter implements Runnable {
    public static int index = 0;
    private PrintWriter writer;
    private Socket socket;
    
    public ServerWriter(Socket socket) throws IOException {
        this.socket = socket;
        writer = new PrintWriter(socket.getOutputStream());            
        index++;
    }       
    
    @Override
    public void run() {
        int k = 0;
        System.out.println("Client: ServerWriter started");
        while (true) {
            writer.println(index + "c");
            writer.flush();                   
            k++;
            try {Thread.sleep(1000);} catch (InterruptedException ex) {}
            if (k==21) {
                writer.println("Bue.");
                writer.flush();      
                try {
                    socket.close();
                } catch (IOException ex) { Logger.getLogger(ServerWriter.class.getName()).log(Level.SEVERE, null, ex); }
                return;
            }
        }
    }                
}

class ServerReader implements Runnable {
    private BufferedReader reader;
    private Socket socket;
    
    public ServerReader(Socket socket) throws IOException {
        this.socket = socket;
        InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(isReader);
    }       
    
    @Override
    public void run() {     
        System.out.println("Client: ServerReader started");
        try {                
            while (true) {                
                String message = reader.readLine();
                System.out.println("Client: " + message);
                try {Thread.sleep(500);} catch (InterruptedException ex) {}                
            }
        } catch (IOException ex) { Logger.getLogger(ServerReader.class.getName()).log(Level.SEVERE, null, ex); }
    }  
}     