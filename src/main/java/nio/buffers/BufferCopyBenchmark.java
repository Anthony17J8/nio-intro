package nio.buffers;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

/**
 * Задача 4 (Этап 1): сравнение heap vs direct ByteBuffer на копировании файла.
 * <p>
 * Что готово:  вся обвязка JMH (state, setup, teardown), создание файлов и буферов.
 * Что делаешь ТЫ: заполняешь метод copy(...) — тот самый ритм
 * "записал → flip() → прочитал → clear()", только для канала.
 * Это пропуск №1. Есть ещё пара мест, помеченных TODO.
 * <p>
 * Запуск:
 * 1) создать проект через JMH-архетип (см. ресурсы Этапа 1);
 * 2) положить этот класс в src/main/java/com/example/;
 * 3) mvn clean package
 * 4) java -jar target/benchmarks.jar BufferCopyBenchmark
 * <p>
 * Версия JMH на начало 2026 — линейка 1.37; точную смотри в сгенерированном pom.xml.
 */

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
public class BufferCopyBenchmark {

    // Размер буфера. Слишком маленький — разница утонет в шуме. 64 КБ — разумно.
    private static final int BUFFER_SIZE = 64 * 1024;

    // Размер исходного файла. Бери достаточно большой, чтобы копирование было заметным.
    private static final long FILE_SIZE = 55L * 1024 * 1024;   // 64 МБ

    private Path srcPath;
    private Path dstPath;

    private ByteBuffer heapBuffer;
    private ByteBuffer directBuffer;

    /**
     * Выполняется ОДИН раз до замеров (Level.Trial). НЕ входит в измеряемое время.
     * Готовим исходный файл и оба буфера.
     */
    @Setup(Level.Trial)
    public void setup() throws IOException {
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

        // ВОТ ОНА — разница, которую меряем: один буфер в куче, другой вне кучи.
        heapBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        directBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    /**
     * Выполняется один раз после всех замеров. Чистим временные файлы.
     */
    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        Files.deleteIfExists(srcPath);
        Files.deleteIfExists(dstPath);
    }

    // ====================================================================
    //  Два измеряемых метода. Логика идентична — отличается ТОЛЬКО буфер.
    //  Именно это и позволяет честно сравнить heap и direct.
    // ====================================================================

    @Benchmark
    public void copyWithHeapBuffer(Blackhole bh) throws IOException {
        long copied = copy(heapBuffer);
        bh.consume(copied);   // "потребляем" результат, иначе JIT может выкинуть весь код
    }

    @Benchmark
    public void copyWithDirectBuffer(Blackhole bh) throws IOException {
        long copied = copy(directBuffer);
        bh.consume(copied);
    }

    /**
     * Копирует srcPath → dstPath, прогоняя данные через переданный буфер.
     * Возвращает число скопированных байт.
     * <p>
     * ┌─────────────────────────── ПРОПУСК №1 (главный) ───────────────────────────┐
     * │  Здесь живёт ритм буфера с Этапа 1:                                         │
     * │    записал (канал read в буфер) → flip() → прочитал (канал write из буфера) │
     * │    → clear() (или compact()) → повторяем, пока src не кончится.             │
     * │                                                                            │
     * │  Подсказки:                                                                │
     * │   • src.read(buffer)  наполняет буфер из файла, возвращает число байт       │
     * │     или -1 в конце файла (помнишь -1 с Этапа 0?).                           │
     * │   • перед записью в dst буфер надо ПЕРЕКЛЮЧИТЬ на чтение — каким методом?    │
     * │   • dst.write(buffer) может записать НЕ ВСЁ за раз → пиши, пока             │
     * │     buffer.hasRemaining().                                                  │
     * │   • после того как буфер вычерпан — подготовь его к следующему read.        │
     * │   • буфер переиспользуется между итерациями: ОБЯЗАТЕЛЬНО сбрось его          │
     * │     в начале метода (buffer.clear()), иначе словишь мусор с прошлого раза.  │
     * └────────────────────────────────────────────────────────────────────────────┘
     */
    private long copy(ByteBuffer buffer) throws IOException {
        long total = 0;

        try (FileChannel src = FileChannel.open(srcPath, StandardOpenOption.READ);
             FileChannel dst = FileChannel.open(dstPath,
                     StandardOpenOption.WRITE, StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING)) {

            buffer.clear();   // на всякий случай: буфер пришёл из прошлой итерации

            int read = 0;
            while ((read = src.read(buffer)) != -1 /* прочитали что-то из src, не конец файла */) {
                // защита от редкого edge-case
                if (read == 0) continue;
                // переключить буфер на чтение
                buffer.flip();
                // пока в буфере есть данные — писать их в dst
                while (buffer.hasRemaining()) {
                    dst.write(buffer);
                }
                // подготовить буфер к следующему чтению
                buffer.clear();
                // не забыть увеличить total
                total += read;
            }

            // Удали заглушку ниже, когда напишешь цикл:
            // throw new UnsupportedOperationException("реализуй копирование (ПРОПУСК №1)");
        }

        return total;   // <- раскомментируй, когда уберёшь заглушку выше
    }

    // Опционально: запуск прямо из IDE без сборки jar (для быстрой отладки).
    // Для честных финальных цифр всё равно гоняй через target/benchmarks.jar.
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
