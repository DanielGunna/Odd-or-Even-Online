/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labreso;

import labreso.controller.Server;

/**
 *
 * @author 940437
 */
public class Labreso {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Server(4000).startServer();
    }
    
}
