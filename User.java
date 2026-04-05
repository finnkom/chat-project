import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;
public class User {

    // --- Fields ---
    private String userId;
    private String displayName;
    private String phoneNumber;
    private String statusMessage;
    private HashMap<String, String> contacts;        // HashSet prevents duplicate contacts
    private ArrayList<Chat> chats;         // All chats this user is part of
    private Queue<Message> notifications;  // FIFO queue for incoming notifications

    // --- Constructor ---
    public User(String userId, String displayName, String phoneNumber) {
        this.userId = userId;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.statusMessage = "";
        this.contacts = new HashMap<>();
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

    // Adds a contact by their userId and a custom saved name.
    public void addContactById(String userId, String savedName) {
        contacts.put(userId, savedName);
    }
    // Removes a contact by userId.
    public void removeContactById(String userId) {
        contacts.remove(userId);
    }
    public HashMap<String, String> getContactMap() {
        return contacts;
    }
    // Returns the saved name for a contact userId, or null if not a contact.
    public String getSavedNameFor(String userId) {
        return contacts.get(userId);
    }
    // Prints contacts sorted A-Z by saved name.
    public void printContactsAlphabetical() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts.");
            return;
        }

        ArrayList<Map.Entry<String, String>> entries = new ArrayList<>(contacts.entrySet());
        entries.sort((a, b) -> a.getValue().compareToIgnoreCase(b.getValue()));
        System.out.println("=== Contacts (A-Z) ===");
        for (int i = 0; i < entries.size(); i++) {
            System.out.println((i + 1) + ". " + entries.get(i).getValue() + " (ID: " + entries.get(i).getKey() + ")");
        }
    }

    // Prints contacts sorted by most recently messaged.
    public void printContactsByRecent() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts.");
            return;
        }

        // Build a list of contact IDs so we can sort them
        ArrayList<String> contactIds = new ArrayList<>(contacts.keySet());

        contactIds.sort((idA, idB) -> {
            java.time.LocalDateTime timeA = getLastMessageTimeWithId(idA);
            java.time.LocalDateTime timeB = getLastMessageTimeWithId(idB);
            if (timeA == null && timeB == null) return 0;
            if (timeA == null) return 1;  // null goes to the end
            if (timeB == null) return -1;
            return timeB.compareTo(timeA); // most recent first
        });

        System.out.println("=== Contacts (Recent) ===");
        for (int i = 0; i < contactIds.size(); i++) {
            String id = contactIds.get(i);
            String savedName = contacts.get(id);
            java.time.LocalDateTime lastTime = getLastMessageTimeWithId(id);
            String timeLabel = lastTime == null ? "never messaged" : lastTime.format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            );
            System.out.println((i + 1) + ". " + savedName + " (ID: " + id + ")  - " + timeLabel);
        }
    }

    // Returns the timestamp of the most recent message shared with a contact.
    // Returns null if no messages have been exchanged.
    // Returns the timestamp of the most recent message in any shared chat with a contact,
// matched by userId string rather than User object.
    private java.time.LocalDateTime getLastMessageTimeWithId(String contactId) {
        java.time.LocalDateTime latest = null;
        for (Chat chat : chats) {
            if (chat.getIsDeleted()) continue;
            // Check if this chat contains a participant with the matching userId
            boolean containsContact = false;
            for (User p : chat.getParticipants()) {
                if (p.getUserId().equals(contactId)) {
                    containsContact = true;
                    break;
                }
            }
            if (!containsContact) continue;
            LinkedList<Message> messages = chat.getMessages();
            if (!messages.isEmpty()) {
                java.time.LocalDateTime t = messages.getLast().getUnformattedTime();
                if (latest == null || t.isAfter(latest)) {
                    latest = t;
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

    public HashMap<String, String> getContacts() { return contacts; }

    public ArrayList<Chat> getChats() { return chats; }

    public Queue<Message> getNotifications() { return notifications; }
}
