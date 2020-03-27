package github.com.carlosvin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("File transfer test with NIO channels")
public class TransferTest {

    static Path outputDir;
    static BufferedWriter fwSame;
    static BufferedWriter fwDiff;
    static final Map<Integer,Path> inputFiles = new HashMap<>();
    static int[] inputFileSizes = {3, 16, 32};
    static int[] numberOfOutputFiles = {1, 32, 128};
    static int[] numberOfThreads = {1, 8, 32};

    @BeforeAll
    public static void init() throws IOException {
        Path dir = Files.createTempDirectory("inputFiles");

        for (int size : inputFileSizes) {
            inputFiles.put(size, Utils.createInputFile(size, dir));
        }
		outputDir = Files.createDirectories(Paths.get("output"));

        fwSame = Files.newBufferedWriter(outputDir.resolve("same_input_channel.csv"));
        fwDiff = Files.newBufferedWriter(outputDir.resolve("diff_input_channel.csv"));
    }

    @AfterAll
    public static void tearDown() throws IOException {
        fwSame.close();
        fwDiff.close();
    }

    private static Stream<Arguments> argumentsProvider() {
        List<Arguments> args = new LinkedList<Arguments>();
        for (Map.Entry<Integer, Path> inputFile : inputFiles.entrySet()) {
            for (int nOutputs : numberOfOutputFiles) {
                for (int nThreads : numberOfThreads) {
                    args.add(Arguments.of(inputFile.getKey(), inputFile.getValue(), nOutputs, nThreads));
                }
            }
        }
        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("argumentsProvider")
    public void testSameInputChannel(int inputSize, Path inputFile, int nOutputs, int nThreads)
            throws IOException, URISyntaxException, InterruptedException {
        long startTime = System.currentTimeMillis();
        new FileTransmitterSameChannel(nThreads, inputFile, generateOutputPaths("same.", nOutputs)).transfer();
        fwSame.append(str(inputSize, nOutputs, nThreads, startTime));
    }

    @ParameterizedTest
    @MethodSource("argumentsProvider")
    public void testDiffInputChannel(int inputSize, Path inputFile, int nOutputs, int nThreads)
            throws IOException, URISyntaxException, InterruptedException {
        long startTime = System.currentTimeMillis();
        new FileTransmitterDiffInChannel(nThreads, inputFile, generateOutputPaths("diff.", nOutputs)).transfer();
        fwDiff.append(str(inputSize, nOutputs, nThreads, startTime));
    }

    private static String str(int fileSize, int nOutputs, int nThreads, long startTime) {
        return fileSize + "," + nOutputs + ',' + nThreads + ',' + (System.currentTimeMillis() - startTime) + '\n';
    }

    private static Path[] generateOutputPaths(String pref, int e) throws IOException {
        Path[] outputPaths = new Path[e];
        for (int i = 0; i < e; i++) {
            outputPaths[i] = Files.createTempFile(outputDir, pref + "-" + e + "-" + i, ".txt");
        }
        return outputPaths;
    }
}
