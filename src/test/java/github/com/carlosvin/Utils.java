package github.com.carlosvin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

public class Utils {
    public static final int KILOBYTES = 1024;
    public static final int MEGABYTES = KILOBYTES * 1024;
    private static final SecureRandom RND = new SecureRandom();

    public static Path createInputFile(int sizeMegaB, Path dir) throws IOException {
        int bytes = sizeMegaB * MEGABYTES;
        Path file = Files.createTempFile(dir, sizeMegaB + "MB-", ".txt");
        byte [] data = new byte[bytes];
        RND.nextBytes(data);
        return Files.write(file, data);
    }
}
