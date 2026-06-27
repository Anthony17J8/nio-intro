package nio.selectors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SelectorOpWriteDemo {

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(9000));
        server.configureBlocking(false);
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
                } else if (key.isWritable()) {
                    handleWrite(key);
                }
            }
        }
    }

    private static void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer attachment = (ByteBuffer) key.attachment();
         SocketChannel client = (SocketChannel) key.channel();
        client.write(attachment);

        if (!attachment.hasRemaining()) {
            key.attach(null);
            key.interestOps(key.interestOps() | ~SelectionKey.OP_WRITE);
        }
    }

    private static void handlerRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer bb = ByteBuffer.allocate(128);
        int read = client.read(bb);
        if (read == -1) {
            key.cancel();
            client.close();
            System.out.println("Клиент отключился");
            return;
        }

        bb.flip();
        client.write(bb);
        if (bb.hasRemaining()) {
            key.attach(bb);
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        }
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
