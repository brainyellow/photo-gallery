package photos.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that stores album data
 * @author Brian Huang
 * @author Tyler Latawiec
 */
public class Album implements Serializable {

    private String albumName;
    private ArrayList<Photo> photoList;

    /**
     * album constructor
     * @param albumName
     */
    public Album(String albumName){
        this.albumName = albumName;
        photoList = new ArrayList<>();
    }

    /**
     * toString method
     * @return String representation of album
     */
    @Override
    public String toString(){ return albumName; }

    /**
     * sets album name
     * @param albumName
     */
    public void setAlbumName(String albumName){
        this.albumName = albumName;
    }

    /**
     * adds photo to album
     * @param p
     */
    public void addPhoto(Photo p){
        photoList.add(p);
    }

    /**
     * delets photo from album
     * @param p
     */
    public void deletePhoto(Photo p){
        photoList.remove(p);
    }

    /**
     * returns album
     * @return ArrayList of the album of photos
     */
    public ArrayList<Photo> getAlbum(){ return photoList; }

    /**
     * returns photo count in album
     * @return int of photo count
     */
    public int getPhotoCount(){ return photoList.size(); }
}
