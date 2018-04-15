package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

/**
 * Class that launching the UI of program
 * @author Pasut Kittiprapas
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = (Parent) FXMLLoader.load(getClass().getResource("FlashgetUI.fxml"));
			Scene scene = new Scene(root);
			setUserAgentStylesheet(STYLESHEET_CASPIAN);
			primaryStage.getIcons().add(new Image("file:icon.png"));
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.show();
			primaryStage.setTitle("Flashget");
		} catch(Exception e) {
			System.out.println("Exception creating scene: "+e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}