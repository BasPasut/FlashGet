package application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Class that perform the download process which is read the file from the given
 * url and write it into the new file.
 * 
 * @author Pasut Kittiprapas
 *
 */
public class DownloadTask extends Task<Long> {

	private URL url;
	private File file;
	private Long start;
	private Long size;

	/**
	 * Initialize the valuables that will be use when you create the object.
	 * 
	 * @param url
	 * @param file
	 * @param start
	 * @param size
	 */
	public DownloadTask(URL url, File file, Long start, Long size) {
		this.url = url;
		this.file = file;
		this.start = start;
		this.size = size;
	}

	/**
	 * Read the file from the given start and stop position and write the data
	 * that its read to the new file.
	 */
	@Override
	protected Long call() throws Exception {
		long bytesRead = 0;
		RandomAccessFile writer;
		if (size == null) {
			updateMessage("Invalid");
			updateProgress(0, 100);
		} else {
			updateProgress(0, size);
			final int BUFFERSIZE = 16384;
			InputStream in = null;
			try {
				URLConnection connection = url.openConnection();
				String range = null;
				if (size > 0L) {
					range = String.format("bytes=%d-%d",
							new Object[] { Long.valueOf(start), Long.valueOf((start + size) - 1L) });
				} else {
					range = String.format("bytes=%d-", new Object[] { Long.valueOf(start) });
				}
				connection.setRequestProperty("Range", range);
				in = connection.getInputStream();
			} catch (IOException e) {
				alertBox();
			}
			writer = new RandomAccessFile(file, "rwd");
			writer.seek(start);
			byte[] buffer = new byte[BUFFERSIZE];
			try {
				do {
					int n = in.read(buffer);
					if (n < 0) {
						updateMessage("Complete");
						break;
					}
					// n < 0 means end of the input
					writer.write(buffer, 0, n);
					// write n bytes from buffer
					bytesRead += n;
					updateValue(bytesRead);
					updateMessage(Long.toString(bytesRead));
					updateProgress(bytesRead, size);
					Thread.sleep(1);
				} while (bytesRead < size);
			} catch (IOException ex) {
				alertBox();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				writer.close();
			}
		}
		return bytesRead;

	}

	/**
	 * Show alert information about the URL.
	 */
	private void alertBox() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Warning");
		alert.setHeaderText("Invalid URL");
		alert.setContentText("Cannot receive file from this URL. Please select another URL.");
		alert.showAndWait();
	}

}
