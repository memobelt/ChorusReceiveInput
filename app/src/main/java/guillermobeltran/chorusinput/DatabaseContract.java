package guillermobeltran.chorusinput;

import android.provider.BaseColumns;

/**
 * Created by Memo on 6/24/15 to make sure all data names line up correctly.
 */
public final class DatabaseContract {
    public DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class DatabaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_chats";
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_ROLE = "role";
        public static final String COLUMN_NAME_SIZE = "size";
        public static final String COLUMN_NAME_MSG = "message";
        public static final String COLUMN_NAME_CHATID = "chatid";
    }
}
