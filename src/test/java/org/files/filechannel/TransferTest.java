package org.files.filechannel;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TransferTest {

    private static final int MB_IN_BYTES = 1024 * 1024;
    private static final int[] numberOfOutputFiles = { 32, 64, 128 };
    private static final int[] numberOfThreads = {1, 4, 16, 32 };
    private static final int[] sizeOfInputFilesMb = { 50, 100, 200};

    private static final File TMP_DIR = new File("tmp");
    private static final File INPUT_DIR= new File(TMP_DIR, "input");
    private static final File OUTPUT_DIR = new File(TMP_DIR, "output");
    private static final List<File> inputFiles = new ArrayList<>();

    @BeforeClass
	public static void initInputFiles() throws IOException {
        createDirs();
        createInputFiles();
	}

    @AfterClass
    public static void cleanDirectories() throws IOException {
        FileUtils.deleteDirectory(TMP_DIR);
    }

    private static void createInputFiles() throws IOException {
        SecureRandom rnd = new SecureRandom();
        for (int size : sizeOfInputFilesMb){
            byte [] data = new byte[size * MB_IN_BYTES];
            rnd.nextBytes(data);
            File inputFile=new File(INPUT_DIR,  size + "MB.input");
            inputFiles.add(inputFile);
            FileUtils.writeByteArrayToFile(inputFile, data);
        }
    }


    private static void createDirs()throws IOException{
        FileUtils.forceMkdir(INPUT_DIR);
        FileUtils.forceMkdir(OUTPUT_DIR);
    }

	@Test
	public void nInputNOutput() throws IOException, URISyntaxException, InterruptedException {
		testGeneric("n_input_channel-n_output_channels", FileTransmitterDiffInChannel.class);
	}


    @Test
    public void oneInputNOutput() throws IOException, URISyntaxException, InterruptedException {
        testGeneric("one_input_channel-n_output_channels", FileTransmitterDiffInChannel.class);
    }

    private void testGeneric (String testName, Class<? extends  FileTransmitter> ftClass) throws IOException, URISyntaxException, InterruptedException {
        FileWriter fw = new FileWriter(testName + ".data");
        for (File file : inputFiles) {
            for (int nOut : numberOfOutputFiles) {
                File [] outputPaths = generateOutputPaths(file.getName(), nOut);
                for (int nTh : numberOfThreads) {
                    long startTime = System.currentTimeMillis();
                    if (FileTransmitterDiffInChannel.class.isAssignableFrom(ftClass)){
                        new FileTransmitterDiffInChannel(nTh, file, outputPaths);
                    }else{
                        new FileTransmitterOneInputMultiOutput(nTh, file, outputPaths);
                    }
                    fw.append(file.getAbsolutePath() + ',' + nOut + ',' + nTh + ',' + (System.currentTimeMillis() - startTime) + '\n');
                }
                assertFilesExits(outputPaths);
                //assertAreEquals(file, outputPaths);
            }
        }
        fw.close();
    }

    private void assertAreEquals(File input, File[] outputPaths) throws IOException {
        for (File f: outputPaths){
            assertTrue(input.toString() + " = " + f.toString(), FileUtils.contentEquals(f, input));
        }
    }

    private void assertFilesExits(File[] outputPaths) {
        for (File f: outputPaths){
            assertTrue(f.toString(), f.exists());
        }
    }

    private static File[] generateOutputPaths(String prefix, int e) {
        File[] outputPaths = new File[e];
		for (int i = 0; i < e; i++) {
			outputPaths[i] = new File (OUTPUT_DIR, prefix + "." + e + "." + i);
		}
		return outputPaths;
	}
}
