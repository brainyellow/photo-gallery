package photos.model;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.regex.Pattern;

/** Class to store photo data
 * @author Brian Huang
 * @author Tyler Latawiec
 */
public class Photo implements Serializable{
    private File photoFile;
    private String filename;
    private String caption;
    private Date creationDate;
    private ArrayList<Tag> tags = new ArrayList<Tag>();

    /**
     * Photo constructor
     * @param file
     */
    public Photo(File file){
        this.photoFile = file;
        this.filename = file.getName();
        this.creationDate = new Date(file.lastModified());
        String[] splitter = filename.split(Pattern.quote(File.separator));
        this.caption = splitter[splitter.length - 1];
    }

    /**
     * sets photo caption
     * @param caption
     */
    public void setCaption(String caption){ this.caption = caption; }

    /**
     * gets caption
     * @return String
     */
    public String getCaption(){ return caption; }

    /**
     * gets date created
     * @return Date
     */
    public Date getCreationDate(){ return creationDate; }

    /**
     * gets photo file
     * @return File
     */
    public File getFile(){ return this.photoFile; }

    /**
     * gets tags of photo
     * @return ArrayList of tags
     */
    public ArrayList<Tag> getTags(){return tags;}

    /**
     * adds a tag to photo
     * @param key
     * @param value
     */
    public void addTag(String key, String value){
        tags.add(new Tag(key, value));
    }

    /**
     * delets a tag from arraylist
     * @param key
     * @param value
     * @return true if delete successful
     */
    public boolean deleteTag(String key, String value){
        boolean tagExists = false;
        for (Tag tag : tags){
            if (tag.key.equalsIgnoreCase(key) && tag.value.equalsIgnoreCase(value)){
                tags.remove(tag);
                tagExists = true;
            }
        }
        return tagExists;
    }

    /**
     * toString method
     * @return String
     */
    @Override
    public String toString(){
        return filename;
    }
}
