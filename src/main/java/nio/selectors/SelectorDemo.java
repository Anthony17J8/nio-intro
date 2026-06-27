package nio.selectors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SelectorDemo {

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(9000));
        server.configureBlocking(false);
        // регистрируем канал, в селекторе и говорим что нам интересно событие accept connection
        server.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server is listening port 9000...");

        while (true) {
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> itr = selectionKeys.iterator();

            while (itr.hasNext()) {
                SelectionKey key = itr.next();
                itr.remove();

                int readyOps = key.readyOps();
                int i = key.interestOps();
                if (key.isAcceptable()) {
                    handleAccept(key, selector);
                } else if (key.isReadable()) {
                    handlerRead(key);
                }
            }
        }
    }

    private static void handlerRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer bb = ByteBuffer.allocate(64 * 1024);
        int read = client.read(bb);
        if (read == -1) {
            key.cancel();
            client.close();
            System.out.println("Клиент отключился");
            return;
        }

        bb.flip();
        client.write(bb);
        bb.clear();
    }


    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("Подключился клиент: " + client.getRemoteAddress());
    }
}
