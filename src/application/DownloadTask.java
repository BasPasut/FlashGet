package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DownloadTask extends Task<Long> {

	private URL url;
	private File file;

	public DownloadTask(URL url, File file) {
		this.url = url;
		this.file = file;
	}

	@Override
	protected Long call() throws Exception {
		Long length = getFileSize();
		long bytesRead = 0;
		if (length <= 0) {

		} else {
			updateProgress(0, length);
			final int BUFFERSIZE = 16 * 1024;
			InputStream in = null;
			OutputStream out = null;
			try {
				URLConnection connection = url.openConnection();
				in = connection.getInputStream();
				out = getOutputStream(file);
			} catch (IOException e) {
				alertBox();
			}
			byte[] buffer = new byte[BUFFERSIZE];
			try {
				do {
					int n = in.read(buffer);
					updateValue(bytesRead);
					updateMessage(Long.toString(bytesRead) + "/" + length);
					updateProgress(bytesRead, length);
					if (n < 0){
						updateMessage("Complete");
						break;
					}
					// n < 0 means end of the input
					out.write(buffer, 0, n);
					// write n bytes from buffer
					bytesRead += n;

					try {
						Thread.sleep(1);
					} catch (Exception e) {
						break;
					}
				} while (true);
			} catch (IOException ex) {
				alertBox();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bytesRead;
	}

	public FileOutputStream getOutputStream(File file) {
		FileOutputStream dFile = null;
		try {
			dFile = new FileOutputStream(file);
			return dFile;
		} catch (FileNotFoundException e) {
			alertBox();
		}
		return null;
	}

	public Long getFileSize() {
		long length = 0;
		URLConnection connection = null;
		try {
			connection = url.openConnection();
			length = connection.getContentLengthLong();
			return length;
		} catch (MalformedURLException ex) {
			alertBox();
		} catch (IOException ioe) {
			alertBox();
		}
		return null;
	}

	private void alertBox() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Warning");
		alert.setHeaderText("Invalid URL");
		alert.setContentText("Cannot receive file from this URL. Please select another URL.");
		alert.showAndWait();
	}
}
