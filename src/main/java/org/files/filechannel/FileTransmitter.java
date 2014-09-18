package org.files.filechannel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by carlos on 16/09/14.
 */
public abstract class FileTransmitter {

    protected final Logger log;
    protected final ExecutorService executor;

    protected final Set<File> pending;
    protected final File from;


    FileTransmitter(int numberOfThreads, File from, File... to) throws IOException, InterruptedException {
        log = Logger.getLogger(getClass().getSimpleName() + " " +numberOfThreads + "th " + to.length);
        log.log(Level.INFO, "Starting");
        pending = new HashSet<>(Arrays.asList(to));
        executor = Executors.newFixedThreadPool(numberOfThreads);
        this.from = from;

        for (File destination : to) {
            log.log(Level.FINE, "Enqueue " + destination);
            executor.execute(createTransmitter(destination));
        }

        synchronized (pending) {
            pending.wait();
        }

    }

    protected abstract Transmitter createTransmitter(File destination) throws FileNotFoundException;

    protected abstract void cleanup() ;

    protected final TransmitterListener listener = new TransmitterListener() {
        @Override
        public void finished(File outfile) {
            synchronized (pending) {
                pending.remove(outfile);
                if (pending.isEmpty()) {
                    log.log(Level.FINE, "Stopping");
                    executor.shutdown();
                    log.log(Level.INFO, "Stopped");
                    cleanup();
                    pending.notify();
                }
            }
        }
    };

}
