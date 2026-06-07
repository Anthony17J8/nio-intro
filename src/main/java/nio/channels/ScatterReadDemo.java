package nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ScatterReadDemo {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("You should set the source file");
        }

        try (FileChannel in = FileChannel.open(Path.of(args[0]), StandardOpenOption.READ)) {
            ByteBuffer header = ByteBuffer.allocate(4);
            ByteBuffer body = ByteBuffer.allocate(128);
            ByteBuffer[] bufs = {header, body};
            long read = in.read(bufs);
            header.flip();
            body.flip();
            int len = header.getInt();
            byte[] res = new byte[len];
            while (body.hasRemaining()) {
                body.get(res, 0, body.limit());
            }
            String out = new String(res, StandardCharsets.UTF_8);
            System.out.println("Количество считанных байтов из канала в буферы: " + read);
            System.out.println("Количество считанных байтов из header: " + header.limit());
            System.out.println("Количество считанных байтов из body: " + body.limit());
            System.out.println("Result: " + out);

        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }

    }
}
