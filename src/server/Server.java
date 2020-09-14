package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private Set<ClientHandler> clientHandlers;
    private AuthenticationService authenticationService;

    public Server() {
        this.clientHandlers = new HashSet<>();
        this.authenticationService = new AuthenticationService();
        start(8888);
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    private void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            listenClients(serverSocket);
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong during server start-up");
        }
    }

    private void listenClients(ServerSocket serverSocket) throws IOException {
        while (true) {
            System.out.println("Server is looking for a client...");
            Socket client = serverSocket.accept();
            System.out.println("Client accepted: " + client);
            new ClientHandler(client, this);
        }
    }

    public void broadcast(String incomingMessage) {
        for (ClientHandler ch : clientHandlers) {
            ch.sendMessage(incomingMessage);
        }
    }

    public void subscribe(ClientHandler client) {
        clientHandlers.add(client);
    }

    public void unsubscribe(ClientHandler client) {
        clientHandlers.remove(client);
    }
//
    public boolean checkLogin(String name) {
        for (ClientHandler ch : clientHandlers) {
            if (ch.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
