package application;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DownloadController {

	@FXML
	private Button download;
	@FXML
	private Button clear;
	@FXML
	private Button cancel;
	@FXML
	private TextField urlField;
	@FXML
	private Label filename;
	@FXML
	private Label filesize;
	@FXML
	private ProgressBar downloadProgress;
	@FXML
	private ProgressBar thread1;
	@FXML
	private ProgressBar thread2;
	@FXML
	private ProgressBar thread3;
	@FXML
	private ProgressBar thread4;

	Task<Long> task1;
	Task<Long> task2;
	Task<Long> task3;
	Task<Long> task4;
	private File file;

	@FXML
	public void initialize() {
		download.setOnAction(this::startWorker);
		clear.setOnAction(this::handleClear);
		cancel.setOnAction(this::stopWorker);
	}

	public void startWorker(ActionEvent event) {
		URL url = createURL();
		if (url == null) {
			alertBox();
		} else {
			Long length = getFileSize(url) / 4;
			downloadProgress.setStyle("-fx-accent: orange;");
			FileChooser fc = setFileChooser(url);
			file = fc.showSaveDialog(new Stage());
			if (file != null) {
				ExecutorService executor = Executors.newFixedThreadPool(6);
				task1 = new DownloadTask(url, file, 0L, length);
				thread1.progressProperty().bind(task1.progressProperty());
				executor.execute(task1);

				task2 = new DownloadTask(url, file, length, length);
				thread2.progressProperty().bind(task2.progressProperty());
				executor.execute(task2);

				task3 = new DownloadTask(url, file, length * 2, length);
				thread3.progressProperty().bind(task3.progressProperty());
				executor.execute(task3);

				task4 = new DownloadTask(url, file, length * 3, length);
				thread4.progressProperty().bind(task4.progressProperty());
				executor.execute(task4);

				try {
					executor.shutdown();
					executor.awaitTermination(1, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				DoubleBinding compute = task1.progressProperty().add(task2.progressProperty()).add(task3.progressProperty()).add(task4.progressProperty());
				
				// worker = new DownloadTask(url, file,start,length);
				// downloadProgress.progressProperty().bind(worker.progressProperty());
				downloadProgress.progressProperty().bind(compute);

				filesize.textProperty().bind(task1.messageProperty());
				filename.setText(file.getName());
				// new Thread(worker).start();

			}
		}

	}

	public void stopWorker(ActionEvent event) {
		task1.cancel();
		task2.cancel();
		task3.cancel();
		task4.cancel();
	}

	public FileChooser setFileChooser(URL url) {
		FileChooser fc = new FileChooser();
		fc.setInitialFileName(Paths.get(url.getPath()).getFileName().toString());
		fc.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		return fc;
	}

	public void handleClear(ActionEvent event) {
		urlField.clear();
	}

	public URL createURL() {
		String urlname = urlField.getText();
		if (urlField.getText().isEmpty()) {
			return null;
		}
		URL url = null;
		try {
			url = new URL(urlname);
			return url;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public void alertBox() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Warning");
		alert.setHeaderText("Invalid URL");
		alert.setContentText("Cannot receive file from this URL. Please select another URL.");
		alert.showAndWait();
		urlField.clear();
	}

	public Long getFileSize(URL url) {
		long length = 0;
		URLConnection connection = null;
		try {
			connection = url.openConnection();
			length = connection.getContentLengthLong();
			if (length > 0) {
				return length;
			} else {
				return null;
			}
		} catch (MalformedURLException ex) {
			alertBox();
		} catch (IOException ioe) {
			alertBox();
		}
		return null;
	}
}
