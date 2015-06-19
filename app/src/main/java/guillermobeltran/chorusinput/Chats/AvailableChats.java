package guillermobeltran.chorusinput.Chats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
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
        // Add 3 sample items.
        for(int i = 1; i<21; i++){
            addItem(new ChatNumber(Integer.toString(i),"Chat "+ Integer.toString(i)));
        }
//        addItem(new ChatNumber("1", "Chat 1"));
//        addItem(new ChatNumber("2", "Chat 2"));
//        addItem(new ChatNumber("3", "Chat 3"));
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
