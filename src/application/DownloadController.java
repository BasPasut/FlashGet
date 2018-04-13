package application;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

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

	private DownloadTask worker;
	private File file;

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

	public void startWorker(ActionEvent event) {
		URL url = createURL();
		if (url == null) {
			alertBox();
		} else {
			downloadProgress.setStyle("-fx-accent: orange;");
			FileChooser fc = new FileChooser();
			fc.setInitialFileName(Paths.get(url.getPath()).getFileName().toString());
			fc.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
			if (worker == null || !worker.isRunning()) {
				file = fc.showSaveDialog(new Stage());
				if (file != null) {
					worker = new DownloadTask(url, file);
					// automatically update the progressBar using worker's
					// progress
					// Property
					downloadProgress.progressProperty().bind(worker.progressProperty());
					// update the displayField whenever the value of worker
					// changes:
					// add the observer (ChangeListener)
					filesize.textProperty().bind(worker.messageProperty());
					filename.setText(file.getName());
					new Thread(worker).start();
				}
			}
		}
	}

	public void stopWorker(ActionEvent event) {
		worker.cancel();
	}

	public void handleClear(ActionEvent event) {
		urlField.clear();
	}

	@FXML
	public void initialize() {
		download.setOnAction(this::startWorker);
		cancel.setOnAction(this::stopWorker);
	}

	public void alertBox() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Warning");
		alert.setHeaderText("Invalid URL");
		alert.setContentText("Cannot receive file from this URL. Please select another URL.");
		alert.showAndWait();
		urlField.clear();
	}
}
