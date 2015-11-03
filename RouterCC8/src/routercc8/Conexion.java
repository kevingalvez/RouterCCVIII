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
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Conexion implements Runnable {

    Socket socket;
    String name, myname;
    int keepalive, msgRouter;
    int port;
    HashMap adyacentes;
    public static DistanceVector dv;

    //new Conexion(connectionSocket, keepalive, "A", msgrouter,  portNumber,s);
    public Conexion(Socket s, int ka, String mn, int msgRouter, int port, HashMap adyacentes) {
        this.socket = s;
        this.keepalive = ka;
        this.myname = mn;
        this.msgRouter = msgRouter;
        this.port = port;
        this.adyacentes = adyacentes;

    }

    private static void mandaHello(String IP, int port, String myName) {

        try {

            Socket cliente = new Socket(IP, port);
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
            outToServer.write("From:" + myName);
            outToServer.newLine();
            outToServer.write("Type:HELLO");
            outToServer.newLine();
            outToServer.flush();
            cliente.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void mandaWelcome(String IP,int port,String myName) {
        try {

            Socket cliente = new Socket(IP, port);
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
            outToServer.write("From:" + myName);
            outToServer.newLine();
            outToServer.write("Type:WELCOME");
            outToServer.newLine();
            outToServer.flush();
            outToServer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean esperaRespuesta(BufferedReader inFromServer) {
        int TotalLineas = 2, cont = 0;
        String type = "";
        String from = "";
        try {
            for (cont = 0; cont < TotalLineas; cont++) {
                String msg = inFromServer.readLine();
                if(msg.equals(null))
                    break;
                String[] arr = msg.split(":");
                if (arr[0].toUpperCase().equals("FROM")) {
                    from = arr[0];

                } else if (arr[0].toUpperCase().equals("TYPE")) {
                    type = arr[1];
                    if (type.toUpperCase().equals("WELCOME")) {

                    }
                    if (type.toUpperCase().equals("HELLO")) {
                        String IP = adyacentes.get(from).toString();
                    }
                    if (type.toUpperCase().equals("DV")) {
                        String[] message = inFromServer.readLine().split(":");
                        for (int i = 0; i < Integer.parseInt(message[1]); i++) {
                            String[] ady = inFromServer.readLine().split(":");
                            dv.recibeMinimo(from, arr[0], Integer.parseInt(ady[1]));
                        }
                        HashMap<String, Integer> d = dv.calcular();

                    }

                }
                if (type.toUpperCase().equals("KEEPALIVE")) {

                }

                System.out.println(arr[0] + ":" + arr[1]);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

    @Override
    public void run() {
        System.out.println("Conexion aceptada!");
        BufferedWriter outToServer = null;
        BufferedReader inFromServer = null;
        try {
            //outToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            esperaRespuesta(inFromServer);

            //mandaWelcome();
        } catch (IOException e) {

        }

        while (true) {
            try {

                esperaRespuesta(inFromServer);
                Thread.sleep(5000);

            } catch (Exception e) {

            }
        }
    }

    public static void main(String args[]) throws Exception {
        int MaxThreads, portNumber, keepalive, msgrouter;
        MaxThreads = portNumber = keepalive = msgrouter = 0;

        JSONParser parser = new JSONParser();
        try {
            JSONArray arr = (JSONArray) parser.parse(new FileReader("./src/routercc8/conf.json"));

            JSONObject j = (JSONObject) arr.get(0);
            portNumber = Integer.parseInt(j.get("port").toString());
            MaxThreads = Integer.parseInt(j.get("maxthreads").toString());
            keepalive = Integer.parseInt(j.get("keepalive").toString());
            msgrouter = Integer.parseInt(j.get("msgrouter").toString());

        } catch (Exception e) {
            e.printStackTrace();

        }
        ServerSocket welcomeSocket = new ServerSocket(portNumber);

        ThreadPool thread = new ThreadPool(MaxThreads, 1);
        BufferedReader archivo = new BufferedReader(new FileReader("./src/routercc8/conf.ini"));
        String read = "";
        String MyName = "B";

        HashMap s = new HashMap();
        while ((read = archivo.readLine()) != null) {
            String[] arr = read.split(":");
            s.put(arr[0], arr[2]);
            Conexion.mandaHello(arr[2], portNumber, MyName);

        }

        DistanceVector dv = new DistanceVector("B", "./src/routercc8/conf.ini");
        Conexion.dv = dv;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                System.out.println(dv.mins.toString());

            }

        }, 0, 5000);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();

            Conexion request = new Conexion(connectionSocket, keepalive, MyName, msgrouter, portNumber, s);
            thread.execute(request);
        }

//        while (true) {
//            Socket connectionSocket = welcomeSocket.accept();
//            Conexion request = new Conexion(connectionSocket, adyacente, ka, "A");
//            thread.execute(request);
//        }
    }
}
