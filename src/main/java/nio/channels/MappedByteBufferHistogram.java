package nio.channels;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class MappedByteBufferHistogram {
    private final Path src;

    public MappedByteBufferHistogram(Path src) {
        this.src = src;
    }

    public long[] createHistogram() {
        try (FileChannel in = FileChannel.open(src, StandardOpenOption.READ)) {
            long[] histogram = new long[256];
            MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, in.size());
            while (buf.hasRemaining()) {
                byte signed = buf.get();
                int unsigned = signed & 0xFF;
                histogram[unsigned] += 1;
            }
            return histogram;
        } catch (IOException exc) {
            throw new RuntimeException("Error during read file", exc);
        }
    }
}
