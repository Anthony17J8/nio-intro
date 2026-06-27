package nio.stage4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileSizeSummator {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Command line parameter must be the only one and has folder path");
            System.exit(1);
        }

        String pathS = args[0];
        try (Stream<Path> paths = Files.walk(Path.of(pathS))) {
            long totalSize = paths.filter(
                    Files::isRegularFile
            ).mapToLong(p -> {
                System.out.printf("Size for %s...\n", p);
                try {
                    return Files.size(p);
                } catch (IOException exc) {
                    System.out.println("Error during size calculation of " + p);
                    System.out.println("Error: " + exc);
                    return 0L;
                }
            }).sum();

            System.out.printf("Total size of folder %s - %d%n\n", pathS, totalSize);

        } catch (IOException exc) {
            System.out.println("Ошибка при определении");
        }
    }
}
