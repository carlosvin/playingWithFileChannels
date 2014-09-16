package org.files.filechannel;

import java.io.File;
import java.io.FileInputStream;
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

public class FileTransmitterOneInputMultiOutput extends  FileTransmitter {

    private final FileInputStream fis;

    public FileTransmitterOneInputMultiOutput(int numberOfThreads, File from, File... to) throws IOException, URISyntaxException,
			InterruptedException {
        super(numberOfThreads, from, to);
        fis = new FileInputStream(from);
    }

    @Override
    protected Transmitter createTransmitter(File destination) {
        return new Transmitter(fis, destination, listener);
    }

    @Override
    protected void cleanup(){
        try {
            fis.close();
        } catch (IOException e) {
            log.log(Level.WARNING, e.toString());
        }
    }


}
