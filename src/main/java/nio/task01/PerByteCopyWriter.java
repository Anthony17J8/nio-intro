package nio.task01;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PerByteCopyWriter {

    public static void copy(String src, String dst) {
        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(dst)) {
            int n;
            while ((n = fis.read()) != -1) {
                fos.write(n);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка копирования файла", ex);
        }

    }
}
