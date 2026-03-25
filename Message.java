import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        String formattedtime;
        if (this.timestamp.toLocalDate().equals(LocalDate.now())) 
        // return time in HH:mm format if message was sent today
        {
            formattedtime = this.timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else 
        // return date in dd/MM/yyyy format if message was sent before today
        {
            formattedtime = this.timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return formattedtime;
    }

    public LocalDateTime getUnformattedTime() {
        return this.timestamp;
    }

    public String getMessageID() {
        return this.messageID;
    }


    
}
