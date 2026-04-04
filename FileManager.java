import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class FileManager {

    private static final String SECTION_USERS = "USERS";
    private static final String SECTION_CONTACTS = "CONTACTS";
    private static final String SECTION_CHATS = "CHATS";
    private static final String SECTION_MESSAGES = "MESSAGES";
    private static final String SEP = "|";
    private static final String SPLIT = "\\|";

    //save

    public static void saveToFile(ArrayList<User> users, String filename) {
        ArrayList<Chat> allChats = collectAllChats(users);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            // users
            writer.println(SECTION_USERS);
            for (User u : users) {
                writer.println(
                        (u.getUserId()) + SEP +
                                (u.getUsername()) + SEP +
                                (u.getPhoneNumber()) + SEP +
                                (u.getStatusMessage())
                );
            }
            writer.println();

            // CONTACTS — ownerUserId|contactUserId|savedName
            writer.println(SECTION_CONTACTS);
            for (User u : users) {
                for (Map.Entry<String, String> entry : u.getContactMap().entrySet()) {
                    writer.println(
                            (u.getUserId()) + SEP +
                                    (entry.getKey()) + SEP +
                                    (entry.getValue())
                    );
                }
            }
            writer.println();

            // CHATS
            writer.println(SECTION_CHATS);
            for (Chat chat : allChats) {
                StringBuilder pIds = new StringBuilder();
                ArrayList<User> parts = chat.getParticipants();
                for (int i = 0; i < parts.size(); i++) {
                    pIds.append(parts.get(i).getUserId());
                    if (i < parts.size() - 1) pIds.append(",");
                }
                writer.println(
                        (chat.getChatID()) + SEP +
                                chat.getIsDeleted() + SEP +
                                pIds
                );
            }
            writer.println();

            // MESSAGES
            writer.println(SECTION_MESSAGES);
            for (Chat chat : allChats) {
                for (Message msg : chat.getMessages()) {
                    // Build likedBy string
                    StringBuilder likedBy = new StringBuilder();
                    ArrayList<User> likers = msg.getLikedBy();
                    for (int i = 0; i < likers.size(); i++) {
                        likedBy.append(likers.get(i).getUserId());
                        if (i < likers.size() - 1) likedBy.append(",");
                    }
                    // Build readBy string
                    StringBuilder readBy = new StringBuilder();
                    String[] readers = msg.getReadBy().toArray(new String[0]);
                    for (int i = 0; i < readers.length; i++) {
                        readBy.append(readers[i]);
                        if (i < readers.length - 1) readBy.append(",");
                    }

                    String safeContent = msg.getContent() == null ? "{{DELETED}}" : msg.getContent();

                    writer.println(
                            chat.getChatID() + SEP +
                                    msg.getMessageID() + SEP +
                                    msg.getSender().getUserId() + SEP +
                                    safeContent + SEP +
                                    msg.getUnformattedTime().toString() + SEP +
                                    msg.getMessageIndex() + SEP +
                                    msg.getIsDeleted() + SEP +
                                    likedBy + SEP +
                                    readBy
                    );
                }
            }
            writer.println();

            System.out.println("Data saved to " + filename);

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // LOAD

    public static ArrayList<User> loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("No save file found. Starting fresh.");
            return new ArrayList<>();
        }

        Map<String, User> userMap = new LinkedHashMap<>();
        Map<String, Chat> chatMap = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String section = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equals(SECTION_USERS) || line.equals(SECTION_CONTACTS) ||
                        line.equals(SECTION_CHATS) || line.equals(SECTION_MESSAGES)) {
                    section = line;
                    continue;
                }

                String[] parts = line.split(SPLIT, -1);

                switch (section) {
                    case SECTION_USERS -> {
                        if (parts.length < 4) break;
                        String userId = (parts[0]);
                        String name = (parts[1]);
                        String phone = (parts[2]);
                        String status = (parts[3]);
                        User u = new User(userId, name, phone);
                        u.editProfile(null, null, status);
                        userMap.put(userId, u);
                    }
                    case SECTION_CONTACTS -> {
                        if (parts.length < 3) break;
                        String ownerId = (parts[0]);
                        String contactId = (parts[1]);
                        String savedName = (parts[2]);
                        User owner = userMap.get(ownerId);
                        if (owner != null) owner.addContactById(contactId, savedName);
                    }
                    case SECTION_CHATS -> {
                        if (parts.length < 3) break;
                        String chatID = (parts[0]);
                        boolean isDeleted = Boolean.parseBoolean(parts[1]);
                        String[] pIds = parts[2].split(",");

                        ArrayList<User> participants = new ArrayList<>();
                        for (String pid : pIds) {
                            User u = userMap.get(pid.trim());
                            if (u != null) participants.add(u);
                        }

                        Chat chat = new Chat(chatID, participants, null);
                        if (isDeleted) chat.deleteChat();
                        chatMap.put(chatID, chat);
                        for (User u : participants) u.addChat(chat);
                    }
                    case SECTION_MESSAGES -> {
                        if (parts.length < 9) break;
                        String chatID = (parts[0]);
                        String messageID = (parts[1]);
                        String senderID = (parts[2]);
                        String content = parts[3].equals("{{DELETED}}") ? null : parts[3];
                        LocalDateTime ts = LocalDateTime.parse(parts[4]);
                        int index = Integer.parseInt(parts[5]);
                        boolean isDeleted = Boolean.parseBoolean(parts[6]);
                        String likedByRaw = parts[7];
                        String readByRaw = parts[8];

                        Chat chat = chatMap.get(chatID);
                        User sender = userMap.get(senderID);
                        if (chat == null || sender == null) break;

                        Message msg = new Message(messageID, sender, content, ts, index);
                        if (isDeleted) msg.deleteMessage();

                        if (!likedByRaw.isBlank()) {
                            for (String lid : likedByRaw.split(",")) {
                                User liker = userMap.get(lid.trim());
                                if (liker != null) msg.toggleLike(liker);
                            }
                        }
                        if (!readByRaw.isBlank()) {
                            for (String rid : readByRaw.split(",")) {
                                if (!rid.isBlank()) msg.markReadBy(rid.trim());
                            }
                        }

                        chat.getMessages().add(msg);
                    }
                }
            }

            System.out.println("Data loaded from " + filename);

        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
            return new ArrayList<>();
        }

        return new ArrayList<>(userMap.values());
    }

    // HELPERS

    private static ArrayList<Chat> collectAllChats(ArrayList<User> users) {
        Map<String, Chat> seen = new LinkedHashMap<>();
        for (User u : users) {
            for (Chat c : u.getChats()) {
                seen.put(c.getChatID(), c);
            }
        }
        return new ArrayList<>(seen.values());
    }

}
