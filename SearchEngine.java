//Import neccessary classes
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
/**
 * Class to Handle Searching chats, designed to handle wildcard searches
 * and jump to specific matching messages
 *
 * @author Jamie Hodge
 * @version 1.0
 */
public class SearchEngine
{
    //Class Fields
    private ArrayList<Chat> chats;
    private final int size;
    
    public SearchEngine(ArrayList<Chat> chatLog)
    {
        chats = chatLog;
        size = chats.size();
    }
    
    public void searchFunction()
    {
        //Local Variables
        boolean complete = false;
        ArrayList<String> keywords = new ArrayList<String>();
        Scanner s = new Scanner(System.in);
        ArrayList<Chat> searchResults;
        
        //Method Execution
        System.out.println("This is the search function, Wildcards like '_' or '*' are supported");
        System.out.println("Wrap your search term in *_ if you want to search for messages that contain your keyword");
        System.out.println("Otherwise, the search function will look for messages that match your text input exactly");
        System.out.println("Each Entry will be treated as a seperate keyword");
        do
        {
            keywords.add(getKeyWord());
            System.out.println("Do you want to add more KeyWords?");
            System.out.println("Type 'Y'/'N' to indicate");
            String temp = s.nextLine();
            temp = temp.toUpperCase(); //convert temp to uppercase to make input case insensitive
            if (temp.equals('Y'))
            {
                complete = true;
            }
        } while(!complete);
        searchResults = textSearch(keywords);
        displayResults(searchResults);
    }
    
    private String getKeyWord()
    {
        //Local Variables
        Scanner s = new Scanner(System.in);
        String input;
        
        //Method Execution
        System.out.println("This is the search function, Wildcards like '_' or '*' are supported");
        System.out.println("Wrap your search term in *_ if you want to search for messages that contain your keyword");
        System.out.println("Otherwise, the search function will look for messages that match your text input exactly");
        System.out.println("Please enter the text/words you want to search for: ");
        input = s.nextLine();
        
        //Return Value
        return input;
    }
    
    private ArrayList<Chat> textSearch(ArrayList<String> searchTerms)
    {
        //Local Variables
        
        //Passed Variables
        ArrayList<String> keywords = searchTerms;
        int searchNum = keywords.size();
        
        //Variables to store references to the object currently being searched through
        Chat currentChat;
        Message currentMessage;
        LinkedList<Message> currentMessageList;
        Iterator<Message> iterator;
        
        
        //Hashsets for Adding Successful Matches to hashsets for duplicate checking.
        Set<Chat> seenChat = new HashSet<>();
        Set<Message> seenMessage = new HashSet<>();
        
        //Convert Results into ArrayList to be returned to main function
        ArrayList<Chat> searchResults = new ArrayList();
        
        //Method Execution
        
        for (int j = 0; j < size; j++)
        {
            currentChat = chats.get(j);
            currentMessageList = currentChat.getMessages();
            iterator = currentMessageList.iterator();
            while (iterator.hasNext())
            {
                currentMessage = iterator.next();
                String content = currentMessage.getContent();
                for (String term : keywords)
                {
                    if (content.matches(term))
                    {
                        seenChat.add(currentChat);
                        seenMessage.add(currentMessage);
                    }
                }
            }
        }
        
        for (Chat ele: seenChat)
        {
            searchResults.add(ele);
        }
        
        //Return Arraylist of search results
        return searchResults;
    }
    
    private void displayResults(ArrayList<Chat> results)
    {
        //Local Variables
        ArrayList<Chat> chat = new ArrayList();
        chat = results;
        Scanner s = new Scanner(System.in);
        int userInput;
        String userStringInput;
        Chat currentWorkingChat;
        boolean returnToResults = false;
        
        //Method Execution
        System.out.println("Search Results");
        do
        {
            int counter = 0;
            userInput = 0;
            if (chat.size() == 0)
            {
                System.out.println("No search results found, try a more generic keyword");
            }
            else
            {
                for (Chat chatResult : chat)
                {
                  System.out.println(counter++ + ". + Chat ID: " + chatResult.getChatID());
                  System.out.println();
                  counter++;
                }
                System.out.println("Please type the number of the chat you'd like to open, otherwise program will exit");
                System.out.println("Between 1 and " + counter++ + "invalid submission will cause the program to prompt for resubmission: ");
                do
                {
                    userInput = s.nextInt();
                    s.nextLine();
                } while(userInput > 0 && userInput < counter--);
            }
            userInput--;
            currentWorkingChat = chat.get(userInput);
            currentWorkingChat.displayChat();
            System.out.println();
            do
            {
            System.out.println("Would you like to terminate program? type 'Y'/'N' to indicate: ");
            userStringInput = s.nextLine();
            userStringInput.toUpperCase();
            } while (userStringInput != "Y" || userStringInput != "N");
            if (userStringInput.equals("Y"))
            {
                returnToResults = true;
            }
            else if (userStringInput.equals("N"))
            {
                returnToResults = false;
            }
        } while(returnToResults);
    }
}
