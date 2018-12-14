package photos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.util.ArrayList;

import javafx.scene.layout.VBox;
import photos.model.*;
import javafx.scene.Node;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**Class for selecting album to move
 * @author Brian Huang
 * @author Tyler Latawiec
 */
public class SelectAlbum{

    static int index;

    @FXML ListView<Album> albumList;

    private static ArrayList<Album> albums = new ArrayList<Album>();

    /**
     * displays selection menu
     * @param _albums
     * @return
     */
    public static int display(ArrayList<Album> _albums){
        albums = _albums;

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Select Destination Album");
        window.setMinWidth(300);
        window.setMaxHeight(350);
        ListView<Album> list = new ListView<Album>();
        albums = ReadWrite.getCurrentUser().getAlbums();

        list.getItems().setAll(albums);
        list.refresh();
        list.getSelectionModel().selectFirst();

        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");

        confirmButton.setOnAction(e -> {
            index = list.getSelectionModel().getSelectedIndex();
            window.close();
        });
        cancelButton.setOnAction(e -> {
            index = -1;
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(list, confirmButton, cancelButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();


        return index;
    }

    /**
     * init method
     */
    public void initialize(){
        albums = ReadWrite.getCurrentUser().getAlbums();
        albumList.getItems().setAll(albums);
        albumList.refresh();
        albumList.getSelectionModel().selectFirst();

    }

    /**
     * set index of selected album
     * @param i
     */
    public void setIndexSelected(int i){
        index = i;
    }

    /**
     * confirm method for action
     * @param e
     */
    public void confirm(ActionEvent e){
        index = albumList.getSelectionModel().getSelectedIndex();
        
        closeStage(e);
    }

    /**
     * closes stage
     * @param event
     */
    private void closeStage(ActionEvent event) {
        Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

}