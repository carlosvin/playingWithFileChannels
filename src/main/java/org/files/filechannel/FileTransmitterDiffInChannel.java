package org.files.filechannel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransmitterDiffInChannel extends FileTransmitter  {


	public FileTransmitterDiffInChannel(int numberOfThreads, File from, File... to) throws IOException,
			URISyntaxException, InterruptedException {
        super(numberOfThreads, from, to);
	}

    @Override
    protected Transmitter createTransmitter(File destination) throws FileNotFoundException {
        return new Transmitter(from, destination, listener);
    }

    @Override
    protected void cleanup()  {

    }

}
