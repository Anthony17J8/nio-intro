package nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ByteBufferCounter {

    private final Path src;

    public ByteBufferCounter(Path src) {
        this.src = src;
    }

    public long countLines() {
        try (FileChannel in = FileChannel.open(src, StandardOpenOption.READ)) {
            ByteBuffer buf = ByteBuffer.allocate(64 * 1024);
            int lineCounter = 0;
            while ((in.read(buf)) != -1) {
                buf.flip();
                while (buf.hasRemaining()) {
                    if (buf.get() == '\n') {
                        lineCounter++;
                    }
                }
                buf.clear();
            }
            return lineCounter;
        } catch (IOException exc) {
            System.out.println("Read file error: " + exc);
            System.exit(1);
            return 0;
        }
    }
}
