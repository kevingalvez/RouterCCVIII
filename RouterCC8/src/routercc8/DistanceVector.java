/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routercc8;

import java.util.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;

/**
 *
 * @author Javier
 */
public class DistanceVector {

    Vector dv = new Vector();
    HashMap ady = new HashMap();
    HashMap<String, String> mins = new HashMap<String, String>();
    String nombre;
    BufferedReader archivo;
    boolean hasChanged;
    int adyacentes = 0;

    //inicializa el programa
    public DistanceVector(String nombre, String file) {
        this.nombre = nombre;
        String read;
        hasChanged = true;
        try {
            archivo = new BufferedReader(new FileReader("./src/routercc8/conf.ini"));

            

            while ((read = archivo.readLine()) != null) {
                String[] arr = read.split(":");
                dv.add(nombre + ":" + arr[0] + ":" + arr[1]);
                adyacentes++;
                ady.put(arr[0], 0); //saber cuales son adyacentes
                mins.put(arr[0], arr[0] + ":" + arr[1]);
                //A-B:3
                //A-C:23
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void recibeMinimo(String router, String destino, int costo) {
        if (destino != nombre || !ady.containsKey(router)) {
            String entry = router + ":" + destino + ":" + costo;

            for (int i = 0; i < adyacentes; i++) {

                String[] arr = dv.elementAt(i).toString().split(":", 3);
                if (arr[1].equals(router)) {
                    int total = Integer.valueOf(arr[2]) + costo;
                    dv.add(router + ":" + destino + ":" + Integer.toString(total));
                }

            }

        }
    }

    public HashMap<String,Integer> calcular() {
        boolean isDirty = false;
        HashMap <String,Integer> ret  = new HashMap<String,Integer>();
        for (int i = 0; i < dv.size(); i++) {
            String[] arr = dv.elementAt(i).toString().split(":", 3);
            try {
                String[] minimo = mins.get(arr[1]).split(":");
                if (Integer.parseInt(minimo[1]) > Integer.parseInt(arr[2])) {
                    mins.put(arr[1], arr[0] + ":" + arr[2]);
                    ret.put(arr[1],Integer.parseInt(arr[2]));
                    
                }
            } catch (NullPointerException e) {
                mins.put(arr[1], arr[0] + ":" + arr[2]);
                ret.put(arr[1],Integer.parseInt(arr[2]));
                

            }

        }
        //limpiarDV();
        return ret;
    }

    public String getMin(String nodo) {
        try {
            String minimo = mins.get(nodo);
            return minimo;

        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void limpiarDV() {
        int tamaño = dv.size();
        for (int i = 0; i < tamaño; i++) {
            String[] arr = dv.elementAt(i).toString().split(":", 3);
            for (int j = i + 1; j < tamaño; j++) {
                String[] arr2 = dv.elementAt(j).toString().split(":", 3);
                if ((arr[0].equals(arr2[0])) && (arr[1].equals(arr2[1]))) {
                    if (Integer.parseInt(arr[2]) < Integer.parseInt(arr2[2])) {
                        tamaño--;
                        dv.remove(j);
                    } else {
                        dv.set(i, arr[0] + ":" + arr[1] + ":" + arr2[2]);
                        dv.remove(j);
                        tamaño--;

                    }
                }

            }
        }

    }

    public static void main(String args[]) {
        DistanceVector d = new DistanceVector("B", ".\\src\\routercc8\\conf.ini");
        HashMap <String,Integer> newmin = new HashMap<String,Integer>();
        System.out.println("Start:" + d.mins.toString());
        System.out.println(d.dv.toString());

        d.recibeMinimo("A", "B", 3);
        d.recibeMinimo("A", "C", 23);
        newmin=d.calcular();
        if (!newmin.isEmpty()) {
            System.out.println("nuevos Minimos: "+ newmin.toString());
        }
        System.out.println(d.mins.toString());
        System.out.println(d.dv.toString());

        d.recibeMinimo("C", "D", 5);
        d.recibeMinimo("C", "B", 2);
        d.recibeMinimo("C", "A", 23);
        newmin=d.calcular();
        if (!newmin.isEmpty()) {
            System.out.println("nuevos Minimos: "+ newmin.toString());
        }
        System.out.println(d.mins.toString());
        System.out.println(d.dv.toString());

        d.recibeMinimo("D", "C", 5);
        newmin=d.calcular();
        if (!newmin.isEmpty()) {
            System.out.println("nuevos Minimos: "+ newmin.toString());
        }
        System.out.println(d.mins.toString());
        System.out.println(d.dv.toString());

        System.out.println("T1");
        System.out.println();
        //T  = 1;
        d.recibeMinimo("A", "C", 5);
        d.recibeMinimo("A", "D", 28);
        newmin=d.calcular();
        if (!newmin.isEmpty()) {
            System.out.println("nuevos Minimos: "+ newmin.toString());
        }
        System.out.println(d.mins.toString());
        System.out.println(d.dv.toString());

        d.recibeMinimo("C", "A", 5);
        newmin=d.calcular();
        if (!newmin.isEmpty()) {
            System.out.println("nuevos Minimos: "+ newmin.toString());
        }
        System.out.println(d.mins.toString());
        System.out.println(d.dv.toString());

        d.recibeMinimo("D", "A", 28);
        d.recibeMinimo("D", "B", 7);
        newmin=d.calcular();
        if (!newmin.isEmpty()) {
            System.out.println("nuevos Minimos: "+ newmin.toString());
        }
        System.out.println(d.mins.toString());
        System.out.println(d.dv.toString());

        //T=2
        System.out.println("T2");
        System.out.println();

        d.recibeMinimo("D", "A", 10);
        newmin=d.calcular();
        if (!newmin.isEmpty()) {
            System.out.println("nuevos Minimos: "+ newmin.toString());
        }
        System.out.println(d.mins.toString());
        System.out.println(d.dv.toString());

        d.recibeMinimo("A", "D", 10);
        newmin=d.calcular();
        if (!newmin.isEmpty()) {
            System.out.println("nuevos Minimos: "+ newmin.toString());
        }
        System.out.println(d.mins.toString());
        System.out.println(d.getMin("A"));

        System.out.println(d.dv.toString());
    }

}
