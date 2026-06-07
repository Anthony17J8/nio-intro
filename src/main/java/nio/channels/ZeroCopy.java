package nio.channels;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

// Throughput = операций (копирований) в секунду. Чем больше — тем лучше.
// Альтернатива: Mode.AverageTime (среднее время на одно копирование).
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
// Прогрев: 3 итерации (даём JIT скомпилировать код). Замер: 5 итераций.
// 2 форка = два независимых запуска JVM, чтобы исключить случайность одной JVM.
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)   // буферы и файлы переиспользуются между итерациями одного потока
public class ZeroCopy {

    private static final int BUFFER_SIZE = 64 * 1024;

    private static final long FILE_SIZE = 5012L * 1024 * 1024; // размер файла 512 MB

    private Path srcPath;
    private Path dstPath;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        srcPath = Files.createTempFile("nio-bench-src", ".bin");
        dstPath = Files.createTempFile("nio-bench-dst", ".bin");

        // Наполняем исходный файл данными нужного размера.
        try (FileChannel src = FileChannel.open(srcPath,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            ByteBuffer chunk = ByteBuffer.allocate(BUFFER_SIZE);
            while (chunk.hasRemaining()) chunk.put((byte) 'x');
            chunk.flip();
            long written = 0;
            while (written < FILE_SIZE) {
                chunk.rewind();
                written += src.write(chunk);
            }
        }
    }

    @TearDown
    public void tearDown() throws IOException {
        Files.deleteIfExists(srcPath);
        Files.deleteIfExists(dstPath);
    }

    @Benchmark
    public void zeroCopy() {
        try (FileChannel in = FileChannel.open(srcPath, StandardOpenOption.READ);
             FileChannel out = FileChannel.open(dstPath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.CREATE_NEW)) {
            long position = 0;
            long size = in.size();
            while (position < size) {
                position += in.transferTo(position, size - position, out);
            }
        } catch (IOException exc) {
            System.out.println("Ошибка копирования: " + exc);
        }

    }

    // Опционально: запуск прямо из IDE без сборки jar (для быстрой отладки).
    // Для честных финальных цифр всё равно гоняй через target/benchmarks.jar.
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
