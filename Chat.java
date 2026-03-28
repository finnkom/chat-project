import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

public class Chat {
    private String chatID;
    private ArrayList<User> participants;
    private LinkedList<Message> messages;
    private boolean isDeleted;

    public Chat(String chatID, ArrayList<User> participants) {
        this.chatID = chatID;
        this.participants = participants;
        this.messages = new LinkedList<>();
        this.isDeleted = false;
    }

    public void addMessage(User sender, String content) {
        messages.add(new Message(sender, content));
    }
    public void deleteChat() {
        this.isDeleted = true; // Mark the chat as deleted
    }

    public ArrayList<Message> getRecent():
        // Return the recent messages in the chat
    }
}
