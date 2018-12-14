package photos.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/** Class for storing data for Users
 * @author Brian Huang
 * @author Tyler Latawiec
 */
public class User implements Serializable{

    private String username;
    private ArrayList<Album> albumList;

    /**
     * User constructor
     * @param username
     */
    public User(String username){
        this.username = username;
        albumList = new ArrayList<>();
    }

    /**
     * adds album
     * @param album
     * @return true if add successful
     */
    public boolean addAlbum(Album album){
        boolean exists = false;
        for (Album alb : albumList){
            if (alb.toString().equalsIgnoreCase(album.toString()))
                exists = true;
        }
        if (!exists) {
            albumList.add(album);
            return true;
        }
        else
            return false;
    }

    /**
     * Deletes album
     * @param toRemove
     */
    public void deleteAlbum(Album toRemove){
        albumList.remove(toRemove);
    }

    /**
     * returns albums
     * @return ArrayList of albums
     */
    public ArrayList<Album> getAlbums(){ return albumList; }

    /**
     * Returns username of user
     * @return toString of User
     */
    @Override
    public String toString()
    {
        return username;
    }
}
