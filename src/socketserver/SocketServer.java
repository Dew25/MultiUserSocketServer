/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Melnikov
 */
public class SocketServer {
    public static final int PORT = 1234;
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private final EntityManager em;

    public SocketServer() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SocketServerPU");
        this.em = emf.createEntityManager();
        this.clients = (ArrayList<ClientHandler>) em.createQuery("SELECT c FROM Client c").getResultList();
    }
    
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен на порту " + PORT);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler clientThread = new ClientHandler(socket);
                    clients.add(clientThread);
                    clientThread.start();
                } catch (IOException e) {
                    System.out.println("Не удалось установить соединение с клиентом: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Не удалось запустить сервер: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Не удалось закрыть серверный сокет: " + e.getMessage());
            }
        }
    }
}
