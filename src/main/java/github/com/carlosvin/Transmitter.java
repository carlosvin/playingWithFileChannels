package github.com.carlosvin;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Transmitter implements Runnable, Closeable {

	private final Logger log;
	private final Path target;
	private final TransmitterListener listener;
	private final FileChannel sourceChannel;

	public Transmitter(Path source, Path target, TransmitterListener listener) throws IOException {
		this(FileChannel.open(source), target, listener);
	}

	public Transmitter(FileChannel sourceChannel, Path target, TransmitterListener listener) {
		this.log = Logger.getLogger(target.toString());
		this.target = target;
		this.listener = listener;
		this.sourceChannel = sourceChannel;
	}

	@Override
	public void run() {
		try (WritableByteChannel targetChannel = Files.newByteChannel(target, StandardOpenOption.CREATE,
				StandardOpenOption.APPEND)) {
			log.log(Level.FINE, "Transmitting");
			sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
			log.log(Level.FINE, "Transmitted");
			listener.finished(target);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		log.log(Level.INFO, "Closing ");
		this.sourceChannel.close();
	}
}