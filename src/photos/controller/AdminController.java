package photos.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.model.User;

import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.Optional;

/** Controller class that handles admin window
 * @author Brian Huang
 * @author Tyler Latawiec
 */

public class AdminController {

    @FXML ListView<User> userList;
    ReadWrite rw = new ReadWrite();

    /**
     * initialize method
     * @throws Exception
     */
    public void initialize() throws Exception{
        rw.readUsers();
        userList.getItems().addAll(ReadWrite.users);
        userList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        userList.getSelectionModel().selectLast();
    }

    /**
     * creates user
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void createUser() throws ClassNotFoundException, IOException{
        TextInputDialog createPrompt = new TextInputDialog();
        createPrompt.setTitle("Create User");
        createPrompt.setContentText("Enter a username:");
        Optional<String> username = createPrompt.showAndWait();
        if (username.isPresent() && !username.get().isEmpty()){
            for (User user : ReadWrite.users){
                if (user.toString().equalsIgnoreCase(username.get())){
                    Alert alreadyExists = new Alert(AlertType.ERROR, "User already exists", ButtonType.CLOSE);
                    alreadyExists.showAndWait();
                    return;
                }
            }
            User toAdd = new User(username.get());
            ReadWrite.users.add(toAdd);
            rw.writeUsers();
            userList.getItems().add(toAdd);
            userList.refresh();
            userList.getSelectionModel().select(toAdd);
            return;
        }
        else if(username.isPresent()) {
            Alert noNameEntered = new Alert(AlertType.ERROR, "No username entered", ButtonType.CLOSE);
            noNameEntered.showAndWait();
        }
    }

    /**
     * deletes user
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void deleteUser() throws IOException, ClassNotFoundException{
        User currentlySelected = userList.getSelectionModel().getSelectedItem();
        if (currentlySelected.toString().equalsIgnoreCase("stock")){
            Alert cannotDeleteStock = new Alert(AlertType.WARNING, "Cannot delete stock user", ButtonType.OK);
            cannotDeleteStock.showAndWait();
            return;
        }
        else if(currentlySelected.toString().equalsIgnoreCase("admin")){
            Alert cannotDeleteAdmin = new Alert(AlertType.WARNING, "Cannot delete admin", ButtonType.OK);
            cannotDeleteAdmin.showAndWait();
            return;
        }

        ReadWrite.users.remove(currentlySelected);
        rw.writeUsers();
        userList.getItems().setAll(ReadWrite.users);
        userList.refresh();
        userList.getSelectionModel().selectPrevious();
    }

    /**
     * logs out
     * @param e
     * @throws IOException
     */
    public void logout(ActionEvent e) throws IOException {
        Parent rootLogin = FXMLLoader.load(getClass().getResource("/photos/view/Login.fxml"));
        Scene sceneLogin = new Scene(rootLogin);

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(sceneLogin);
        stage.show();
    }
}
