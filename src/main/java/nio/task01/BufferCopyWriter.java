package nio.task01;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BufferCopyWriter {
    private static final int BUFFER_SIZE = 8192;

    public static void copy(String src, String dst) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dst))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, n);
            }

        } catch (IOException ex) {
            throw new RuntimeException("Ошибка копирования файла ", ex);
        }
    }
}
