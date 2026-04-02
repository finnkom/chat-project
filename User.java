import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class User {

    // --- Fields ---
    private String userId;
    private String displayName;
    private String phoneNumber;
    private String statusMessage;
    private HashSet<User> contacts;        // HashSet prevents duplicate contacts
    private ArrayList<Chat> chats;         // All chats this user is part of
    private Queue<Message> notifications;  // FIFO queue for incoming notifications

    // --- Constructor ---
    public User(String userId, String displayName, String phoneNumber) {
        this.userId = userId;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.statusMessage = "";
        this.contacts = new HashSet<>();
        this.chats = new ArrayList<>();
        this.notifications = new LinkedList<>(); // LinkedList implements Queue
    }

    // =========================================================
    // PROFILE METHODS
    // =========================================================

    // Updates the user's name, phone, and status. Pass null to skip a field.
    public void editProfile(String newName, String newPhone, String newStatus) {
        if (newName != null && !newName.isBlank()) {
            this.displayName = newName;
        }
        if (newPhone != null && !newPhone.isBlank()) {
            this.phoneNumber = newPhone;
        }
        if (newStatus != null) {
            this.statusMessage = newStatus;
        }
    }

    // Prints this user's profile. userId is shown but cannot be edited.
    public void printProfile() {
        System.out.println("=== Profile ===");
        System.out.println("User ID: " + userId);
        System.out.println("Name:    " + displayName);
        System.out.println("Phone:   " + phoneNumber);
        System.out.println("Status:  " + (statusMessage.isBlank() ? "(no status)" : statusMessage));
    }

    // =========================================================
    // CONTACT METHODS
    // =========================================================

    // Adds a contact. HashSet ignores duplicates automatically.
    public void addContact(User user) {
        contacts.add(user);
    }

    // Removes a contact. Does NOT delete any shared chats.
    public void removeContact(User user) {
        contacts.remove(user);
    }

    // Prints contacts sorted A-Z by name.
    public void printContactsAlphabetical() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts.");
            return;
        }
        // Copy to ArrayList so we can sort (HashSet has no order)
        ArrayList<User> sorted = new ArrayList<>(contacts);
        sorted.sort((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()));
        System.out.println("=== Contacts (A-Z) ===");
        for (int i = 0; i < sorted.size(); i++) {
            System.out.println((i + 1) + ". " + sorted.get(i).getUsername());
        }
    }

    // Prints contacts sorted by most recently messaged.
    public void printContactsByRecent() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts.");
            return;
        }
        ArrayList<User> sorted = new ArrayList<>(contacts);
        sorted.sort((a, b) -> {
            java.time.LocalDateTime timeA = getLastMessageTimeWith(a);
            java.time.LocalDateTime timeB = getLastMessageTimeWith(b);
            if (timeA == null && timeB == null) return 0;
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            return timeB.compareTo(timeA); // Most recent first
        });
        System.out.println("=== Contacts (Recent) ===");
        for (int i = 0; i < sorted.size(); i++) {
            System.out.println((i + 1) + ". " + sorted.get(i).getUsername());
        }
    }

    // Returns the timestamp of the most recent message shared with a contact.
    // Returns null if no messages have been exchanged.
    private java.time.LocalDateTime getLastMessageTimeWith(User contact) {
        java.time.LocalDateTime latest = null;
        for (Chat chat : chats) {
            if (chat.getParticipants().contains(contact)) {
                LinkedList<Message> messages = chat.getMessages();
                if (!messages.isEmpty()) {
                    java.time.LocalDateTime t = messages.getLast().getUnformattedTime();
                    if (latest == null || t.isAfter(latest)) {
                        latest = t;
                    }
                }
            }
        }
        return latest;
    }

    // Prints a contact's profile and their 3 most recent chats with this user.
    public void printContactProfile(User contact) {
        contact.printProfile();
        System.out.println();
        System.out.println("=== 3 Most Recent Chats ===");

        ArrayList<Chat> sharedChats = getChatsWithContact(contact);

        if (sharedChats.isEmpty()) {
            System.out.println("No chats with this contact.");
            return;
        }

        // Sort newest first
        sharedChats.sort((a, b) -> {
            java.time.LocalDateTime tA = a.getMessages().isEmpty() ? null : a.getMessages().getLast().getUnformattedTime();
            java.time.LocalDateTime tB = b.getMessages().isEmpty() ? null : b.getMessages().getLast().getUnformattedTime();
            if (tA == null && tB == null) return 0;
            if (tA == null) return 1;
            if (tB == null) return -1;
            return tB.compareTo(tA);
        });

        int limit = Math.min(3, sharedChats.size());
        for (int i = 0; i < limit; i++) {
            System.out.println("\n--- Chat " + (i + 1) + " ---");
            sharedChats.get(i).displayChat();
        }
    }

    // Returns all non-deleted chats that include the given contact.
    public ArrayList<Chat> getChatsWithContact(User contact) {
        ArrayList<Chat> result = new ArrayList<>();
        for (Chat chat : chats) {
            if (!chat.getIsDeleted() && chat.getParticipants().contains(contact)) {
                result.add(chat);
            }
        }
        return result;
    }

    // =========================================================
    // CHAT METHODS
    // =========================================================

    // Adds a chat to this user's chat list.
    public void addChat(Chat chat) {
        chats.add(chat);
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public String getUserId() { return userId; }

    public String getUsername() { return displayName; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getStatusMessage() { return statusMessage; }

    public HashSet<User> getContacts() { return contacts; }

    public ArrayList<Chat> getChats() { return chats; }

    public Queue<Message> getNotifications() { return notifications; }
}
