package github.com.carlosvin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class FileTransmitterSameChannel  extends AbstractFilesTransmitter {

	private final FileChannel sourceChannel;

	public FileTransmitterSameChannel(int numberOfThreads, Path from, Path... to)
			throws IOException, URISyntaxException, InterruptedException {
		super(numberOfThreads, from, to);
		sourceChannel = FileChannel.open(from);
	}

	@Override
	public Transmitter createTransmiter(Path target) {
		return new Transmitter(sourceChannel, target, this);
	}
}
