/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routercc8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KevinAlfonso
 */
public class MsgRequest implements Runnable{
    
    Socket socket;
    String inbox;
    String MyName;
    int port_number;
    DistanceVector dv;
    int sequence = 0;
    
    public MsgRequest(Socket s, String inbox, String MyName, DistanceVector dv, int port_number)  {
        this.socket = s;
        this.inbox = inbox;
        this.MyName = MyName;
        this.dv = dv;
        this.port_number = port_number;
    }
    
    public void log(String msg) {
        System.out.println(msg);
    }
    
    public void SendMail(String hostname, HashMap msgsequence) throws Exception
    {

            Socket clientSocket = new Socket(hostname, port_number);
            
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            outToServer.write("FROM:" + msgsequence.get("FROM").toString());
            outToServer.newLine();
            outToServer.write("TO:" + msgsequence.get("TO").toString());
            outToServer.newLine();
            outToServer.write("MSG:" + msgsequence.get("MSG").toString());
            outToServer.newLine();
            outToServer.write("EOF");
            outToServer.newLine();
            outToServer.flush();
            outToServer.close();
            clientSocket.close();
            System.out.println("FROM:" + msgsequence.get("FROM").toString());
            System.out.println("TO:" + msgsequence.get("TO").toString());
            System.out.println("MSG:" + msgsequence.get("MSG").toString());
    }    
    
    public void saveMail(HashMap msgsequence)
                             throws FileNotFoundException, IOException 
    {	
            DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy HH mm ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48

            String mail_from = String.valueOf(msgsequence.get("FROM"));
            String rcpt_to = String.valueOf(msgsequence.get("TO"));
            String data = String.valueOf(msgsequence.get("MSG"));

            String path = this.inbox+dateFormat.format(date)+".txt";
            //Writer output = new BufferedWriter(new FileWriter(new File(path)));	
            BufferedWriter output = new BufferedWriter(new FileWriter(new File(path)));
            output.write("FROM:"+mail_from);
            output.newLine();
            output.write("TO:"+rcpt_to);
            output.newLine();
            output.write("MSG:"+data);	
            output.newLine();
            output.flush();
            output.close();
            
            System.out.println("Mensaje Almacenado!");
    } 
    
    public String getIP(String nodo) throws IOException
    {
        BufferedReader archivo = new BufferedReader(new FileReader("./src/routercc8/conf.ini"));
        String read = "";
        boolean v = false;
        String result = "";
        while (((read = archivo.readLine()) != null)&&(!v)) {
            String[] arr = read.split(":");
            if (arr[0].equals(nodo.split(":")[0])) {
                v = true;
                result = arr[2];
            }
        }          
        return result;
    }    

    @Override
    public void run() {
        System.out.println("Recibiendo Mensaje!");
        String clientSentence = "";
        HashMap msgsequence = new HashMap();
        while (true)
        {
            try {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (!((clientSentence = inFromClient.readLine()).toUpperCase().equals("EOF"))) {
                    switch (sequence) {
                        case 0: {
                            String[] temp = clientSentence.split(":");
                            if (temp[0].toUpperCase().equals("FROM")) {
                                    msgsequence.put(temp[0].toUpperCase(),temp[1]);
                                    sequence++;
                            } else 
                            {
                                System.out.println("No reconoce el comando");
                            }
                            break;
                        }
                        case 1:{
                            String[] temp = clientSentence.split(":");
                            if (temp[0].toUpperCase().equals("TO")) {
                                    msgsequence.put(temp[0].toUpperCase(),temp[1]);
                                    sequence++;
                            } else 
                            {
                                System.out.println("No reconoce el comando");
                            }
                            break;
                        }
                        case 2:{
                            String[] temp = clientSentence.split(":");
                            if (temp[0].toUpperCase().equals("MSG")) {
                                    msgsequence.put(temp[0].toUpperCase(),temp[1]);
                                    sequence++;
                            } else 
                            {
                                System.out.println("No reconoce el comando");
                            }
                            break;
                        }                          
                    }
                    log(clientSentence);
                }
            } catch (IOException ex) {
                Logger.getLogger(MsgRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
        }
        try {
            if (msgsequence.get("TO").toString().toUpperCase().equals(this.MyName)) {
                saveMail(msgsequence);
            } else {
                System.out.println("SEEEND MESSSAGE GETMIN:" +dv.getMin(msgsequence.get("TO").toString()) );
                System.out.println(getIP(dv.getMin(msgsequence.get("TO").toString())));
                SendMail(getIP(dv.getMin(msgsequence.get("TO").toString())),msgsequence);
            }
            
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        } catch (IOException ex) {
            Logger.getLogger(MsgRequest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MsgRequest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(MsgRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
