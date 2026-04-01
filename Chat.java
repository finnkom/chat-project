import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;
import java.util.Scanner;

public class Chat {
    private String chatID;
    private ArrayList<User> participants;
    private LinkedList<Message> messages;
    private boolean isDeleted;
    private Scanner scanner;

    public Chat(String chatID, ArrayList<User> participants, Scanner scanner) {
        this.chatID = UUID.randomUUID().toString(); // Generate a unique chat ID
        this.participants = participants;
        this.messages = new LinkedList<>();
        this.isDeleted = false;
        this.scanner = scanner;
    }

    public void addMessage(User sender) {
        scanner.nextLine(); // Consume the newline left by nextInt() or similar
        System.out.print("Enter your message: ");
        String content = scanner.nextLine();
        messages.add(new Message(sender, content));
    }
    public void deleteChat() {
        this.isDeleted = true; // Mark the chat as deleted
    }

    public ArrayList<Message> getRecent() {
        int size = messages.size();
        int from = Math.max(0, size - 3);
        return new ArrayList<>(messages.subList(from, size)); // Return the last 3 messages, or all if there are less than 3
    }

    public void displayChat() {
        if (isDeleted) {
            System.out.println("This chat has been deleted.");
            return;
        }
        for (Message message : messages) {
            System.out.println("\n" + message.getSender().getUsername());
            System.out.println(message.getContent());
            System.out.println(message.getFormattedTime());
        }
    }
}
