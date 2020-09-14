package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Represents client session
 */
public class ClientHandler {
    private String name;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private boolean timeOut = false;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public void timeOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        System.out.println("timeOut: 120sec");
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (timeOut) {
                        closeConnection();
                    }
            }
        }).start();
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    timeOut();
                    authenticate();
                    readMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }
        }).start();
    }
//
    public void authenticate() throws IOException {
        System.out.println("Client auth is on going...");
        while (true) {
            String loginInfo = in.readUTF();
            if (loginInfo.startsWith("-auth")) {
                // -auth l1 p1
                String[] splittedLoginInfo = loginInfo.split("\\s");
                AuthenticationService.Client maybeClient = server.getAuthenticationService()
                        .findByLoginAndPassword(
                                splittedLoginInfo[1],
                                splittedLoginInfo[2]
                        );
                if (maybeClient != null) {
                    if (!server.checkLogin(maybeClient.getName())) {
                        sendMessage("status: authok");
                        name = maybeClient.getName();
                        server.broadcast(String.format("%s came in", name));
                        System.out.println("Client[" + name + "] auth completed");
                        server.subscribe(this);
                        timeOut = false;
                        return;
                    } else {
                        sendMessage(String.format("%s already logged in", maybeClient.getName()));
                    }
                } else {
                    sendMessage("Incorrect credentials");
                    timeOut = true;
//                    return;
                }
            } else
                timeOut = true;
//            return;
        }
    }

    public void closeConnection() {
        server.unsubscribe(this);
        server.broadcast(String.format("%s left", name));
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            String formatterMessage = String.format("Message from %s: %s", name, message);
            System.out.println(formatterMessage);
            if (message.equalsIgnoreCase("-exit")) {
                return;
            }
            server.broadcast(formatterMessage);
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
