package nio.channels;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MappedByteBufferCounter {

    private final Path src;

    public MappedByteBufferCounter(Path src) {
        this.src = src;
    }

    public long countLines() {
        try (FileChannel in = FileChannel.open(src, StandardOpenOption.READ)) {
            MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, in.size());
            long lineCounter = 0;
            while (buf.hasRemaining()) {
                if (buf.get() == '\n') {
                    lineCounter++;
                }
            }
            return lineCounter;
        } catch (IOException exc) {
            System.out.println("Read file error: " + exc);
            System.exit(1);
            return 0;
        }
    }

    public long countLinesRandomAccess() {
        try (FileChannel in = FileChannel.open(src, StandardOpenOption.READ)) {
            MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, in.size());
            long lineCounter = 0;
            for (int i = 0; i < buf.limit(); i++) {
                if (buf.get() == '\n') {
                    lineCounter++;
                }
            }
            return lineCounter;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }
}
