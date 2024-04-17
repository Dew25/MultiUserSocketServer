/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Melnikov
 */
public class ChatServer implements Runnable{
   
    private final EntityManager em;
    private final Socket clientSocket;
    private boolean auth;

    ChatServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SocketServerPU");
        this.em = emf.createEntityManager();
    }
    /**
     * messageJSON= {
     *  "clientToken": null,
     *  "auth":{
     *      "login":"";,
     *      "Password":""
     *  },
     *  "message":""
     * }.
     * 
     * сервер отправляет сообщение клиенту с полем "clientToken": null
     * клиент с окном авторизации отсылает login; password;
     * сервер получает login:password, создает токен и отсылает сообщение "clientToken": "token"
     * сервер добавляет пользователя в список клиентов чата. 
     * Авторизация совершена
     * клиент с оном чата чатится, поле "clientToken": "token" отправляется на сервер
     * сервер получает сообщение с токеном отправляет его всем пользователям из списка.
     * 
     * клиент присылает null "|| "exit"
     * сервер удаляет клиента из списка и выходит из слушающего цыкла и закрывает 
     *  BufferedReader in.close();
     *  clientSocket.close();
     * 
     */
    @Override
    public void run(){
        String clientToken = ((Double)Math.random()).toString();
        try {
            try (PrintWriter currentClientOut = new PrintWriter(clientSocket.getOutputStream(), true)) {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while (true) {
                    String inputLine = in.readLine();
                    if(inputLine == null || "exit".equals(inputLine)){
                        break;
                    }
                    JSONObject messageJSON = new JSONObject(inputLine);
                    if(!messageJSON.getString("clientToken").equals(clientToken)){
                        currentClientOut.println(inputLine);
                        SocketServer.clientsOut.add(currentClientOut);
                    }else{
                        messageJSON.put("message", "Авторизуйтесь");
                        currentClientOut.println(messageJSON.toString());
                    }
                    System.out.println("Message from client: " + inputLine);
                    //send all clients
                    PrintWriter clientOut = null;
                    for (int i = 0; i < SocketServer.clientsOut.size(); i++) {
                        clientOut = SocketServer.clientsOut.get(i);
                        if(clientOut != null) clientOut.println(messageJSON.toString());
                    }
                }
                in.close();
                SocketServer.clientsOut.remove(currentClientOut);
                clientSocket.close();
            } catch (JSONException ex) {
                System.out.println("JSONObject error: "+ex.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
            
        
    }

    
    
}
