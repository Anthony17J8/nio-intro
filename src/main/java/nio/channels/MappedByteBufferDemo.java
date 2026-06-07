package nio.channels;

import java.io.IOException;
import java.nio.file.Path;

public class MappedByteBufferDemo {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Incorrect number of args: " + args.length + ". You need to set path to file with data.");
        }

        // Count lines using MappedByteBuffer
        MappedByteBufferCounter mappCounter = new MappedByteBufferCounter(Path.of(args[0]));
        System.out.println("Lines in file(mapp): " + mappCounter.countLines());
        // Count lines with random access to bytes
        System.out.println("Lines in file(mappRandomAccess): " + mappCounter.countLinesRandomAccess());

        // Count lines using ByteBuffer
        ByteBufferCounter byteBufCounter = new ByteBufferCounter(Path.of(args[0]));
        System.out.println("Lines in file(byteBuf): " + mappCounter.countLines());

        // Create byte frequency in text file
        createHistogram(args);

        // Compare ByteBuffer to MappedByteBuffer
        benchmark(mappCounter, byteBufCounter);
    }

    private static void createHistogram(String[] args) {
        MappedByteBufferHistogram mbbh = new MappedByteBufferHistogram(Path.of(args[0]));
        long[] histogram = mbbh.createHistogram();
        printTopNChars(histogram, 5);
    }

    private static void countLines(String[] args) {
        MappedByteBufferCounter counter = new MappedByteBufferCounter(Path.of(args[0]));
        System.out.println("Lines in file: " + counter.countLines());
    }

    private static void printTopNChars(long[] hist, int topN) {
        System.out.println("Топ-" + topN + " most freq bytes:");
        for (int rank = 0; rank < topN; rank++) {
            int maxIdx = -1;
            long maxVal = -1;
            for (int i = 0; i < 256; i++) {
                if (hist[i] > maxVal) {
                    maxVal = hist[i];
                    maxIdx = i;
                }
            }
            if (maxIdx < 0 || maxVal == 0) break;
            System.out.printf("  0x%02X %-8s — %d times %n",
                    maxIdx, describe(maxIdx), maxVal);
            hist[maxIdx] = -1;   // вычёркиваем, чтобы найти следующий по убыванию
        }
    }

    private static String describe(int b) {
        if (b == '\n') return "(\\n)";
        if (b == '\r') return "(\\r)";
        if (b == '\t') return "(\\t)";
        if (b == ' ') return "(space)";
        if (b >= 33 && b <= 126) return "('" + (char) b + "')";
        return "";
    }

    static void benchmark(MappedByteBufferCounter mappCounter,
                          ByteBufferCounter bbCounter) throws IOException {
        // прогрев: загоняем файл в page cache ОС, чтобы холодный/горячий прогон
        // не искажали сравнение (иначе первый способ читает с диска, второй из кэша)
        mappCounter.countLines();
        bbCounter.countLines();

        long t1 = System.nanoTime();
        mappCounter.countLines();
        long mmapMs = (System.nanoTime() - t1) / 1_000_000;

        long t2 = System.nanoTime();
        bbCounter.countLines();
        long readMs = (System.nanoTime() - t2) / 1_000_000;

        System.out.printf("%nЗамер (после прогрева):%n");
        System.out.printf("  mmap: %d мс%n", mmapMs);
        System.out.printf("  read: %d мс%n", readMs);
        System.out.println("  Примечание: на простом последовательном проходе разница");
        System.out.println("  обычно скромная. mmap раскрывается на случайном доступе и");
        System.out.println("  многократном чтении, а не на одном проходе сверху вниз.");
    }
}
