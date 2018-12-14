package photos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.Photo;
import photos.model.User;
import photos.model.Tag;
import javafx.stage.Modality;

/** Class for handling album window
 * @author Brian Huang
 * @author Tyler Latawiec
 */

public class AlbumController{

	@FXML ListView<Album> albumList;

    @FXML ScrollPane imgGallery;

    @FXML ImageView bigView;
	
	@FXML Label labelCaption;
	@FXML Label labelDate;
	@FXML Label labelTags;
	@FXML Label labelNumPhotos;
	@FXML Label labelDateRange;

	@FXML TextField searchText;

    ReadWrite rw = new ReadWrite();
	List<ImageView> photoViewList = new ArrayList<>();
	TilePane tile = new TilePane();
	int currentImageIndex;	
	Album currentAlbum;
	

	/**
	 * Initialization method
	 */
    public void initialize() {
        imgGallery.setOnMouseClicked(e -> {
            if (e.getTarget().getClass() != bigView.getClass())
                return;
			ImageView temp = (ImageView) e.getTarget();
			currentImageIndex = photoViewList.indexOf(temp);
			bigView.setImage(temp.getImage());
			refreshPhotoInfo();
			
        });
        
        tile.setPadding(new Insets(15, 15, 15, 15));
        tile.setHgap(15);
        imgGallery.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Horizontal
        imgGallery.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
        imgGallery.setFitToWidth(true);

        albumList.getItems().addAll(ReadWrite.getCurrentUser().getAlbums());
		albumList.refresh();
        albumList.getSelectionModel().selectedItemProperty().addListener((albumList, oldSelection, newSelection) -> {
            photoViewList.clear();
            bigView.setImage(null);
            if (newSelection != null && newSelection.getAlbum() != null) {
				currentAlbum = newSelection;
                for (Photo photo : newSelection.getAlbum()){
                    try {
						addToGallery(photo);
                    }
                    catch(FileNotFoundException fnfx){
                        fnfx.printStackTrace();
                    }
                }
			}
			tile.getChildren().setAll(photoViewList);
			imgGallery.setContent(tile);
			if (newSelection != null && !newSelection.getAlbum().isEmpty()) {
				currentImageIndex = 0;
				bigView.setImage(photoViewList.get(currentImageIndex).getImage());
				refreshPhotoInfo();
			}
			refreshPhotoInfo();
        });
        albumList.getSelectionModel().selectFirst();

    }

	/**
	 * creates album
	 * @throws Exception
	 */
	public void createAlbum() throws Exception{
		TextInputDialog getAlbumName = new TextInputDialog();
		getAlbumName.setTitle("Create Album");
		getAlbumName.setContentText("Enter a name for your album: ");
		Optional<String> albumName = getAlbumName.showAndWait();
		if (albumName.isPresent() && !albumName.get().isEmpty()){
			Album albumToAdd = new Album(albumName.get());
			boolean albumAdded = ReadWrite.getCurrentUser().addAlbum(albumToAdd);
			if(albumAdded) {
				albumList.getItems().setAll(ReadWrite.getCurrentUser().getAlbums());
				albumList.refresh();
				albumList.getSelectionModel().select(albumToAdd);
				rw.writeUsers();
			}
			else{
				Alert albumExists = new Alert(Alert.AlertType.ERROR, "Album already exists", ButtonType.CLOSE);
				albumExists.showAndWait();
			}
		}
		else if (albumName.isPresent()){
			Alert noInput = new Alert(Alert.AlertType.ERROR, "No album name entered", ButtonType.CLOSE);
			noInput.showAndWait();
		}
	}

	/**
	 * deletes album
	 * @throws Exception
	 */
	public void deleteAlbum() throws Exception{
		if (currentAlbum == null){
			Alert noAlbumSelected = new Alert(Alert.AlertType.ERROR, "No album selected", ButtonType.CLOSE);
			noAlbumSelected.showAndWait();
			return;
		}
    	if (currentAlbum.toString().equalsIgnoreCase("stock")){
    	    Alert deleteStock = new Alert(Alert.AlertType.WARNING, "Cannot delete stock album", ButtonType.CLOSE);
            deleteStock.showAndWait();
            return;
		}
		Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this album?",ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> yesOrNo = deleteConfirmation.showAndWait();
		if (yesOrNo.get() == ButtonType.YES){
			ReadWrite.getCurrentUser().deleteAlbum(currentAlbum);
			rw.writeUsers();
			albumList.getItems().setAll(ReadWrite.getCurrentUser().getAlbums());
			albumList.refresh();
			albumList.getSelectionModel().selectPrevious();
		}
		else return;
	}

	/**
	 * adds photo to album
	 * @param e
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public void addPhoto(ActionEvent e) throws FileNotFoundException, Exception {
		if (currentAlbum == null){
			Alert noAlbumSelected = new Alert(Alert.AlertType.ERROR, "No album selected", ButtonType.CLOSE);
			noAlbumSelected.showAndWait();
			return;
		}
		FileChooser fc = new FileChooser();
		Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		File photoToAdd = fc.showOpenDialog(stage);
		if (photoToAdd != null) {
//			System.out.println("File Path: " + photoToAdd);
			Photo photoAdding = new Photo(photoToAdd);

			int currentAlbumIndex = ReadWrite.getCurrentUser().getAlbums().indexOf(currentAlbum);
			ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).addPhoto(photoAdding);
			rw.writeUsers();
			addToGallery(photoAdding);
		}
	}

	/**
	 * deletes photo from album
	 * @throws IOException
	 */
	public void deletePhoto() throws IOException{
		if (photoViewList.size() < 1){
			Alert nothingToDelete = new Alert(Alert.AlertType.ERROR, "No image to delete", ButtonType.CLOSE);
			nothingToDelete.showAndWait();
			return;
		}
		Alert deleteConfirmation = new Alert(Alert.AlertType.WARNING,"Are you sure you want to delete this photo?", ButtonType.YES, ButtonType.NO);
    	Optional<ButtonType> yesOrNo = deleteConfirmation.showAndWait();

		if (yesOrNo.get() == ButtonType.YES){
			int currentAlbumIndex = albumList.getSelectionModel().getSelectedIndex();
			photoViewList.remove(currentImageIndex);
			tile.getChildren().setAll(photoViewList);
			imgGallery.setContent(tile);
			ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).getAlbum().remove(currentImageIndex);
			rw.writeUsers();
			if (currentImageIndex > photoViewList.size()-1)
			currentImageIndex--;
			try {
				bigView.setImage(photoViewList.get(currentImageIndex).getImage());
			}
			catch(Exception e){
				bigView.setImage(null);
			}
		}
		else return;
		refreshPhotoInfo();
	}

	/**
	 * edits photo tag
	 */
	public void editTag() {
    	int currentAlbumIndex = albumList.getSelectionModel().getSelectedIndex();
		TextInputDialog modifyTag = new TextInputDialog();
		Photo currentPhoto = ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).getAlbum().get(currentImageIndex);
		modifyTag.setContentText("Enter either a location or person tag (separated by comma, no spaces):");
		((Button) modifyTag.getDialogPane().lookupButton(ButtonType.OK)).setText("Add Tag");
		((Button) modifyTag.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Delete Tag");
		modifyTag.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		final Button add = (Button) modifyTag.getDialogPane().lookupButton(ButtonType.OK);
		final Button delete = (Button) modifyTag.getDialogPane().lookupButton(ButtonType.CANCEL);
		add.addEventHandler(ActionEvent.ACTION, event -> {
			String tagInput = modifyTag.getEditor().getText();
			if (tagInput.isEmpty()){
				Alert noInput = new Alert(Alert.AlertType.WARNING, "Nothing Entered", ButtonType.CLOSE);
				noInput.showAndWait();
				return;
			}
			String[] splitter;
			try{
				splitter = tagInput.split(",", 2);
			}
			catch (Exception e){
				Alert invalidInput = new Alert(Alert.AlertType.WARNING, "Wrong tag input. Correct input would be \"Location,Prague\"", ButtonType.CLOSE);
				invalidInput.showAndWait();
				return;
			}
			if(splitter.length != 2){
				Alert invalidInput = new Alert(Alert.AlertType.WARNING, "Wrong tag input. Correct input would be \"Location,Prague\"", ButtonType.CLOSE);
				invalidInput.showAndWait();
				return;
			}
			if (splitter[0].equalsIgnoreCase("person")){
				currentPhoto.addTag(splitter[0], splitter[1]);
				try{
					rw.writeUsers();
				}
				catch(IOException ix){
					ix.printStackTrace();
				}
			}
			else if (splitter[0].equalsIgnoreCase("location")){
				ArrayList<Tag> tempTags = currentPhoto.getTags();
				for (Tag tag : tempTags){
					if (tag.key.equalsIgnoreCase("location")){
						Alert locationExists = new Alert(Alert.AlertType.ERROR, "Already has location tag", ButtonType.CLOSE);
						locationExists.showAndWait();
						return;
					}
				}
				currentPhoto.addTag(splitter[0], splitter[1]);
				try{
					rw.writeUsers();
				}
				catch(IOException ix){
					ix.printStackTrace();
				}
			}
			else{
				Alert invalidKey = new Alert(Alert.AlertType.WARNING, "Only location/person tags allowed", ButtonType.CLOSE);
				invalidKey.showAndWait();
				return;
			}
			refreshPhotoInfo();
		});

		delete.addEventHandler(ActionEvent.ACTION, event -> {
			String tagInput = modifyTag.getEditor().getText();
			if (tagInput.isEmpty()){
				Alert noInput = new Alert(Alert.AlertType.WARNING, "Nothing Entered", ButtonType.CLOSE);
				noInput.showAndWait();
				return;
			}
			String[] splitter;
			try{
				splitter = tagInput.split(",", 2);
			}
			catch (Exception e){
				Alert invalidInput = new Alert(Alert.AlertType.WARNING, "Wrong tag input. Correct input would be \"Location,Prague\"", ButtonType.CLOSE);
				invalidInput.showAndWait();
				return;
			}
			if(splitter.length != 2){
				Alert invalidInput = new Alert(Alert.AlertType.WARNING, "Wrong tag input. Correct input would be \"Location,Prague\"", ButtonType.CLOSE);
				invalidInput.showAndWait();
				return;
			}
			if (splitter[0].equalsIgnoreCase("person")){
				boolean deleteSuccess = currentPhoto.deleteTag(splitter[0], splitter[1]);
				if (!deleteSuccess){
					Alert deleteFail = new Alert(Alert.AlertType.WARNING, "Tag does not exist", ButtonType.CLOSE);
					deleteFail.showAndWait();
					return;
				}
				try{
					rw.writeUsers();
				}
				catch(IOException ix){
					ix.printStackTrace();
				}
			}
			else if (splitter[0].equalsIgnoreCase("location")){
				boolean deleteSuccess = currentPhoto.deleteTag(splitter[0], splitter[1]);
				if (!deleteSuccess){
					Alert deleteFail = new Alert(Alert.AlertType.WARNING, "Tag does not exist", ButtonType.CLOSE);
					deleteFail.showAndWait();
					return;
				}
				try{
					rw.writeUsers();
				}
				catch(IOException ix){
					ix.printStackTrace();
				}
			}
			else{
				Alert invalidKey = new Alert(Alert.AlertType.WARNING, "Only location/person tags allowed", ButtonType.CLOSE);
				invalidKey.showAndWait();
				return;
			}
			refreshPhotoInfo();
		});
		modifyTag.showAndWait();
		refreshPhotoInfo();
	}

	/**
	 * edits photo caption
	 */
	public void editCaption(){
		int currentAlbumIndex = albumList.getSelectionModel().getSelectedIndex();
		TextInputDialog editCap = new TextInputDialog();
		Photo currentPhoto = ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).getAlbum().get(currentImageIndex);
    	((Button) editCap.getDialogPane().lookupButton(ButtonType.OK)).setText("Apply");
    	final Button addCap = (Button) editCap.getDialogPane().lookupButton(ButtonType.OK);
    	addCap.addEventHandler(ActionEvent.ACTION, event ->{
			String captionToSet = editCap.getEditor().getText();
			currentPhoto.setCaption(captionToSet);
		});
		editCap.showAndWait();
		refreshPhotoInfo();
	}

	/**
	 * method for adding photos to gallery scrollpane
	 * @param photo
	 * @throws FileNotFoundException
	 */
	private void addToGallery(Photo photo) throws FileNotFoundException{
		Image img = new Image(new FileInputStream(photo.getFile()));
		ImageView photoView = new ImageView();
		photoView.setPreserveRatio(true);
		photoView.setFitHeight(125);
		photoView.setFitWidth(125);
		photoView.setImage(img);
		photoViewList.add(photoView);
		tile.getChildren().setAll(photoViewList);
		imgGallery.setContent(tile);
		bigView.setImage(img);
		currentImageIndex = photoViewList.indexOf(photoView);
		refreshPhotoInfo();
		
	}

	/**
	 * method for going to the previous photo in the list
	 * @param e
	 */
	public void navigateLeft(ActionEvent e){
		if (currentImageIndex > 0){
			currentImageIndex--;
			ImageView temp = photoViewList.get(currentImageIndex);
			bigView.setImage(temp.getImage());

			refreshPhotoInfo();
		}
	}

	/**
	 * method for going to next photo in the list
	 * @param e
	 */
	public void navigateRight(ActionEvent e){
		if (currentImageIndex < (photoViewList.size() - 1)){
			currentImageIndex++;
			ImageView temp = photoViewList.get(currentImageIndex);
			bigView.setImage(temp.getImage());

			refreshPhotoInfo();
		}
	}

	/**
	 * refreshes photo info
	 */
	private void refreshPhotoInfo(){

		int currentAlbumIndex = albumList.getSelectionModel().getSelectedIndex();
		if (ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).getAlbum().size() > 0){
			Photo photoData = ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).getAlbum().get(currentImageIndex);

			labelCaption.setText(photoData.getCaption());
			labelDate.setText(photoData.getCreationDate().toLocaleString());
			String tagStr = "";
			for (Tag tag : photoData.getTags())
			{
				tagStr += tag.key + "," + tag.value + "\n";
			}
			labelTags.setText(tagStr);
			labelNumPhotos.setText(String.valueOf(ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).getPhotoCount()));

			Date min = new Date();
			Date max = new Date();
			boolean firstpass = true;

			for (Photo p: ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).getAlbum()){
				if (firstpass){
					min = max = p.getCreationDate();
					firstpass = false;
				}
				else{
					if (p.getCreationDate().compareTo(min) < 0)
						min = p.getCreationDate();
					else if(p.getCreationDate().compareTo(max) > 0)
						max = p.getCreationDate();
				}
			}
			labelDateRange.setText(min.toLocaleString() + " to " + max.toLocaleString());
		}
		else{
			labelCaption.setText(null);
			labelDate.setText(null);
			labelTags.setText(null);
			labelNumPhotos.setText("0");
			labelDateRange.setText("");
		}
	}

	/**
	 * moves photo to another album
	 * @param e
	 * @throws IOException
	 */
	public void moveTo(ActionEvent e) throws IOException{
		int selected = 0;
		selected = SelectAlbum.display(ReadWrite.getCurrentUser().getAlbums()); //response from popup window (return -1 if canceled)
		int currentAlbumIndex = albumList.getSelectionModel().getSelectedIndex();
		Photo currentPhoto = ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).getAlbum().get(currentImageIndex);

		if (selected > -1 && selected != currentAlbumIndex){
			ReadWrite.getCurrentUser().getAlbums().get(selected).addPhoto(currentPhoto);
			photoViewList.remove(currentImageIndex);
			tile.getChildren().setAll(photoViewList);
			imgGallery.setContent(tile);
			ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).getAlbum().remove(currentImageIndex);
			rw.writeUsers();
			if (currentImageIndex > photoViewList.size()-1)
			currentImageIndex--;
			try {
				bigView.setImage(photoViewList.get(currentImageIndex).getImage());
			}
			catch(Exception ex){
				bigView.setImage(null);
			}
		}
		refreshPhotoInfo();
	}

	/**
	 * copies photo to another album
	 * @param e
	 * @throws IOException
	 */
	public void copyTo(ActionEvent e) throws IOException{
		int selected = 0;
		selected = SelectAlbum.display(ReadWrite.getCurrentUser().getAlbums()); //response from popup window (return -1 if canceled)
		int currentAlbumIndex = albumList.getSelectionModel().getSelectedIndex();
		Photo currentPhoto = ReadWrite.getCurrentUser().getAlbums().get(currentAlbumIndex).getAlbum().get(currentImageIndex);

		if (selected > -1 && selected != currentAlbumIndex){
			ReadWrite.getCurrentUser().getAlbums().get(selected).addPhoto(currentPhoto);
			rw.writeUsers();
		}
	}

	/**
	 * searches for photos with certain tag
	 * @param e
	 */
	public void search(ActionEvent e){
    	if (searchText == null){
    		return;
		}
		if (!searchText.getText().isEmpty()){
			ArrayList<Photo> photoSearchList;
			ArrayList<Photo> newPhotoList = new ArrayList<Photo>();
		
			photoSearchList = ReadWrite.getCurrentUser().getAlbums().get(albumList.getSelectionModel().getSelectedIndex()).getAlbum();
	
			for (Photo p: photoSearchList){
				for (Tag t: p.getTags()){
					if (searchText.getText().equalsIgnoreCase(t.value)){
						newPhotoList.add(p);
						break;
					}
				}
			}

			if(!newPhotoList.isEmpty()){
				photoViewList.clear();
				for (Photo photo : newPhotoList){
                    try {
						addToGallery(photo);
                    }
                    catch(FileNotFoundException fnfx){
                        fnfx.printStackTrace();
                    }
                }
			}
			tile.getChildren().setAll(photoViewList);
			imgGallery.setContent(tile);
			}
		}


	/**
	 * clears search, refreshes gallery
	 * @param e
	 * @throws FileNotFoundException
	 */
	public void clearSearch(ActionEvent e) throws FileNotFoundException{
		searchText.clear();
		photoViewList.clear();
		for (Photo photo:currentAlbum.getAlbum()){
			addToGallery(photo);
		}
		tile.getChildren().setAll(photoViewList);
		imgGallery.setContent(tile);
	}

	/**
	 * Logs out current user, exits album view and goes back to login page
	 * @param e onAction for "Logout" button
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
