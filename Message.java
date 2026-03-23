import java.time.LocalDateTime;
import java.time.LocalDate;

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
        this.isLiked = !this.isLiked; // Inverts isLiked
    }

    public String getFormattedTime() {
        if (this.timestamp.toLocalDate().equals(LocalDate.now())) {
            // return time in HH:mm format
        } else {
            // return date in dd/MM/yyyy format
        }
    }

    public LocalDateTime getUnformattedTime() {
        return this.timestamp;
    }


    
}
