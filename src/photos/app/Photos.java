package photos.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** Main class to start application
 * @author Brian Huang
 * @author Tyler Latawiec
 */

public class Photos extends Application{

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent rootLogin = FXMLLoader.load(getClass().getResource("/photos/view/Login.fxml"));
			Scene sceneLogin = new Scene(rootLogin);
			
			primaryStage.setScene(sceneLogin);
			primaryStage.setTitle("Photo Library");
			primaryStage.initStyle(StageStyle.DECORATED);
			
			primaryStage.show();
			
		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
