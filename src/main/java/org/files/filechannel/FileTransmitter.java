package org.files.filechannel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransmitter implements TransmitterListener {

	private final ExecutorService executor;
	private final Logger log;

	private final Set<String> pending;
	private final FileInputStream fis;
	private AtomicBoolean isFinished;

	public FileTransmitter(int numberOfThreads, String from, String... to) throws IOException {
		log = Logger.getLogger(getClass().getSimpleName() + " " + to.length);
		log.log(Level.INFO, "Starting");
		isFinished = new AtomicBoolean(false);
		pending = new HashSet<>(Arrays.asList(to));
		executor = Executors.newFixedThreadPool(numberOfThreads);
		fis = new FileInputStream(from);

		for (String t : to) {
			log.log(Level.FINE, "Enqueue " + t);
			executor.execute(new Transmitter(fis.getChannel(), t, this));
		}

	}

	@Override
	public void finished(String outPath) {
		synchronized (pending) {
			pending.remove(outPath);
			if (pending.isEmpty()) {
				isFinished.set(true);
				try {
					log.log(Level.FINE, "Stopping");
					fis.close();
					executor.shutdown();
					log.log(Level.INFO, "Stopped");
				} catch (IOException e) {
					log.log(Level.WARNING, e.toString());
				}
			}
		}
	}
	
	// 1, 11 , 181 : 3 threads
	// 1, 8 , 93 : 6 threads
	// 1, 9, 80: 12 threads
}
