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
    private ArrayList<Set> searchResults;
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
    
    private ArrayList<Set> textSearch(ArrayList<String> searchTerms)
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
        
        //Matching results ArrayList for chats
        ArrayList<Chat> matching = new ArrayList();
        ArrayList<Message> matchingMessage = new ArrayList();
        
        //Hashsets for Adding Successful Matches to hashsets for duplicate checking.
        Set<Chat> seenChat = new HashSet<>();
        Set<Message> seenMessage = new HashSet<>();
        
        //Combine Results into ArrayList to be returned to main function
        ArrayList<Set> searchResults = new ArrayList();
        
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
        
        searchResults.add(seenChat);
        searchResults.add(seenMessage);
        
        //Return Arraylist of search results
        return searchResults;
    }
    
    public void displayResults(ArrayList<Set> results)
    {
        
    }
}
