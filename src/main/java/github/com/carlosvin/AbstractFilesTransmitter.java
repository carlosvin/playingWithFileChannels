package github.com.carlosvin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractFilesTransmitter implements TransmitterListener {

	protected final Logger log;
	protected final Path from;

	private final ExecutorService executor;
	private final Set<Path> pending;
	private final AtomicBoolean isFinished;
	private final Path[] to;

	public AbstractFilesTransmitter(int numberOfThreads, Path from, Path... to) throws IOException,
			URISyntaxException, InterruptedException {
		this.log = Logger.getLogger(getClass().getSimpleName() + " " + to.length);
		this.log.log(Level.INFO, "Starting");
		this.isFinished = new AtomicBoolean(false);
		this.pending = new HashSet<>(Arrays.asList(to));
		this.executor = Executors.newFixedThreadPool(numberOfThreads);
		this.from = from;
		this.to = to;
	}
	
	public abstract Transmitter createTransmiter(Path target) throws IOException;
	
	public void transfer() throws InterruptedException, IOException {
		for (Path t : to) {
			log.log(Level.FINE, "Enqueue " + t);
			executor.execute(createTransmiter(t));
		}

		synchronized (isFinished) {
			isFinished.wait();
		}
	}

	@Override
	public void finished(Path outPath) {
		synchronized (pending) {
			pending.remove(outPath);
			if (pending.isEmpty()) {
				isFinished.set(true);
				log.log(Level.FINE, "Stopping");
				executor.shutdown();
				log.log(Level.INFO, "Stopped");
			}
		}

		synchronized (isFinished) {
			isFinished.notify();
		}
	}
}
