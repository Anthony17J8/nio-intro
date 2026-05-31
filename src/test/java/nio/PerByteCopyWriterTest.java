package nio;

import nio.task01.PerByteCopyWriter;
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
public class PerByteCopyWriterTest {

    @Benchmark
    public void copy() {
        PerByteCopyWriter.copy(
            "/home/anthony/IdeaProjects/nio-intro/src/test/resources/huge.txt",
            "/home/anthony/IdeaProjects/nio-intro/src/test/resources/copy.txt"
        );
    }
}