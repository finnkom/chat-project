import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.UUID;

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

    public String getChatID() {
        return this.chatID;
    }
    public ArrayList<User> getParticipants() {
        return this.participants;
    }
    public LinkedList<Message> getMessages() {
        return this.messages;
    }
    public boolean getIsDeleted() {
        return this.isDeleted;
    }

    public void addMessage(User sender) {
        scanner.nextLine(); // Consume the newline left by nextInt() or similar
        System.out.print("Enter your message: ");
        String content = scanner.nextLine();
        int index = messages.size() + 1; // Message index starts at 1
        messages.add(new Message(sender, content, index));
    }



    public void deleteChat() {
        this.isDeleted = true; // Mark the chat as deleted
    }

    public ArrayList<Message> getRecent() {
        int size = messages.size();
        int from = Math.max(0, size - 3);
        return new ArrayList<>(messages.subList(from, size)); // Return the last 3 messages, or all if there are less than 3
    }
    public void markAllReadBy(String userId) {
        for (Message message : messages) {
            message.markReadBy(userId);
        }
    }

    // Returns true if the last message in this chat has been read by the given user.
    // Returns true if there are no messages (nothing to read).
    public boolean isLastMessageReadBy(String userId) {
        if (messages.isEmpty()) return true;
        return messages.getLast().isReadBy(userId);
    }
    public void displayChat() {
        if (isDeleted) {
            System.out.println("This chat has been deleted.");
            return;
        }
        for (Message message : messages) {
            message.printMessage();
        }
    }

    public Message selectMessage(User currentUser) {
        // Keep asking for a valid message number
        Message selectedMessage = null;
        while (selectedMessage == null) {
            System.out.print("Enter the number of the message you want to interact with (0 to cancel): ");
            int messageIndex = scanner.nextInt();
            if (messageIndex == 0) {
                System.out.println("Action cancelled.");
                return null; // Return null to indicate cancellation
            }
            if (messageIndex < 1 || messageIndex > messages.size()) {
                System.out.println("Invalid message number. Please try again.");
                continue;
            }
            selectedMessage = messages.get(messageIndex - 1);
            if (selectedMessage.getIsDeleted()) {
                System.out.println("This message has been deleted and cannot be interacted with. Please try again.");
                selectedMessage = null; // Reset to keep looping
            }
        }

        System.out.println("Selected Message:");
        selectedMessage.printMessage();

        return selectedMessage;
    }

    public void interactWithMessage(User currentUser, Message selectedMessage) {
        // Keep asking for a valid action choice
        boolean validChoice = false;
        while (!validChoice) {
            System.out.println("Options:");
            System.out.println("0 - Cancel");
            System.out.println("1 - Like/Unlike");
            if (selectedMessage.getSender().equals(currentUser)) {
                System.out.println("2 - Edit Content");
                System.out.println("3 - Delete Message");
            }
            System.out.print("Enter your choice: ");
            String choice = scanner.next();
            switch (choice) {
                case "0" -> {
                    System.out.println("Action cancelled.");
                    validChoice = true;
                }
                case "1" -> {
                    selectedMessage.toggleLike(currentUser);
                    System.out.println("Like status toggled.");
                    validChoice = true;
                }
                default -> {
                    if (selectedMessage.getSender().equals(currentUser)) {
                        switch (choice) {
                            case "2" -> {
                                scanner.nextLine(); // Consume the newline
                                System.out.print("Enter new content: ");
                                String newContent = scanner.nextLine();
                                selectedMessage.editContent(newContent);
                                System.out.println("Message content updated.");
                                validChoice = true;
                            }
                            case "3" -> {
                                selectedMessage.deleteMessage();
                                System.out.println("Message deleted.");
                                validChoice = true;
                            }
                            default -> System.out.println("Invalid choice. Please try again.");
                        }
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        }
    }
}

