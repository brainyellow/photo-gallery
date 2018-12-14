package photos.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.Photo;
import photos.model.User;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;


/** Class for handling login window
 * @author Brian Huang
 * @author Tyler Latawiec
 */

public class LoginController{
	@FXML
    private TextField usernameField;

    @FXML
    private Label photosText;

    ReadWrite rw = new ReadWrite();


    /**
     * Initialization method
     */
    public void initialize() throws IOException{

        try{
            rw.readUsers();
        }
        catch(FileNotFoundException fnfx) {
            // init file
            ReadWrite.users.add(new User("stock"));
            ReadWrite.users.get(0).getAlbums().add(new Album("stock"));
            String stockPath = "src/stockPhotos";
            File stockFolder = new File(stockPath);
            File[] stockList = stockFolder.listFiles();
            if (stockList == null){
                Alert deletedStock = new Alert(AlertType.WARNING, "Why did you delete the stock photos!", ButtonType.CLOSE);
                deletedStock.showAndWait();
                return;
            }
            for (File photo : stockList){
                ReadWrite.users.get(0).getAlbums().get(0).addPhoto(new Photo(photo));
            }
            ReadWrite.users.add(new User("admin"));
            rw.writeUsers();
        }
        catch(ClassNotFoundException cnfx){
            cnfx.printStackTrace();
        }

        FadeTransition trans = new FadeTransition(Duration.seconds(1.7), photosText);
        trans.setFromValue(0.0);
        trans.setToValue(1.0);
        trans.play();

        TranslateTransition trans2 = new TranslateTransition(Duration.seconds(2), photosText);
        trans2.setFromX(100);
        trans2.setToX(0);
        trans2.play();

	}

    /**
     * Checks if username corresponds to user, changes to album view if login is successful
     * @param e onAction event for "Login" button
     * @throws IOException
     */
	public void login(ActionEvent e) throws Exception{
        String userToLogin = usernameField.getText();
        if (userToLogin.isEmpty()) {
            Alert noInput = new Alert(AlertType.ERROR, "Please enter a username", ButtonType.OK);
            noInput.showAndWait();
            return;
        }
        if (userToLogin.equalsIgnoreCase("admin")){
            Parent rootAdmin = FXMLLoader.load(getClass().getResource("/photos/view/AdminView.fxml"));
            Scene sceneAdmin = new Scene(rootAdmin);

            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(sceneAdmin);
            stage.show();
            return;
        }
        for (User user : ReadWrite.users){
            if (user.toString().equalsIgnoreCase(userToLogin)) {
                ReadWrite.setCurrentUser(user);
                Parent rootAlbum = FXMLLoader.load(getClass().getResource("/photos/view/AlbumView.fxml"));
                Scene sceneAlbum = new Scene(rootAlbum);

                Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.setScene(sceneAlbum);
                stage.show();
                return;
            }
        }
        Alert loginFailure = new Alert(AlertType.INFORMATION, "User does not exist", ButtonType.CLOSE);
        loginFailure.showAndWait();
	}

    /**
     * Checks if username is available when trying to create a user
     * @throws Exception
     */
	public void createUser() throws Exception{
        String userToAdd = usernameField.getText();
        if (userToAdd.isEmpty()) {
            Alert noInput = new Alert(AlertType.ERROR, "Please enter a username", ButtonType.OK);
            noInput.showAndWait();
            return;
        }
	    for (User user : ReadWrite.users){
	        if (user.toString().equalsIgnoreCase(userToAdd)) {
                Alert creationError = new Alert(AlertType.ERROR, "User already exists", ButtonType.CLOSE);
                creationError.showAndWait();
                return;
            }
	    }
        ReadWrite.users.add(new User(userToAdd));
        rw.writeUsers();
        Alert creationSuccess = new Alert(AlertType.INFORMATION, "User created", ButtonType.CLOSE);
        creationSuccess.showAndWait();
        }
}
	

