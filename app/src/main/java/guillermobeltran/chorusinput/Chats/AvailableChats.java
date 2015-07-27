package guillermobeltran.chorusinput.Chats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is where the chat numbers are made. Only 1-20. Should replace with actual chats the user
 * can answer. Probably need server cooperation.
 * TODO: Get appropriate chat numbers rather than 1-20.
 */
public class AvailableChats {

    /**
     * An array of sample (dummy) items.
     */
    public static List<ChatNumber> ITEMS = new ArrayList<ChatNumber>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, ChatNumber> ITEM_MAP = new HashMap<String, ChatNumber>();

    static {
        for(int i = 1; i<21; i++){
            addItem(new ChatNumber(Integer.toString(i),"Chat "+ Integer.toString(i)));
        }
    }

    private static void addItem(ChatNumber item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class ChatNumber {
        public String id;
        public String content;

        public ChatNumber(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
