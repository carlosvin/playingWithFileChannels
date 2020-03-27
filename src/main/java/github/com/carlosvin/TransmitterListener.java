package github.com.carlosvin;

import java.nio.file.Path;

public interface TransmitterListener {

	public void finished(Path outPath);
}
