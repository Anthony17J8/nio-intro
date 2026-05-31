package nio;

import nio.task01.BufferCopyWriter;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class BufferCopyWriterTest {

    @Benchmark
    public void copy() {
        BufferCopyWriter.copy(
            "huge.txt",
            "copy.txt"
        );
    }

    @Test
    public void test() {
        BufferCopyWriter.copy(
                "huge.txt",
                "copy.txt"
        );
    }
}