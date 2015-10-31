/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routercc8;

/**
 *
 * @author KevinAlfonso
 */
import java.io.* ;
import java.net.* ;
import java.util.* ;

public class Conexion  implements Runnable{
    
    private int port = 9080;
    Socket socket;
    String name, myname;
    int keepalive;
    
    public Conexion(Socket s, String n, int ka, String mn)
    {
        this.socket = s;
        this.name = n;
        this.keepalive = ka;
        this.myname = mn;
    }
   
    private void mandaHello(BufferedWriter outToServer)
    {
        try 
        {
            outToServer.write("From:"+this.myname);
            outToServer.newLine();
            outToServer.write("Type:HELLO");
            outToServer.flush();
        } catch (Exception e) {
            
        }
    }
    
    public boolean esperaRespuesta(BufferedReader inFromServer)
    {
        try
        {
            String temp = inFromServer.readLine();
            System.out.println(temp);
        } catch (Exception ex)
        {
            
        }
        
        return true;
    }
    
    @Override
    public void run() {
        System.out.println("Conexion aceptada!");
        BufferedWriter outToServer = null;
        BufferedReader inFromServer = null;
        try
        {
            outToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));   		                            
        } catch (IOException e) 
        {
            
        }
        
        while (true) 
        {
            try
            {
                mandaHello(outToServer);
                esperaRespuesta(inFromServer);

            } catch (Exception e) {

            }
        }
    }
    
    public static void main(String args[]) throws Exception{
        int MaxThreads = 10;
        int portNumber = 9080;
        int ka = 90;
        ServerSocket welcomeSocket = new ServerSocket(portNumber);
        ThreadPool thread = new ThreadPool(MaxThreads,1);
         while(true) {             
            Socket connectionSocket = welcomeSocket.accept();
            Conexion request = new Conexion(connectionSocket, "B", ka,"A");
            thread.execute(request);
         } 	        
    }
}
