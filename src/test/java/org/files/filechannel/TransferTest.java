package org.files.filechannel;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TransferTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFileTransmitter4Th10Elements() throws IOException {
		new FileTransmitter(4, "/home/carlos/visits.sqlite.db", generateOutputPaths(10));
	}
	@Test
	public void testFileTransmitter4Th100Elements() throws IOException {
		new FileTransmitter(4, "/home/carlos/visits.sqlite.db", generateOutputPaths(100));
	}
	@Test
	public void testFileTransmitter4Th1000Elements() throws IOException {
		new FileTransmitter(4, "/home/carlos/visits.sqlite.db", generateOutputPaths( 1000));
	}
	
	private static String[] generateOutputPaths( int e) {
		String[] outputPaths = new String[e];
		for (int i = 0; i < e; i++) {
			outputPaths[i] = "tmp/" + e + '.' +i;
		}
		return outputPaths;
	}
}
