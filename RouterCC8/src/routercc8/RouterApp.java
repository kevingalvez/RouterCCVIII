/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package routercc8;

import java.util.Scanner;


/**
 *
 * @author kcoenich
 */


class UserInterface {

    String input = "";

    String origin = "";
    String destiny = "";
    String message = "";
    final String EOF_KEY = "EOF";

    public UserInterface(String origin) {

        this.origin = "FROM:" + origin;
    }

    public void sendMsg() {

        System.out.println("WELCOME TO YOUR ROUTER!!");

        while (!input.equals("exit")) {

            Scanner keyboard = new Scanner(System.in);
            System.out.print("TO:");
            destiny = keyboard.nextLine();
            System.out.print("MSG:");
            message = keyboard.nextLine();
            destiny = "TO:" + destiny;

            
        }

    }




}
