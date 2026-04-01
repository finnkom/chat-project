import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Message {
    private String messageID;
    private User sender;
    private String content;
    private LocalDateTime timestamp;
    private boolean isLiked;
    private boolean isDeleted;

    public Message(User sender, String content) {
        this.messageID = UUID.randomUUID().toString(); // Generate a unique message ID
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isLiked = false;
        this.isDeleted = false;
    }

    public void toggleLike() {
        this.isLiked = !this.isLiked; // Inverts isLiked
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

    public boolean getLiked() {
        return this.isLiked;
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
