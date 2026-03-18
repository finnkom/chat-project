import java.time.LocalDateTime;

public class Message {
    private String messageID;
    private User sender;
    private String content;
    private LocalDateTime timestamp;
    private boolean isLiked;

    public Message(String messageID, User sender, String content) {
        this.messageID = messageID;
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isLiked = false;
    }

    public void toggleLike() {
        // Toggle the like status of the message
    }

    public String getFormattedTime() {
        // Return the timestamp in a human-readable format
    }


    
}
