package nio.channels;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileOpenOptionsDemo {

    public static void main(String[] args) {
        Path filePath = Path.of(args[0]);

//        appendOption(filePath);
//        truncateOption(filePath);
        createNewOption(filePath);
    }

    /**
     * Option CREATE_NEW protects files from rewriting. Throws exception if file exists
     */
    private static void createNewOption(Path filePath) {
        try (FileChannel fc = FileChannel.open(filePath,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE)) {
            fc.write(StandardCharsets.UTF_8.encode("Первая строка\n"));
        } catch (FileAlreadyExistsException exc) {
            System.out.println("Файл уже существует. Перезапись невозможна");
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * APPEND adds new data to the tail of file
     */
    private static void appendOption(Path filePath) {
        try (FileChannel fc = FileChannel.open(filePath, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            fc.write(StandardCharsets.UTF_8.encode("Первая строка\n"));
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }

        try (FileChannel fc = FileChannel.open(filePath, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            fc.write(StandardCharsets.UTF_8.encode("Вторая строка\n"));
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * TRUNCATE_EXISTING deletes all data before write
     */
    private static void truncateOption(Path filePath) {
        try (FileChannel fc = FileChannel.open(filePath, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE)) {
            fc.write(StandardCharsets.UTF_8.encode("Очень длинная строка\n"));
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }

        try (FileChannel fc = FileChannel.open(filePath,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            fc.write(StandardCharsets.UTF_8.encode("Короткая\n"));
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }
}
