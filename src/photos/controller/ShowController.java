package photos.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Class for handling photo viewer window
 * @author Brian Huang
 * @author Tyler Latawiec
 */
public class ShowController {
    /**
     * init method
     */
    public void initialize(){}

    /**
     * logout method
     * @param e
     * @throws IOException
     */
    public void logout(ActionEvent e) throws IOException{
        Parent rootLogin = FXMLLoader.load(getClass().getResource("/photos/view/Login.fxml"));
        Scene sceneLogin = new Scene(rootLogin);

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(sceneLogin);
        stage.show();
    }
}
