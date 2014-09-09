package org.files.filechannel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Transmitter implements Runnable {

	private final Logger log;

	private final FileChannel sourceChannel;
	private final String outPath;
	private final TransmitterListener listener;

	public Transmitter(FileChannel sourceChannel, String outPath, TransmitterListener listener) {
		this.sourceChannel = sourceChannel;
		this.outPath = outPath;
		this.log = Logger.getLogger(outPath);
		this.listener = listener;
	}

	@Override
	public void run() {
		synchronized (sourceChannel) {
			try (FileOutputStream outStream = new FileOutputStream(outPath)) {
				log.log(Level.FINE, "Transmitting");
				sourceChannel.transferTo(0, sourceChannel.size(), outStream.getChannel());
				log.log(Level.FINE, "Transmitted");
				listener.finished(outPath);
			} catch (IOException e) {
				log.log(Level.WARNING, e.toString());
			}
		}
	}
}