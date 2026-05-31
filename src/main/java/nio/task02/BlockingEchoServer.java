package nio.task02;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingEchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9000);
        System.out.println("Listening on 9000...");
        while (true) {
            Socket client = server.accept();
            new Thread(() -> handle(client)).start();
        }
    }

    private static void handle(Socket client) {
        try (client;
             InputStream in = client.getInputStream();
             OutputStream out = client.getOutputStream()) {

            byte[] buf = new byte[1024];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Client gone: " + e.getMessage());
        }
    }
}
