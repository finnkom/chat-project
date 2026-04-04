import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashSet;

public class Message {
    private String messageID;
    private User sender;
    private String content;
    private LocalDateTime timestamp;
    private ArrayList<User> likedBy;
    private boolean isDeleted;
    private int messageIndex;
    private HashSet<String> readBy;

    public Message(User sender, String content, int messageIndex) {
        this.messageID    = UUID.randomUUID().toString();
        this.sender       = sender;
        this.content      = content;
        this.timestamp    = LocalDateTime.now();
        this.likedBy      = new ArrayList<>();
        this.isDeleted    = false;
        this.messageIndex = messageIndex;
        this.readBy       = new HashSet<>();
    }
    // Load constructor — used by FileManager when restoring from file
    public Message(String savedMessageID, User sender, String content,
                   LocalDateTime savedTimestamp, int messageIndex) {
        this.messageID    = savedMessageID;
        this.sender       = sender;
        this.content      = content;
        this.timestamp    = savedTimestamp;
        this.likedBy      = new ArrayList<>();
        this.isDeleted    = false;
        this.messageIndex = messageIndex;
        this.readBy       = new HashSet<>();
    }
    // Mark this message as read by a user (identified by userId)
    public void markReadBy(String userId) {
        readBy.add(userId);
    }

    // Check whether a given user has read this message
    public boolean isReadBy(String userId) {
        return readBy.contains(userId);
    }

    // Returns the raw readBy set — used by FileManager for saving
    public HashSet<String> getReadBy() {
        return readBy;
    }

    public void toggleLike(User user) {
        if (likedBy.contains(user)) {
            likedBy.remove(user);
        } else {
            likedBy.add(user);
        }
    }

    public void editContent(String newContent) {
        this.content = newContent;
    }

    public void deleteMessage() {
        this.isDeleted = true; // Mark the message as deleted
        this.content = null; // Clear the content to indicate deletion
    }

    public boolean getIsDeleted() {
        return this.isDeleted;
    }

    public String getFormattedTime() {
        String formattedtime;
        if (this.timestamp.toLocalDate().equals(LocalDate.now())) 
        // return time in HH:mm format if message was sent today
        {
            formattedtime = this.timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
            formattedtime = "Today at " + formattedtime;
        } else 
        // return date in dd/MM/yyyy format if message was sent before today
        {
            formattedtime = this.timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            formattedtime = "Sent on " + formattedtime;
        }
        return formattedtime;
    }

    public void printMessage() {
        System.out.println();
        System.out.println("Message: " + this.messageIndex);
        System.out.println(sender.getUsername());
        if (isDeleted) {
            System.out.println("This message has been deleted.");
        } else {
            System.out.println(this.content);
        }
        System.out.println(this.getFormattedTime());
        if (!likedBy.isEmpty()) {
            String Likers = "Liked by: "; 
            for (User user : likedBy) {
                Likers += user.getUsername() + ", ";
            }
            Likers = Likers.substring(0, Likers.length() - 2); // Remove the trailing comma and space
            System.out.println(Likers);
        }
    }

    public int getMessageIndex() {
        return this.messageIndex;
    }

    public ArrayList<User> getLikedBy() {
        return this.likedBy;
    }

    public LocalDateTime getUnformattedTime() {
        return this.timestamp;
    }

    public String getMessageID() {
        return this.messageID;
    }

    public User getSender() {
        return this.sender;
    }

    public String getContent() {
        return this.content;
    }



    
}
