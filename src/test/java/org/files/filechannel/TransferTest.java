package org.files.filechannel;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransferTest {

	@BeforeClass
	public static void createDir(){
		new File("tmp").mkdir();
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFileTransmitter4Th10Elements() throws IOException, URISyntaxException {
		new FileTransmitter(4, "input.txt", generateOutputPaths(10));
	}
	@Test
	public void testFileTransmitter4Th100Elements() throws IOException, URISyntaxException {
		new FileTransmitter(4, "input.txt", generateOutputPaths(100));
	}
	@Test
	public void testFileTransmitter4Th1000Elements() throws IOException, URISyntaxException {
		new FileTransmitter(4, "input.txt", generateOutputPaths( 1000));
	}
	
	private static String[] generateOutputPaths( int e) {
		String[] outputPaths = new String[e];
		for (int i = 0; i < e; i++) {
			outputPaths[i] = "tmp/" + e + '.' +i;
		}
		return outputPaths;
	}
}
