package nio.task02;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LoadClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        int n = 200;
        List<Socket> sockets = new ArrayList<>();
        for (int i = 0 ; i < n ; i++) {
            Socket s = new Socket("localhost",9000);
            s.getOutputStream().write(("Hello " + i + '\n').getBytes());
            sockets.add(s);
        }

        System.out.println("Открыто соединений: " + sockets.size());
        System.out.println("Жму Enter — закрыть всё и выйти.");

        Thread.sleep(60000);
    }
}
