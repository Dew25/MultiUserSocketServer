/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketserver;

/**
 *
 * @author Melnikov
 */


public class MultiUserSocketServer {

    public static void main(String[] args){
       SocketServer socketServer = new SocketServer();
       socketServer.startChat();
    }

    
}

