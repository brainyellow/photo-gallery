package photos.controller;

import photos.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** Class for writing and reading from file
 * @author Brian Huang
 * @author Tyler Latawiec
 */
public class ReadWrite {
    private FileOutputStream fos = null;
    private FileInputStream fis = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;

    // holds user list for all controller classes
    static List<User> users = new ArrayList<>();
    private static User currentUser;

    @SuppressWarnings("unchecked")
    public void readUsers() throws IOException, ClassNotFoundException{
        fis = new FileInputStream("Users.ser");
        ois = new ObjectInputStream(fis);
        users = (ArrayList<User>) ois.readObject();
    }
    public void writeUsers() throws IOException{
        fos = new FileOutputStream("Users.ser");
        oos = new ObjectOutputStream(fos);

        oos.writeObject(users);
        oos.flush();
        oos.close();
    }
    public static void setCurrentUser(User user){
        int userIndex = users.indexOf(user);
        currentUser = users.get(userIndex);
    }
    public static User getCurrentUser(){ return currentUser; }

}
