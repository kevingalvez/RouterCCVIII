/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routercc8;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author KevinAlfonso
 */
public class RouterCC8 {

    
    
    public static void main(String args[]) throws Exception{
        System.out.println("Hello");
        int MaxThreads = 5;
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
