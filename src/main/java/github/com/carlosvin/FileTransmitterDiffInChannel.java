package github.com.carlosvin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class FileTransmitterDiffInChannel extends AbstractFilesTransmitter {
	

	public FileTransmitterDiffInChannel(int numberOfThreads, Path from, Path... to)
			throws IOException, URISyntaxException, InterruptedException {
		super(numberOfThreads, from, to);
	}

	@Override
	public Transmitter createTransmiter(Path target) throws IOException {
		return new Transmitter(from, target, this);
	}
}
