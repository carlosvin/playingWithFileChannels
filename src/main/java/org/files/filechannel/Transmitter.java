package org.files.filechannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Transmitter implements Runnable {

	private final Logger log;

	private final FileChannel sourceChannel;
	private final File destination;
	private final TransmitterListener listener;

	private final FileInputStream fis;

	public Transmitter(FileInputStream fis, File destination, TransmitterListener listener) {
		this.sourceChannel = fis.getChannel();
		this.destination = destination;
		this.log = Logger.getLogger(destination.toString());
		this.listener = listener;
		this.fis = null;
	}

	public Transmitter(File source, File destination, TransmitterListener listener) throws FileNotFoundException {
		this.fis = new FileInputStream(source);
		this.sourceChannel = this.fis.getChannel();
		this.destination = destination;
		this.log = Logger.getLogger(destination.toString());
		this.listener = listener;
	}

	@Override
	public void run() {
		synchronized (sourceChannel) {
			try (FileOutputStream outStream = new FileOutputStream(destination)) {
				log.log(Level.FINE, "Transmitting");
				sourceChannel.transferTo(0, sourceChannel.size(), outStream.getChannel());
				log.log(Level.FINE, "Transmitted");
				if (fis != null) {
					fis.close();
				}
				listener.finished(destination);
			} catch (IOException e) {
				log.log(Level.WARNING, e.toString());
			}
		}
	}
}