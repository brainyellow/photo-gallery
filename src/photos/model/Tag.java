package photos.model;

import java.io.Serializable;

/** Class to store tag data
 * @author Brian Huang
 * @author Tyler Latawiec
 */
public class Tag implements Serializable{

    public String key, value;

    /**
     * Tag constructor
     * @param key
     * @param value
     */
    public Tag(String key, String value){
        this.key = key;
        this.value = value;
    }
}