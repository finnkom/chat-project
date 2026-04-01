import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class User {
    private String userId;
    private String displayName;
    private String phoneNumber;
    private String statusMessage;
    private HashSet<User> contacts;
    private ArrayList<Chat> chats;
    private Queue<Message> notifications;

    public User(String userId, String displayName, String phoneNumber) {}

    public void editProfile(String newName, String newPhone, String newStatus) {}
    public void addContact(User user) {}
    public void removeContact(User user) {}
    public void sendMessage(Chat chat, String content) {}
    public ArrayList<Chat> getChats() {}
    public HashSet<User> getContacts() {}
    public Queue<Message> getNotifications() {}
}
