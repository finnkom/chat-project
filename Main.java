import java.util.*;
import java.util.Scanner;


public class Main {

    private static final String SAVE_FILE = "chatapp_data.txt";

    private static ArrayList<User> allUsers = new ArrayList<>();
    private static User            currentUser = null;
    private static Scanner         scanner     = new Scanner(System.in);


    public static void main(String[] args) {
        allUsers = FileManager.loadFromFile(SAVE_FILE);
        printBanner();
        landingMenu();
    }


    private static void printBanner() {
        System.out.println(" Messaging App ");
    }

    private static void landingMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1 - Login");
            System.out.println("2 - Register");
            System.out.println("0 - Exit");
            System.out.print("Choice: ");
            String choice = scanner.next().trim();

            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "0" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void login() {
        scanner.nextLine();
        System.out.print("Enter your User ID: ");
        String id = scanner.nextLine().trim();

        User found = findUserById(id);
        if (found == null) {
            System.out.println("User ID not found. Please register first.");
            return;
        }
        currentUser = found;
        System.out.println("Welcome back, " + currentUser.getUsername() + "!");
        mainMenu();
    }

    private static void register() {
        scanner.nextLine();
        System.out.print("Enter a display name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine().trim();

        int idNum = 1;
        while (findUserById("U" + idNum) != null) {
            idNum++;
        }
        String id = "U" + idNum;
        User newUser = new User(id, name, phone);
        allUsers.add(newUser);

        FileManager.saveToFile(allUsers, SAVE_FILE);

        System.out.println("Registered successfully!");
        System.out.println("Your User ID is: " + id + "  <-- save this to log in later");
        currentUser = newUser;
        mainMenu();
    }

    private static void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("======= Main Menu =======");
            System.out.println("1 - Chats");
            System.out.println("2 - Contacts");
            System.out.println("3 - Edit Profile");
            System.out.println("4 - Save & Logout");
            System.out.print("Choice: ");
            String choice = scanner.next().trim();

            switch (choice) {
                case "1" -> chatListMenu();
                case "2" -> contactsMenu();
                case "3" -> editProfileMenu();
                case "4" -> {
                    FileManager.saveToFile(allUsers, SAVE_FILE);
                    System.out.println("Saved. Goodbye, " + currentUser.getUsername() + "!");
                    currentUser = null;
                    running = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void chatListMenu() {
        ArrayList<Chat> active = getActiveChats();

        if (active.isEmpty()) {
            System.out.println("You have no chats yet.");
            newChatPrompt();
            return;
        }

        System.out.println();
        System.out.println("=== Your Chats ===");
        for (int i = 0; i < active.size(); i++) {
            Chat c = active.get(i);


            StringBuilder label = new StringBuilder();
            for (User p : c.getParticipants()) {
                if (!p.getUserId().equals(currentUser.getUserId())) {
                    if (label.length() > 0) label.append(", ");
                    // Show saved contact name if available, otherwise fall back to display name
                    String name = currentUser.getSavedNameFor(p.getUserId());
                    label.append(name != null ? name : p.getUsername());
                }
            }

            // Read indicator on last message
            String readIndicator = c.isLastMessageReadBy(currentUser.getUserId()) ? "[READ]" : "[UNREAD]";

            System.out.println((i + 1) + ". " + label + "  " + readIndicator);
        }

        System.out.println();
        System.out.println("0 - Back");
        System.out.println("N - New Chat");
        System.out.print("Select chat number (or N/0): ");
        String input = scanner.next().trim();

        if (input.equalsIgnoreCase("N")) {
            newChatMenu();
            return;
        }

        int sel;
        try {
            sel = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        if (sel == 0) return;
        if (sel < 1 || sel > active.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        openChatMenu(active.get(sel - 1));
    }

    private static void newChatPrompt() {
        System.out.print("Would you like to start a new chat? (y/n): ");
        String ans = scanner.next().trim();
        if (ans.equalsIgnoreCase("y")) newChatMenu();
    }

    private static void openChatMenu(Chat chat) {
        // Mark all messages as read when opening the chat
        chat.markAllReadBy(currentUser.getUserId());
        FileManager.saveToFile(allUsers, SAVE_FILE);

        boolean running = true;
        while (running) {
            System.out.println();
            chat.displayChat();
            System.out.println();
            System.out.println("1 - Send Message");
            System.out.println("2 - Interact with a Message");
            System.out.println("3 - Delete this Chat");
            System.out.println("0 - Back");
            System.out.print("Choice: ");
            String choice = scanner.next().trim();

            switch (choice) {
                case "1" -> {
                    chat.addMessage(currentUser);
                    FileManager.saveToFile(allUsers, SAVE_FILE);
                }
                case "2" -> {
                    Message selected = chat.selectMessage(currentUser);
                    if (selected != null) {
                        chat.interactWithMessage(currentUser, selected);
                        FileManager.saveToFile(allUsers, SAVE_FILE);
                    }
                }
                case "3" -> {
                    System.out.print("Are you sure? (y/n): ");
                    if (scanner.next().trim().equalsIgnoreCase("y")) {
                        chat.deleteChat();
                        FileManager.saveToFile(allUsers, SAVE_FILE);
                        System.out.println("Chat deleted.");
                        running = false;
                    }
                }
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void newChatMenu() {
        scanner.nextLine();
        System.out.print("Enter the User ID of the person to chat with: ");
        String otherId = scanner.nextLine().trim();

        User other = findUserById(otherId);
        if (other == null) {
            System.out.println("No user found with that ID.");
            return;
        }
        if (other.getUserId().equals(currentUser.getUserId())) {
            System.out.println("You cannot start a chat with yourself.");
            return;
        }


        Chat existingChat = null;
        for (Chat c : currentUser.getChats()) {
            if (!c.getIsDeleted() && c.getParticipants().size() == 2
                    && c.getParticipants().contains(other)) {
                existingChat = c;
                break;
            }
        }

        if (existingChat != null) {
            System.out.println("Chat with " + other.getUsername() + " already exists. Opening it.");
            openChatMenu(existingChat);
            return;
        }

        ArrayList<User> participants = new ArrayList<>();
        participants.add(currentUser);
        participants.add(other);

        Chat chat = new Chat(null, participants, scanner);
        for (User p : participants) p.addChat(chat);

        FileManager.saveToFile(allUsers, SAVE_FILE);
        System.out.println("Chat started with " + other.getUsername() + "!");
        openChatMenu(chat);
    }


    private static void contactsMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("=== Contacts ===");
            System.out.println("1 - View contacts (A-Z)");
            System.out.println("2 - Add contact");
            System.out.println("3 - Remove contact");
            System.out.println("0 - Back");
            System.out.print("Choice: ");
            String choice = scanner.next().trim();

            switch (choice) {
                case "1" -> currentUser.printContactsAlphabetical();
                case "2" -> addContact();
                case "3" -> removeContact();
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addContact() {
        scanner.nextLine();
        System.out.print("Enter the User ID to add: ");
        String contactId = scanner.nextLine().trim();
        System.out.print("Enter a name to save them as: ");
        String savedName = scanner.nextLine().trim();

        if (savedName.isBlank()) {
            System.out.println("Name cannot be blank.");
            return;
        }

        currentUser.addContactById(contactId, savedName);
        FileManager.saveToFile(allUsers, SAVE_FILE);
        System.out.println("Contact \"" + savedName + "\" saved.");
    }

    private static void removeContact() {
        // Show current contacts with their saved names
        Map<String, String> contactMap = currentUser.getContactMap();
        if (contactMap.isEmpty()) {
            System.out.println("No contacts to remove.");
            return;
        }

        ArrayList<String> ids = new ArrayList<>(contactMap.keySet());
        System.out.println("Your contacts:");
        for (int i = 0; i < ids.size(); i++) {
            System.out.println((i + 1) + ". " + contactMap.get(ids.get(i)) + " (" + ids.get(i) + ")");
        }
        System.out.println("0 - Cancel");
        System.out.print("Select contact to remove: ");
        int sel = safeNextInt();
        if (sel == 0 || sel < 1 || sel > ids.size()) return;

        currentUser.removeContactById(ids.get(sel - 1));
        FileManager.saveToFile(allUsers, SAVE_FILE);
        System.out.println("Contact removed.");
    }

   // edit profile

    private static void editProfileMenu() {
        scanner.nextLine();
        System.out.println();
        currentUser.printProfile();
        System.out.println();
        System.out.println("Press Enter to keep current value.");

        System.out.print("New name: ");
        String name = scanner.nextLine().trim();

        System.out.print("New phone: ");
        String phone = scanner.nextLine().trim();

        System.out.print("New status: ");
        String status = scanner.nextLine();

        currentUser.editProfile(
                name.isBlank()   ? null : name,
                phone.isBlank()  ? null : phone,
                status.isBlank() ? null : status
        );

        FileManager.saveToFile(allUsers, SAVE_FILE);
        System.out.println("Profile updated.");
        currentUser.printProfile();
    }


    private static ArrayList<Chat> getActiveChats() {
        ArrayList<Chat> active = new ArrayList<>();
        for (Chat c : currentUser.getChats()) {
            if (!c.getIsDeleted()) active.add(c);
        }
        return active;
    }

    private static User findUserById(String id) {
        for (User u : allUsers) {
            if (u.getUserId().equals(id)) return u;
        }
        return null;
    }

    private static int safeNextInt() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }
}
