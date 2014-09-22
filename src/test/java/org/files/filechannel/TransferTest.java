package org.files.filechannel;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TransferTest {

    private static final int MB_IN_BYTES = 1024 * 1024;
    private static final int[] numberOfOutputFiles = { 32, 64, 128 };
    private static final int[] numberOfThreads = {1, 8, 32 };
    private static final int[] sizeOfInputFilesMb = { 5, 20, 100};

    private static final File TMP_DIR = new File("tmp");
    private static final File INPUT_DIR= new File(TMP_DIR, "input");
    private static final File OUTPUT_DIR = new File(TMP_DIR, "output");
    private static final Map<Integer, File> inputFiles = new HashMap<>();

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
            int bytesSize = size * MB_IN_BYTES;
            byte [] data = new byte[bytesSize];
            for (int i=0; i<bytesSize; i+=rnd.nextInt(MB_IN_BYTES)){
                data[i] = (byte)rnd.nextInt(256);
            }
            File inputFile=new File(INPUT_DIR,  size + "MB.input");
            inputFiles.put(size, inputFile);
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
        try(FileWriter fw = new FileWriter(testName + ".data")){
        for (Map.Entry<Integer, File> fileEntry : inputFiles.entrySet()) {
            File file = fileEntry.getValue();
            for (int nOut : numberOfOutputFiles) {
                File [] outputPaths = generateOutputPaths(testName, file.getName(), nOut);
                for (int nTh : numberOfThreads) {
                    long startTime = System.currentTimeMillis();
                    if (FileTransmitterDiffInChannel.class.isAssignableFrom(ftClass)){
                        new FileTransmitterDiffInChannel(nTh, file, outputPaths);
                    }else{
                        new FileTransmitterOneInputMultiOutput(nTh, file, outputPaths);
                    }
                    fw.append(fileEntry.getKey().toString() + ',' + nOut + ',' + nTh + ',' + (System.currentTimeMillis() - startTime) + '\n');
                }
                assertFilesExits(outputPaths);
                //assertAreEquals(file, outputPaths);
            }
        }}catch(IOException e){
            e.printStackTrace();
        }
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

    private static File[] generateOutputPaths(String testName, String prefix, int e) throws IOException {
        File[] outputPaths = new File[e];
        File thisOutputDir = new File(OUTPUT_DIR, testName);
        FileUtils.forceMkdir(thisOutputDir);
		for (int i = 0; i < e; i++) {
			outputPaths[i] = new File (thisOutputDir, prefix + "." + e + "." + i);
		}
		return outputPaths;
	}
}
