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
    Socket cliente;

    HashMap sockEscritura;
    Timer timer;
    boolean isConnected = true;
    int kill = 0;
    Timer killswitch;
    String connectedTo = "";

    //new Conexion(connectionSocket, keepalive, "A", msgrouter,  portNumber,s);
    public Conexion(Socket s, int ka, String mn, int msgRouter, int port, HashMap adyacentes, HashMap sockEscritura) {
        this.socket = s;
        this.keepalive = ka;
        this.myname = mn;
        this.msgRouter = msgRouter;
        this.port = port;
        this.adyacentes = adyacentes;
        this.sockEscritura = sockEscritura;

    }

    private static Socket mandaHello(String IP, int port, String myName) {

        try {

            Socket client = new Socket();
            client.connect(new InetSocketAddress(IP, port), 500);
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            outToServer.write("From:" + myName);
            outToServer.newLine();
            outToServer.write("Type:HELLO");
            outToServer.newLine();
            outToServer.flush();
            //cliente.close();
            System.out.println("FROM:" + myName);
            System.out.println("TYPE:HELLO");
            return client;

        } catch (ConnectException ex) {
            System.out.println("mandaHello: Server " + IP + " not listening on port " + port);
            return null;

        } catch (SocketTimeoutException e) {
            System.out.println("Server " + IP + " not listening on port " + port);
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    private void mandaKeepAlive() {

        try {

            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
            outToServer.write("From:" + myname);
            outToServer.newLine();
            outToServer.write("Type:KeepAlive");
            outToServer.newLine();
            outToServer.flush();

            System.out.println("<FROM:" + myname);
            System.out.println("TYPE:KeepAlive");
            System.out.println("TO:" + connectedTo + ">");

        } catch (Exception e) {
            System.out.println("KeepAliveNotReached to:" + connectedTo);
            //e.printStackTrace();
        }
    }

    private void mandaMinimos(Vector dv, HashMap ady) {

        try {

            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));

            outToServer.write("From:" + myname);
            outToServer.newLine();
            outToServer.write("Type:DV");
            outToServer.newLine();
            outToServer.write("Len:" + dv.size());
            outToServer.newLine();
            System.out.println("From:" + myname);
            System.out.println("Type:DV");
            System.out.println("Len:" + dv.size());
            for (int i = 0; i < dv.size(); i++) {
                outToServer.write(dv.get(i).toString());
                outToServer.newLine();
                System.out.println(dv.get(i).toString());
            }
            outToServer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mandaWelcome() {
        try {

            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
            outToServer.write("From:" + myname);
            outToServer.newLine();
            outToServer.write("Type:WELCOME");
            outToServer.newLine();
            outToServer.flush();

            System.out.println("From:" + myname);
            System.out.println("Type:Welcome");

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
                if (msg.equals(null)) {
                    break;
                }
                String[] arr = msg.split(":");
                if (arr[0].toUpperCase().equals("FROM")) {
                    from = arr[1];
                    connectedTo = arr[1];
                    System.out.println("esperaRespuesta: FROM" + from);

                } else if (arr[0].toUpperCase().equals("TYPE")) {
                    type = arr[1];
                    if (type.toUpperCase().equals("WELCOME")) {
                        System.out.println("EsperaRespuesta Welcome " + from);
                        this.cliente = (Socket) sockEscritura.get(from);
//                        Iterator entries = dv.mins.entrySet().iterator();
//                        Vector newmin = new Vector();
//                        while (entries.hasNext()) {
//                            Map.Entry entry = (Map.Entry) entries.next();
//                            newmin.add(entry.getKey().toString() + ":" + entry.getValue().toString());
//
//                        }
                        //mandaMinimos(newmin, adyacentes);
                        mandaMinimos(dv.dv, adyacentes);

                        timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {

                            @Override
                            public void run() {
                                Vector newmin = dv.calcular();
                                System.out.println("Calcular");
                                System.out.println("DVmin." + dv.mins.toString());
                                System.out.println("DV" + dv.dv.toString());
                                if (!newmin.isEmpty()) {
                                    //Enviar Minimos Nuevos

                                    mandaMinimos(newmin, adyacentes);
                                    System.out.println("nuevos Minimos: " + newmin.toString());

                                } else {
                                    mandaKeepAlive();
                                }

                            }

                        }, 0, msgRouter);

                        killswitch = new Timer();
                        killswitch.scheduleAtFixedRate(new TimerTask() {

                            @Override
                            public void run() {
                                kill++;

                                if (kill >= keepalive) {
                                    dv.recibeMinimo(myname, connectedTo, 99);
                                    Vector newmin = dv.calcular();
                                    System.out.println("Kill Calcular");
                                    System.out.println("Kill DVmin." + dv.mins.toString());
                                    System.out.println("Kill DV" + dv.dv.toString());
                                    if (!newmin.isEmpty()) {
                                        //Enviar Minimos Nuevos

                                        mandaMinimos(newmin, adyacentes);
                                    } else {
                                        System.out.println("Kill nuevos Minimos: " + newmin.toString());
                                    }
                                    timer.cancel();
                                    killswitch.cancel();
                                }
                            }

                        }, 0, msgRouter * keepalive);

                    }
                    if (type.toUpperCase().equals("HELLO")) {
                        String IP = adyacentes.get(from).toString();
                        System.out.println("esperaRespuesta Hello " + IP);
                        cliente = new Socket(IP, port);
                        mandaWelcome();

                        timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {

                            @Override
                            public void run() {
                                Vector newmin = dv.calcular();
                                System.out.println("Calcular");
                                System.out.println("DVmin." + dv.mins.toString());
                                System.out.println("DV" + dv.dv.toString());
                                if (!newmin.isEmpty()) {
                                    //Enviar Minimos Nuevos

                                    mandaMinimos(newmin, adyacentes);
                                    System.out.println("nuevos Minimos: " + newmin.toString());

                                } else {
                                    mandaKeepAlive();
                                }

                            }

                        }, 0, msgRouter);
                    }
                    if (type.toUpperCase().equals("DV")) {
                        String[] message = inFromServer.readLine().split(":");
                        for (int i = 0; i < Integer.parseInt(message[1]); i++) {
                            String[] ady = inFromServer.readLine().split(":");
                            System.out.println("EsperaRespuesta dv " + ady[1] + ":" + ady[2]);
                            dv.recibeMinimo(from, ady[1], Integer.parseInt(ady[2]));
                        }

                    }

                }
                if (type.toUpperCase().equals("KEEPALIVE")) {
                    System.out.println("esperaRespuesta Keepalive" + from);
                    kill = 0;

                }

                System.out.println("esperaRespuesta Last pritnln" + arr[0] + ":" + arr[1]);
            }

        } catch (NullPointerException ex) {

            //ex.printStackTrace();
            //Do Nothing, no data on read
            //System.out.println("NO READ");
        } catch (Exception ex) {
            timer.cancel();
            isConnected = false;
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
            //esperaRespuesta(inFromServer);

            //mandaWelcome();
        } catch (IOException e) {

        }

        while (isConnected) {
            try {

                esperaRespuesta(inFromServer);
                //Thread.sleep(5000);

            } catch (Exception e) {
                //break;

            }
        }

    }

    public static void main(String args[]) throws Exception {

        JSONParser parser = new JSONParser();
        try {
            final int MaxThreads, portNumber, keepalive, msgrouter;
            JSONArray arry = (JSONArray) parser.parse(new FileReader("./src/routercc8/conf.json"));

            JSONObject j = (JSONObject) arry.get(0);
            portNumber = Integer.parseInt(j.get("port").toString());
            MaxThreads = Integer.parseInt(j.get("maxthreads").toString());
            keepalive = Integer.parseInt(j.get("keepalive").toString());
            msgrouter = Integer.parseInt(j.get("msgrouter").toString());

            ServerSocket welcomeSocket = new ServerSocket(portNumber);

            ThreadPool thread = new ThreadPool(MaxThreads, 1);
            BufferedReader archivo = new BufferedReader(new FileReader("./src/routercc8/conf.ini"));
            String read = "";
            final String MyName = "B";

            HashMap s = new HashMap();
            HashMap sockets = new HashMap();

            while ((read = archivo.readLine()) != null) {
                String[] arr = read.split(":");
                s.put(arr[0], arr[2]);
                sockets.put(arr[0], Conexion.mandaHello(arr[2], portNumber, MyName));

            }

            DistanceVector dv = new DistanceVector(MyName, "./src/routercc8/conf.ini");
            Conexion.dv = dv;

            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                Conexion request = new Conexion(connectionSocket, keepalive, MyName, msgrouter, portNumber, s, sockets);
                thread.execute(request);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
