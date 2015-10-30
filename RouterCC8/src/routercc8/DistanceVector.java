/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routercc8;

import java.util.*;
import java.io.*;

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
            archivo = new BufferedReader(new FileReader(file));

            while ((read = archivo.readLine()) != null) {
                String[] arr = read.split(":");
                dv.add(nombre + ":" + arr[0] + ":" + arr[1]);
                adyacentes++;
                ady.put(arr[0], 0); //saber cuales son adyacentes
                mins.put(arr[0], arr[0] + ":"+ arr[1]);
                //A-B:3
                //A-C:23
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void reciveMinimo(String router, String destino, int costo) {
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

    public boolean calcular() {
        boolean isDirty=false;
        for (int i = 0; i < dv.size(); i++) {
            String[] arr = dv.elementAt(i).toString().split(":", 3);
            try {
                String [] minimo = mins.get(arr[1]).split(":");
                if (Integer.parseInt(minimo[1]) > Integer.parseInt(arr[2])) {
                    mins.put(arr[1], arr[0]+":"+arr[2]);
                    isDirty= true;
                }
            } catch (NullPointerException e) {
                mins.put(arr[1],  arr[0]+":"+arr[2]);
                isDirty=true;

            }

        }
        return isDirty;
    }

    public static void main(String args[]) {
        DistanceVector d = new DistanceVector("A", ".\\build\\classes\\routercc8\\conf.ini");
        System.out.println(d.mins.toString());
        
        d.reciveMinimo("B", "C", 2);
        d.reciveMinimo("B", "A", 3);
        if(d.calcular())
            System.out.println("Cambio Minimos");
        System.out.println(d.mins.toString());

        d.reciveMinimo("C", "D", 5);
        d.reciveMinimo("C", "B", 2);
        d.reciveMinimo("C", "A", 23);
        if(d.calcular())
            System.out.println("Cambio Minimos");
        System.out.println(d.mins.toString());

        
        d.reciveMinimo("D", "C", 5);
        if(d.calcular())
            System.out.println("Cambio Minimos");
        System.out.println(d.mins.toString());
     
        
        //T  = 1;
        d.reciveMinimo("B","D",7);
        if(d.calcular())
            System.out.println("Cambio Minimos");
        System.out.println(d.mins.toString());
        
        
        
        d.reciveMinimo("C","A",5);
        if(d.calcular())
            System.out.println("Cambio Minimos");
        System.out.println(d.mins.toString());
        
        
        d.reciveMinimo("D","A",28);
        d.reciveMinimo("D","B",7);
        if(d.calcular())
            System.out.println("Cambio Minimos");
        System.out.println(d.mins.toString());


        //T=2
        
        
        d.reciveMinimo("D","B",10);
        if(d.calcular())
            System.out.println("Cambio Minimos");
        System.out.println(d.mins.toString());
        System.out.println(d.dv.toString());
    }

}
