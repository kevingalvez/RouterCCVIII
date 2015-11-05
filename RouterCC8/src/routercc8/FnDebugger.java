/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routercc8;

import java.io.*;

/**
 *
 * @author javjimeno
 */
public class FnDebugger {

    long id;
    BufferedWriter out;

    public FnDebugger(long id) {
        this.id = id;
        try {
            out = new BufferedWriter(new FileWriter(id + ".txt"));
        } catch (Exception e) {
        }

    }

    public void out(String Message) {
        try {
            out.write(Message);
            out.newLine();
        } catch (Exception e) {
        }
    }
}
