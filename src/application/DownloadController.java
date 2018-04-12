package application;

import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

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
		URL url = null;
		try {
			url = new URL(urlname);
			return url;
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	
	public void startWorker(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(file);
		if (worker == null || !worker.isRunning()) {
			file = fc.showSaveDialog(new Stage());
			worker = new DownloadTask(createURL(),file);
			// automatically update the progressBar using worker's progress
			// Property
			downloadProgress.progressProperty().bind(worker.progressProperty());
			// update the displayField whenever the value of worker changes:
			// add the observer (ChangeListener)
			filesize.textProperty().bind(worker.messageProperty());
			filename.setText(fc.getTitle());
			new Thread(worker).start();
		}
	}
	
	public void stopWorker(ActionEvent event){
		worker.cancel();
	}
	
	public void handleClear(ActionEvent event){
		urlField.clear();
	}
	
	@FXML
	public void initialize(){
		download.setOnAction(this::startWorker);
		cancel.setOnAction(this::stopWorker);
	}
}
