package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplicationOne {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8888);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            Scanner scanner = new Scanner(System.in);

            out.writeUTF("-auth l1 p2");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String message = in.readUTF();
                            System.out.println(message);
                            if (message.contains("Incorrect credentials")) {
                                System.out.println("Type login / password:");
                                String msg = scanner.nextLine();
                                out.writeUTF(msg);
//                                out.writeUTF("-auth l1 p1");

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }).start();
//
            out.writeUTF("-exit");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
