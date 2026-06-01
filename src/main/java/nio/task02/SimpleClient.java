package nio.task02;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SimpleClient {

    public static void main(String[] args) throws InterruptedException {
        try(Socket s = new Socket("localhost", 9000)) {
            OutputStream os = s.getOutputStream();
            os.write("AAAA".getBytes());
            os.write("BBBB".getBytes());
            os.write("CCC".getBytes());
            os.flush();

        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
        Thread.sleep(1000);
    }
}
