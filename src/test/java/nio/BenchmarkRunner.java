package nio;

import nio.task01.BufferCopyWriter;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;

public class BenchmarkRunner {

    @Test
    public void run() throws Exception {
        Options options = new OptionsBuilder()
                .include(PerByteCopyWriterTest.class.getSimpleName())
                .forks(1)
                .build();

        Collection<RunResult> results = new Runner(options).run();
        results.forEach(r -> System.out.println(r.getPrimaryResult()));
    }

    @Test
    public void runBuffer() throws Exception {
        Options options = new OptionsBuilder()
                .include(BufferCopyWriter.class.getSimpleName())
                .forks(1)
                .build();

        Collection<RunResult> results = new Runner(options).run();
        results.forEach(r -> System.out.println(r.getPrimaryResult()));
    }
}
